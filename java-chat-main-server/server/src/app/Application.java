package app;

import dao.ChatDao;
import domain.ChatRoom;
import service.ChatService;
import thread.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Application {

    public static List<Socket> sockets = new ArrayList<>();

    ChatDao chatDao = new ChatDao();
    ChatService chatService = new ChatService(chatDao);

    public Application() {

        // ✅ 서버 시작 시 DB에서 채팅방 불러오기
        List<String> roomNames = chatDao.getChatRoomsFromDB();
        for (String name : roomNames) {
            chatDao.addChatRoom(new ChatRoom(name));
        }
        System.out.println("✅ DB 채팅방 로드 완료: " + roomNames);

        Socket clientSocket = null;

        try(ServerSocket serverSocket = new ServerSocket(9090)) {
            System.out.println("✅ 서버가 성공적으로 시작되었습니다. (포트: 9090)");

            while (true) {
                System.out.println("접속 대기중...");
                clientSocket = serverSocket.accept();
                System.out.println("client IP: " + clientSocket.getInetAddress() + " Port: " + clientSocket.getPort());

                sockets.add(clientSocket);

                ServerThread thread = new ServerThread(clientSocket, chatService);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
