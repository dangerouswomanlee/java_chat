package dto.response;

import dto.type.MessageType;

public class MessageResponse {

	private MessageType messageType;
	private String chatRoomName;
	private String userName;
	private String message;

	public MessageResponse(String message) {
		// "MESSAGE:CHAT,room,user,msg" 형태 방어
		if (message.startsWith("MESSAGE:")) {
			message = message.substring("MESSAGE:".length());
		}

		String[] value = message.split(",", 4); // 4개만 split
		this.messageType = MessageType.valueOf(value[0]);
		this.chatRoomName = value[1];
		this.userName = value[2];
		this.message = value[3];
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getChatRoomName() {
		return chatRoomName;
	}

	public String getUserName() {
		return userName;
	}

	public String getMessage() {
		return message;
	}
}
