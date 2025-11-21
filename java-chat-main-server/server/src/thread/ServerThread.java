package thread;

import app.Application;
import dao.ChatDao;
import domain.ChatRoom;
import domain.User;
import dto.response.DTO;
import dto.request.*;
import dto.response.*;
import dto.type.DtoType;
import dto.type.MessageType;
import exception.ChatRoomExistException;
import exception.ChatRoomNotFoundException;
import exception.UserNotFoundException;
import service.ChatService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ServerThread extends Thread {

    private Socket socket;
    private ChatService chatService;
    private ChatDao chatDao = new ChatDao();

    public ServerThread(Socket socket, ChatService chatService) {
        this.socket = socket;
        this.chatService = chatService;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String str = reader.readLine();

                if (str == null) {
                    socket.close();
                    Application.sockets.remove(socket);
                    return;
                }

                String[] token = str.split(":", 2);
                if (token.length < 2) continue;

                DtoType type = DtoType.valueOf(token[0]);
                String message = token[1];

                processReceiveMessage(type, message);
                Thread.sleep(200);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processReceiveMessage(DtoType type, String message)
            throws UserNotFoundException, ChatRoomNotFoundException, ChatRoomExistException, IOException {

        switch (type) {

            // ✅ 로그인 처리
            case LOGIN:
                LoginRequest loginReq = new LoginRequest(message);
                User user = chatService.login(loginReq.getUserId(), loginReq.getPassword());

                user.setSocket(socket);
                chatService.enterLobby(user);

                InitDataResponse initRes = new InitDataResponse(
                        chatService.getChatRooms(),
                        chatService.getUsers()
                );
                sendMessageToUser(initRes, socket);

                MessageResponse lobbyEnterMsg = new MessageResponse(
                        MessageType.ENTER,
                        ChatDao.LOBBY_CHAT_NAME,
                        user.getName(),
                        user.getEnterString()
                );
                broadcastToLobby(lobbyEnterMsg);

                UserListResponse lobbyUserList = new UserListResponse(
                        ChatDao.LOBBY_CHAT_NAME,
                        chatService.getUsers()
                );
                broadcastToLobby(lobbyUserList);
                break;

            // ✅ 메시지 처리
            case MESSAGE:
                MessageResponse req = new MessageResponse(message);
                User sender = chatService.findUserBySocket(socket); // ✅ 수정!

                // DB 저장
                chatDao.saveChatLog(req.getChatRoomName(), sender.getId(), req.getMessage());

                // 이름만 보내기
                MessageResponse chatRes = new MessageResponse(
                        MessageType.CHAT,
                        req.getChatRoomName(),
                        sender.getName(),
                        req.getMessage()
                );

                ChatRoom msgRoom = chatService.getChatRoom(req.getChatRoomName());
                if (msgRoom != null) {
                    for (User u : msgRoom.getUsers()) {
                        if (u.getSocket() != null && u.getSocket() != socket) {
                            sendMessageToUser(chatRes, u.getSocket());
                        }
                    }
                }
                break;


            // ✅ 채팅방 생성
            case CREATE_CHAT:
                CreateChatRoomRequest createReq = new CreateChatRoomRequest(message);
                ChatRoom newRoom = chatService.createChatRoom(createReq.getName(), createReq.getUserId());
                chatService.enterChatRoom(newRoom.getName(), createReq.getUserId());

                chatService.saveChatRoom(newRoom.getName());

                broadcastToAll(new CreateChatRoomResponse(newRoom));
                broadcastToAll(new UserListResponse(newRoom.getName(), chatService.getChatRoomUsers(newRoom.getName())));
                break;

            // ✅ 채팅방 입장
            case ENTER_CHAT:
                EnterChatRequest enterReq = new EnterChatRequest(message);
                chatService.enterChatRoom(enterReq.getChatRoomName(), enterReq.getUserId());
                User enterUser = chatService.getUser(enterReq.getUserId());

                MessageResponse enterMsg = new MessageResponse(
                        MessageType.ENTER,
                        enterReq.getChatRoomName(),
                        enterUser.getName(),
                        enterUser.getEnterString()
                );
                broadcastToRoom(enterReq.getChatRoomName(), enterMsg);

                UserListResponse enterList = new UserListResponse(
                        enterReq.getChatRoomName(),
                        chatService.getChatRoomUsers(enterReq.getChatRoomName())
                );
                broadcastToRoom(enterReq.getChatRoomName(), enterList);

                // ✅ DB 채팅기록 로드 (닉네임만)
                List<String> oldMessages = chatService.getChatLogs(enterReq.getChatRoomName());
                for (String log : oldMessages) {
                    String[] parts = log.split(":", 2);
                    if (parts.length == 2) {
                        String oldUserName = parts[0];
                        String oldMsg = parts[1];

                        MessageResponse oldMsgRes = new MessageResponse(
                                MessageType.CHAT,
                                enterReq.getChatRoomName(),
                                oldUserName,
                                oldMsg
                        );
                        sendMessageToUser(oldMsgRes, socket);
                    }
                }
                break;

            // ✅ 채팅방 퇴장
            case EXIT_CHAT:
                ExitChatRequest exitReq = new ExitChatRequest(message);
                ChatRoom exitRoom = chatService.getChatRoom(exitReq.getChatRoomName());
                User exitUser = chatService.exitChatRoom(exitReq.getChatRoomName(), exitReq.getUserId());

                if (exitRoom.ieExistUser()) {
                    MessageResponse exitMsg = new MessageResponse(
                            MessageType.EXIT,
                            exitReq.getChatRoomName(),
                            exitUser.getName(),
                            exitUser.getExitString()
                    );
                    broadcastToRoom(exitReq.getChatRoomName(), exitMsg);

                    UserListResponse exitList = new UserListResponse(
                            exitReq.getChatRoomName(),
                            chatService.getChatRoomUsers(exitReq.getChatRoomName())
                    );
                    broadcastToRoom(exitReq.getChatRoomName(), exitList);
                }
                break;
        }
    }

    private void sendMessageToUser(DTO dto, Socket targetSocket) throws IOException {
        PrintWriter sender = new PrintWriter(targetSocket.getOutputStream());
        sender.println(dto);
        sender.flush();
    }

    private void broadcastToLobby(DTO dto) throws IOException {
        ChatRoom lobby = chatService.getChatRoom(ChatDao.LOBBY_CHAT_NAME);
        if (lobby != null) {
            for (User u : lobby.getUsers()) {
                if (u.getSocket() != null) sendMessageToUser(dto, u.getSocket());
            }
        }
    }

    private void broadcastToRoom(String room, DTO dto) throws IOException {
        ChatRoom chatRoom = chatService.getChatRoom(room);
        if (chatRoom != null) {
            for (User u : chatRoom.getUsers()) {
                if (u.getSocket() != null) sendMessageToUser(dto, u.getSocket());
            }
        }
    }

    private void broadcastToAll(DTO dto) throws IOException {
        for (Socket s : Application.sockets) {
            sendMessageToUser(dto, s);
        }
    }
}
