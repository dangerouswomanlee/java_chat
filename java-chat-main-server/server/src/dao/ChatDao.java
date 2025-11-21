package dao;

import domain.ChatRoom;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatDao {

	// ✅ Oracle DB 연결 정보
	private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE?useUnicode=true&characterEncoding=UTF-8";
	private static final String USER = "chaeyeon";
	private static final String PASSWORD = "1234";

	public static final String LOBBY_CHAT_NAME = "LOBBY";

	// ✅ 메모리 내 데이터 (현재 접속 중인 사용자 / 채팅방 목록)
	private List<User> users = new ArrayList<>();
	private List<ChatRoom> chatRooms = new ArrayList<>();
	private ChatRoom lobby = new ChatRoom(LOBBY_CHAT_NAME);

	// ✅ DB에서 등록된 사용자 조회 (로그인용)
	public Optional<User> findRegisteredUserById(String userId) {
		String sql = "SELECT user_id, user_pw, user_name FROM signup WHERE user_id = ?"; // ✅ signup으로 확인

		System.out.println("🧩 실행할 SQL: " + sql); // ✅ 실행 전 로그
		System.out.println("🧩 검색할 user_id: " + userId); // ✅ 전달받은 값 확인

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, userId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String id = rs.getString("user_id");
				String pw = rs.getString("user_pw");
				String name = rs.getString("user_name");

				System.out.println("✅ DB 조회 성공 → id: " + id + ", name: " + name);
				return Optional.of(new User(id, pw, name));
			} else {
				System.out.println("❌ signup 테이블에 해당 아이디 없음: " + userId);
				return Optional.empty();
			}

		} catch (SQLException e) {
			System.out.println("❌ SQL 오류 발생!");
			e.printStackTrace();
		}

		return Optional.empty();
	}

	// ✅ 현재 접속 중인 사용자 추가
	public void addUser(User user) {
		users.add(user);
	}

	// ✅ 현재 접속 중 사용자 목록 반환
	public List<User> getUsers() {
		return users;
	}

	// ✅ ID로 현재 접속 중 사용자 찾기
	public Optional<User> findUserById(String userId) {
		return users.stream().filter(u -> u.getId().equals(userId)).findAny();
	}

	// ✅ ID로 현재 접속 중 사용자 직접 접근 (disconnect용)
	public Optional<User> getUser(String userId) {
		return findUserById(userId);
	}

	// ✅ 채팅방 목록 반환
	public List<ChatRoom> getChatRooms() {
		return chatRooms;
	}

	// ✅ 채팅방 추가
	public void addChatRoom(ChatRoom chatRoom) {
		chatRooms.add(chatRoom);
	}

	// ✅ 이름으로 채팅방 찾기
	public Optional<ChatRoom> findChatRoomByName(String name) {
		return chatRooms.stream().filter(room -> room.getName().equals(name)).findAny();
	}

	// ✅ 로비 채팅방 반환
	public ChatRoom getLobby() {
		return lobby;
	}

	// ✅ 채팅 로그 DB 저장 메서드 추가
	public void saveChatLog(String roomName, String userId, String message) {
		String sql = "INSERT INTO CHAT_LOG (ID, ROOM_NAME, USER_ID, MESSAGE) VALUES (CHAT_LOG_SEQ.NEXTVAL, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, roomName);
			pstmt.setString(2, userId);
			pstmt.setString(3, message);
			pstmt.executeUpdate();

			System.out.println("💾 채팅 저장 완료 → [" + roomName + "] " + userId + ": " + message);

		} catch (SQLException e) {
			System.out.println("❌ 채팅 로그 저장 실패");
			e.printStackTrace();
		}
	}

	public void saveChatRoom(String roomName) {
		String sql = "INSERT INTO CHAT_ROOM (ROOM_NAME) VALUES (?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, roomName);
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ✅ DB에서 채팅방 목록 불러오기
	public List<String> getChatRoomsFromDB() {
		List<String> roomNames = new ArrayList<>();
		String sql = "SELECT ROOM_NAME FROM CHAT_ROOM";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				roomNames.add(rs.getString("ROOM_NAME"));
			}
			System.out.println("📂 DB에서 채팅방 로드: " + roomNames);

		} catch (SQLException e) {
			System.out.println("❌ 채팅방 불러오기 실패");
			e.printStackTrace();
		}

		return roomNames;
	}

	public List<String> getChatLogs(String roomName) {
		List<String> chatLogs = new ArrayList<>();
		String sql = """
				SELECT s.user_name || ':' || c.message AS log
				FROM chat_log c
				JOIN signup s ON c.user_id = s.user_id
				WHERE c.room_name = ?
				ORDER BY c.id
				""";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, roomName);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				chatLogs.add(rs.getString("log"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return chatLogs;
	}

}
