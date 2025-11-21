package view.frame;

import app.Application;
import view.panel.ChatPanel;
import view.panel.ChatRoomListPanel;
import view.panel.ChatRoomUserListPanel;
import view.panel.MenuPanel;
import view.panel.WeatherPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LobbyFrame extends JFrame implements WindowListener {

    public static ChatPanel chatPanel;
    public static ChatRoomListPanel chatRoomListPanel;
    public static MenuPanel menuPanel;
    public static ChatRoomUserListPanel chatRoomUserListPanel;
    public static CreateChatFrame createChatFrame;

    public LobbyFrame() {
        super("Chat Chat");

        Application.lobbyFrame = this;

        // 로그인창 띄우기
        new LoginFrame(this);
        createChatFrame = new CreateChatFrame();

        setLayout(null);
        setSize(830, 550);

        // ✅ 배경색 통일 (파스텔 하늘색 느낌)
        Color bg = new Color(248, 254, 255);
        getContentPane().setBackground(bg);

        // ✅ 날씨 패널
        WeatherPanel weatherPanel = new WeatherPanel();
        weatherPanel.setBounds(600, 10, 200, 50);
        weatherPanel.setBackground(bg);
        add(weatherPanel);

        // ✅ ChatPanel
        chatPanel = new ChatPanel(Application.LOBBY_CHAT_NAME);
        chatPanel.setBounds(10, 10, 400, 500);
        chatPanel.setBackground(bg);
        add(chatPanel);

        // ✅ 사용자 목록 패널
        chatRoomUserListPanel = new ChatRoomUserListPanel(this);
        chatRoomUserListPanel.setBackground(bg);
        add(chatRoomUserListPanel);

        // ✅ 채팅방 목록 패널
        chatRoomListPanel = new ChatRoomListPanel(this);
        chatRoomListPanel.setBackground(bg);
        add(chatRoomListPanel);

        // ✅ 메뉴 패널
        menuPanel = new MenuPanel(this, Application.LOBBY_CHAT_NAME);
        menuPanel.setCreateChatBtnVisible(true);
        menuPanel.setBackground(bg);
        add(menuPanel);

        this.addWindowListener(this);
        setVisible(false);
    }

    public ChatPanel getChatPanel() { return chatPanel; }
    public ChatRoomUserListPanel getChatRoomUserListPanel() { return chatRoomUserListPanel; }

    @Override public void windowOpened(WindowEvent e) { }
    @Override public void windowClosing(WindowEvent e) { }
    @Override public void windowClosed(WindowEvent e) { }
    @Override public void windowIconified(WindowEvent e) { }
    @Override public void windowDeiconified(WindowEvent e) { }
    @Override public void windowActivated(WindowEvent e) { }
    @Override public void windowDeactivated(WindowEvent e) { }
}
