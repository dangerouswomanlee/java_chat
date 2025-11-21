package dto.request;

public class LoginRequest {

    private String id;
    private String password;

    // message 형식 예: "user1,1234"
    public LoginRequest(String message) {
        String[] token = message.split(",");
        if (token.length >= 2) {
            this.id = token[0];
            this.password = token[1];
        } else {
            throw new IllegalArgumentException("잘못된 로그인 요청 형식입니다. (예: user1,1234)");
        }
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

	public String getName() {
		return null;
	}
}
