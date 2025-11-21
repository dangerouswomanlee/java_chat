package domain;

import dto.request.LoginRequest;
import java.net.Socket;
import java.util.Date;

public class User {

    private Socket socket;
    private String id;
    private String name;
    private String password; // ✅ 추가
    private Date createdAt;

    public User(Socket socket, LoginRequest req) {
        this.socket = socket;
        this.id = req.getId();
        this.name = req.getName();
        this.password = req.getPassword(); // ✅ 추가
        this.createdAt = new Date();
    }

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.createdAt = new Date();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public Date getCreatedAt() { return createdAt; }
    public Socket getSocket() { return socket; }
    public void setSocket(Socket socket) { this.socket = socket; }

    public String getEnterString() { return "[" + name + "]님이 입장했습니다."; }
    public String getExitString() { return "[" + name + "]님이 퇴장했습니다."; }
}
