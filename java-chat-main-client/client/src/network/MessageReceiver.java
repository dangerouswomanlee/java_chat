package network;

import app.Application;
import domain.ChatRoom;
import dto.response.*;
import dto.type.DtoType;
import view.frame.LobbyFrame;
import view.panel.ChatPanel;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class MessageReceiver extends Thread {

    private final Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"))
        ) {
            while (true) {
                String str = reader.readLine();

                if (str == null) {
                    System.out.println("⚠ 클라이언트 연결이 끊어졌습니다.");
                    closeSocket();
                    return;
                }

                System.out.println("📩 수신 원본: " + str);
                String[] token = str.split(":", 2);

                if (token.length < 2) {
                    System.out.println("❌ 잘못된 메시지 포맷: " + str);
                    continue;
                }

                DtoType type;
                try {
                    type = DtoType.valueOf(token[0]);
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ 알 수 없는 DtoType: " + token[0]);
                    continue;
                }

                String message = token[1];
                processReceivedMessage(type, message);
                Thread.sleep(100);
            }

        } catch (IOException e) {
            System.out.println("❌ 소켓 입력 스트림 오류: " + e.getMessage());
            closeSocket();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processReceivedMessage(DtoType type, String message) {
        System.out.println("🟢 [수신된 메시지] type=" + type + ", message=" + message);

        switch (type) {

            case LOGIN_FAIL:
                JOptionPane.showMessageDialog(null, "로그인 실패: " + message);
                break;

            case LOGIN:
                InitResponse initRes = new InitResponse(message);

                // ✅ 서버에서 받은 유저, 방 목록 적용
                Application.chatRooms = initRes.getChatRooms();
                Application.users = initRes.getUsers();

                // ✅ 로그인한 사용자 정보 정확히 설정
                Application.me = Application.users.stream()
                        .filter(u -> u.getId().equals(Application.userId))
                        .findFirst()
                        .orElse(null);

                // 로비 UI 갱신
                LobbyFrame.chatRoomUserListPanel.paintChatUsers(Application.users);
                LobbyFrame.chatRoomListPanel.paintChatRoomList();

                System.out.println("✅ 로그인 성공");
                System.out.println("내 정보: " + Application.me.getName());
                break;

            case MESSAGE:
                MessageResponse messageRes = new MessageResponse(message);
                String roomName = messageRes.getChatRoomName();

                System.out.println("💬 [" + roomName + "] "
                        + messageRes.getUserName() + ": " + messageRes.getMessage());

                ChatPanel chatPanel = Application.chatPanelMap.get(roomName);

                if (chatPanel == null) {
                    if (roomName.equals(Application.LOBBY_CHAT_NAME)) {
                        chatPanel = Application.chatPanelMap.get(Application.LOBBY_CHAT_NAME);
                    } else {
                        ChatPanel newPanel = new ChatPanel(roomName);
                        Application.chatPanelMap.put(roomName, newPanel);

                        LobbyFrame lobbyFrame = Application.lobbyFrame;
                        lobbyFrame.add(newPanel);
                        newPanel.setBounds(10, 10, 400, 500);
                        lobbyFrame.repaint();

                        chatPanel = newPanel;
                    }
                }

                boolean isMe = messageRes.getUserName().equals(Application.me.getName());

                chatPanel.addBubble(
                        messageRes.getUserName(),
                        messageRes.getMessage(),
                        isMe
                );
                break;

            case CREATE_CHAT:
                CreateChatRoomResponse createChatRoomResponse = new CreateChatRoomResponse(message);
                String chatRoomName = createChatRoomResponse.getName();

                ChatRoom newChatRoom = new ChatRoom(chatRoomName);
                Application.chatRooms.add(newChatRoom);

                LobbyFrame.chatRoomListPanel.addChatRoomLabel(chatRoomName);
                break;

            case USER_LIST:
                UserListResponse userListRes = new UserListResponse(message);
                if (Application.chatRoomUserListPanelMap.get(userListRes.getChatRoomName()) != null) {
                    Application.chatRoomUserListPanelMap.get(userListRes.getChatRoomName())
                            .paintChatUsers(userListRes.getUsers());
                }
                break;

            case CHAT_ROOM_LIST:
                ChatRoomListResponse chatRoomListRes = new ChatRoomListResponse(message);
                Application.chatRooms = chatRoomListRes.getChatRooms();
                LobbyFrame.chatRoomListPanel.paintChatRoomList();
                break;
        }
    }

    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("🔌 소켓 종료");
            }
        } catch (IOException e) {
            System.out.println("❌ 소켓 종료 중 오류");
        }
    }
}
