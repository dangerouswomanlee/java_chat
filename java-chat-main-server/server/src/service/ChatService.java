package service;

import app.Application;
import dao.ChatDao;
import domain.ChatRoom;
import domain.User;
import exception.ChatRoomExistException;
import exception.ChatRoomNotFoundException;
import exception.UserNotFoundException;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class ChatService {

	private ChatDao chatDao;

	public ChatService(ChatDao chatDao) {
		this.chatDao = chatDao;
	}

	// ✅ 로그인 (DB 조회 기반)
	public User login(String userId, String password) throws UserNotFoundException {
	    System.out.println("🔍 로그인 시도: " + userId);

	    // 1️⃣ DB 조회 시작
	    System.out.println("🧩 DB에서 사용자 검색 중...");
	    Optional<User> optionalUser = chatDao.findRegisteredUserById(userId);

	    if (optionalUser.isEmpty()) {
	        System.out.println("❌ DB에서 사용자 찾지 못함: " + userId);
	        throw new UserNotFoundException(userId);
	    }

	    User user = optionalUser.get();
	    System.out.println("✅ DB에서 사용자 찾음: ID = " + user.getId() + ", 이름 = " + user.getName());

	    // 2️⃣ 비밀번호 검증
	    if (!user.getPassword().equals(password)) {
	        System.out.println("❌ 비밀번호 불일치");
	        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
	    }

	    // 3️⃣ 이미 접속 중인지 확인
	    Optional<User> existingUser = chatDao.findUserById(userId);
	    if (existingUser.isPresent()) {
	        System.out.println("⚠ 이미 접속 중인 사용자: " + userId);
	        return existingUser.get();
	    }

	    // 4️⃣ 접속 목록에 추가
	    chatDao.addUser(user);
	    System.out.println("✅ 로그인 성공 및 접속 목록에 추가됨: " + userId);

	    return user;
	}


	// ✅ 사용자 추가 (메모리에만)
	public void addUser(User user) {
		chatDao.addUser(user);
	}

	// ✅ 로비 입장
	public void enterLobby(User user) {
		chatDao.getLobby().addUser(user);
	}

	// ✅ 채팅방 입장
	public void enterChatRoom(String chatRoomName, String userId)
			throws UserNotFoundException, ChatRoomNotFoundException {

		// 현재 접속 중 사용자 확인 (없으면 DB 재조회)
		User user = getUser(userId);
		if (user == null) {
			throw new UserNotFoundException(userId);
		}

		// 채팅방 확인
		ChatRoom room = getChatRoom(chatRoomName);
		if (room == null) {
			throw new ChatRoomNotFoundException(chatRoomName);
		}

		room.addUser(user);
		System.out.println("✅ " + userId + " 님이 " + chatRoomName + " 방에 입장했습니다.");
	}

	// ✅ 채팅방 생성
	public ChatRoom createChatRoom(String chatRoomName, String userId) throws ChatRoomExistException {

		Optional<ChatRoom> findChatRoom = chatDao.getChatRooms().stream()
				.filter(room -> room.getName().equals(chatRoomName)).findAny();

		if (findChatRoom.isEmpty()) {
			ChatRoom chatRoom = new ChatRoom(chatRoomName);
			chatDao.addChatRoom(chatRoom);
			System.out.println("✅ 새 채팅방 생성: " + chatRoomName);
			return chatRoom;
		} else {
			throw new ChatRoomExistException(chatRoomName);
		}
	}

	// ✅ 채팅방 나가기
	public User exitChatRoom(String chatRoomName, String userId)
			throws UserNotFoundException, ChatRoomNotFoundException {

		ChatRoom chatRoom = getChatRoom(chatRoomName);
		if (chatRoom == null)
			throw new ChatRoomNotFoundException(chatRoomName);

		User user = getUser(userId);
		if (user == null)
			throw new UserNotFoundException(userId);

		chatRoom.removeUser(user);
		System.out.println("👋 " + userId + " 님이 " + chatRoomName + " 방에서 퇴장했습니다.");

		if (!chatRoom.ieExistUser()) {
			chatDao.getChatRooms().remove(chatRoom);
			System.out.println("⚠ " + chatRoomName + " 방에 유저가 없어 삭제됨");
		}

		return user;
	}

	// ✅ 전체 사용자 목록
	public List<User> getUsers() {
		return chatDao.getUsers();
	}

	// ✅ 전체 채팅방 목록
	public List<ChatRoom> getChatRooms() {
		return chatDao.getChatRooms();
	}

	// ✅ 사용자 조회 (메모리 → DB 순서로)
	public User getUser(String userId) {
		// 1️⃣ 메모리에서 먼저 찾기
		Optional<User> findUser = chatDao.findUserById(userId);
		if (findUser.isPresent()) {
			return findUser.get();
		}

		// 2️⃣ DB에서 재조회 (로그인 안 된 상태에서도 접근 가능)
		Optional<User> dbUser = chatDao.findRegisteredUserById(userId);
		if (dbUser.isPresent()) {
			chatDao.addUser(dbUser.get());
			System.out.println("✅ DB에서 유저 불러옴: " + userId);
			return dbUser.get();
		}

		System.out.println("❌ [" + userId + "] 사용자 없음");
		return null;
	}

	// ✅ 채팅방 조회
	public ChatRoom getChatRoom(String chatRoomName) {
		if (chatRoomName.equals(ChatDao.LOBBY_CHAT_NAME)) {
			return chatDao.getLobby();
		}

		Optional<ChatRoom> findChatRoom = chatDao.findChatRoomByName(chatRoomName);
		return findChatRoom.orElse(null);
	}

	// ✅ 채팅방 내 사용자 조회
	public List<User> getChatRoomUsers(String chatRoomName) {
		ChatRoom chatRoom = getChatRoom(chatRoomName);
		return (chatRoom != null) ? chatRoom.getUsers() : null;
	}

	// ✅ 유저 연결 해제
	public void disconnect(String userId) throws UserNotFoundException, IOException {
		Optional<User> findUser = chatDao.getUser(userId);
		if (findUser.isEmpty())
			throw new UserNotFoundException(userId);

		User user = findUser.get();

		// 모든 채팅방에서 제거
		chatDao.getChatRooms().forEach(room -> room.removeUser(user));

		// 전체 유저 리스트에서 제거
		chatDao.getUsers().remove(user);

		// 소켓 닫기
		Socket clientSocket = user.getSocket();
		if (clientSocket != null && !clientSocket.isClosed()) {
			clientSocket.close();
		}
		Application.sockets.remove(clientSocket);

		System.out.println("🔌 사용자 연결 해제: " + userId);
	}
	// ✅ 채팅 메시지 저장 (서버에서 호출)
	public void saveMessage(String roomName, String userId, String message) {
	    chatDao.saveChatLog(roomName, userId, message);
	}

	public void saveChatRoom(String roomName) {
	    chatDao.saveChatRoom(roomName);
	}
	public List<String> getChatLogs(String roomName) {
	    return chatDao.getChatLogs(roomName);
	}
	public User findUserBySocket(Socket socket) {
	    return chatDao.getUsers().stream()
	        .filter(u -> u.getSocket() == socket)
	        .findFirst()
	        .orElse(null);
	}





}
