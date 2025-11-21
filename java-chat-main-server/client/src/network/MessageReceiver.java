package network;

import app.Application;
import domain.ChatRoom;
import dto.response.*;
import dto.type.DtoType;
import view.frame.LobbyFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReceiver extends Thread {

    Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        try {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str = reader.readLine();
                if (str == null) {
                    try {
                        socket.close();
                        System.out.println(Application.me.getName() + "'s socket is closed.");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("disconnect");
                    System.exit(1);
                }
                System.out.println(str);
                String[] token = str.split(":");
                DtoType type = DtoType.valueOf(token[0]);
                String message = token[1];

                processReceivedMessage(type, message);

                Thread.sleep(300);
            }
        }
        catch (Exception e) {
            try {
                System.out.println("socket error (can't get socket input stream)");
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    private void processReceivedMessage(DtoType type, String message) {
        System.out.println("📩 받은 메시지: " + message);

        switch (type) {

            case LOGIN:
                InitResponse initRes = new InitResponse(message);
                Application.chatRooms = initRes.getChatRooms();
                Application.users = initRes.getUsers();

                LobbyFrame.chatRoomUserListPanel.paintChatUsers(Application.users); // 전체 리스트
                LobbyFrame.chatRoomListPanel.paintChatRoomList();
                break;

            case MESSAGE:
                MessageResponse messageRes = new MessageResponse(message);

                String chatRoomName = messageRes.getChatRoomName();
                // ✅ 안전하게 ChatPanel 가져오기 (없으면 새로 생성)
                ChatPanel chatPanel = Application.chatPanelMap.get(chatRoomName);
                if (chatPanel == null) {
                    System.out.println("⚠ 채팅방 [" + chatRoomName + "] 패널이 존재하지 않아 새로 생성합니다.");
                    chatPanel = new ChatPanel(chatRoomName);

                    // 🔤 폰트 한글 깨짐 방지
                    chatPanel.setFont(new java.awt.Font("맑은 고딕", java.awt.Font.PLAIN, 14));

                    Application.chatPanelMap.put(chatRoomName, chatPanel);
                }

                // ✅ 메시지 추가
                chatPanel.addMessage(
                    messageRes.getMessageType(),
                    messageRes.getUserName(),
                    messageRes.getMessage()
                );
                break;

            case CREATE_CHAT:
                CreateChatRoomResponse createChatRoomResponse = new CreateChatRoomResponse(message);
                String newRoomName = createChatRoomResponse.getName();

                ChatRoom newChatRoom = new ChatRoom(newRoomName);
                Application.chatRooms.add(newChatRoom);

                LobbyFrame.chatRoomListPanel.addChatRoomLabel(newRoomName); // 새로 생성된 채팅방 추가
                break;

            case USER_LIST:
                UserListResponse userListRes = new UserListResponse(message);
                if (Application.chatRoomUserListPanelMap.get(userListRes.getChatRoomName()) != null) {
                    Application.chatRoomUserListPanelMap
                            .get(userListRes.getChatRoomName())
                            .paintChatUsers(userListRes.getUsers());
                } else {
                    System.out.println("⚠ USER_LIST 수신: 해당 채팅방 패널이 아직 존재하지 않습니다.");
                }
                break;

            case CHAT_ROOM_LIST:
                ChatRoomListResponse chatRoomListRes = new ChatRoomListResponse(message);
                Application.chatRooms = chatRoomListRes.getChatRooms();
                LobbyFrame.chatRoomListPanel.paintChatRoomList();
                break;
        }
    }

}
