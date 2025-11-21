package view.frame;

import view.panel.ChatPanel;
import view.panel.ChatRoomUserListPanel;
import view.panel.MenuPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ChatFrame extends JFrame implements WindowListener {

    String chatRoomName;

    ChatPanel chatPanel;

    ChatRoomUserListPanel chatRoomUserListPanel;

    MenuPanel menuPanel;

    public ChatFrame(String chatRoomName) {
        super(chatRoomName);

        setLayout(null);
        setSize(830, 550);
        
        getContentPane().setBackground(new java.awt.Color(242, 252, 255)); // 연한 하늘색

        this.chatRoomName = chatRoomName;

        // ✅ ChatPanel 수정된 생성자 사용
        chatPanel = new ChatPanel(chatRoomName);
        chatPanel.setBounds(10, 10, 400, 500);
        add(chatPanel);

        // ✅ 다른 패널은 this 전달 유지 (이 클래스는 프레임이 맞음)
        chatRoomUserListPanel = new ChatRoomUserListPanel(this);
        menuPanel = new MenuPanel(this, chatRoomName);
        menuPanel.setExitBtnVisible(true);

        addWindowListener(this);

        setVisible(true);
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    public ChatRoomUserListPanel getChatRoomUserListPanel() {
        return chatRoomUserListPanel;
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosing(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
}
