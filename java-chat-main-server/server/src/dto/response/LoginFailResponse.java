package dto.response;

import dto.type.DtoType;

public class LoginFailResponse extends DTO {

    private String message;

    public LoginFailResponse(String message) {
        super(DtoType.LOGIN_FAIL);
        this.message = message;
    }

    @Override
    public String toString() {
        return getType() + ":" + message;
    }
}
