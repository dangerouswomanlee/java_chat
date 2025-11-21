package view.panel;

import app.Application;
import dto.request.ExitChatRequest;
import view.frame.LobbyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {

    String chatRoomName;

    JButton createChatBtn = new JButton("새로운 채팅방 생성");
    JButton exitBtn = new JButton("나가기");

    public MenuPanel(JFrame frame, String chatRoomName) {
        this.chatRoomName = chatRoomName;
        setLayout(null);

        // ✅ 전체 배경 ChatPanel과 톤 맞춤
        setBackground(new Color(245, 252, 255));

        // ✅ 버튼 공통 스타일
        Color btnBg = new Color(230, 245, 255);
        Color btnBorder = new Color(150, 180, 200);
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 14);

        // ✅ 새로운 채팅방 버튼
        createChatBtn.setBounds(15, 10, 370, 35);
        createChatBtn.setFont(btnFont);
        createChatBtn.setBackground(btnBg);
        createChatBtn.setBorder(BorderFactory.createLineBorder(btnBorder));
        createChatBtn.setFocusPainted(false);
        createChatBtn.setContentAreaFilled(true);
        createChatBtn.setOpaque(true);

        createChatBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LobbyFrame.createChatFrame.setVisible(true);
            }
        });
        add(createChatBtn);
        createChatBtn.setVisible(false);

        // ✅ 나가기 버튼
        exitBtn.setBounds(15, 10, 370, 35);
        exitBtn.setFont(btnFont);
        exitBtn.setBackground(btnBg);
        exitBtn.setBorder(BorderFactory.createLineBorder(btnBorder));
        exitBtn.setFocusPainted(false);
        exitBtn.setContentAreaFilled(true);
        exitBtn.setOpaque(true);

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.chatPanelMap.remove(chatRoomName);
                Application.chatRoomUserListPanelMap.remove(chatRoomName);
                Application.sender.sendMessage(
                        new dto.request.ExitChatRequest(chatRoomName, Application.me.getId())
                );
                frame.dispose();
            }
        });
        add(exitBtn);

        // ✅ 패널 위치 고정
        frame.add(this);
        setBounds(410, 460, 400, 50);
        setVisible(true);
    }

    public void setCreateChatBtnVisible(boolean bool) {
        createChatBtn.setVisible(bool);
    }

    public void setExitBtnVisible(boolean bool) {
        exitBtn.setVisible(bool);
    }
}
