package dto.response;

import dto.type.DtoType;
import dto.type.MessageType;

public class MessageResponse extends DTO {

    private MessageType messageType;
    private String chatRoomName;
    private String userName;   // 화면 표시용
    private String message;

    // ✅ 수신용 파싱
    public MessageResponse(String payload) {
        super(DtoType.MESSAGE);

        String[] value = payload.split(",", 4);
        this.messageType  = MessageType.valueOf(value[0]);
        this.chatRoomName = value[1];
        this.userName     = value[2];
        this.message      = value[3];
    }

    // ✅ 송신용 생성자 (userId는 DB 다오에서 처리하므로 필요 없음)
    public MessageResponse(MessageType type, String roomName, String userName, String message) {
        super(DtoType.MESSAGE);
        this.messageType = type;
        this.chatRoomName = roomName;
        this.userName = userName;
        this.message = message;
    }


    public MessageType getMessageType() { return messageType; }
    public String getChatRoomName() { return chatRoomName; }
    public String getUserName() { return userName; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return super.toString()
                + messageType + ","
                + chatRoomName + ","
                + userName + ","
                + message;
    }
}
