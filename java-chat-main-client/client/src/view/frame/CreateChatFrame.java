package view.frame;

import app.Application;
import dto.request.CreateChatRoomRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateChatFrame extends JFrame {

    CreateChatFrame frame;

    JLabel chatNameLabel = new JLabel("채팅방 이름");
    JTextField chatNameTextF = new JTextField();
    JButton okBtn = new JButton("확인");
    JButton cancelBtn = new JButton("취소");

    public CreateChatFrame() {
        frame = this;

        // ✅ 배경 패널 (그라데이션)
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(245, 252, 255),
                        0, getHeight(), new Color(225, 240, 250)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // ✅ 라벨 스타일
        chatNameLabel.setBounds(100, 80, 120, 40);
        chatNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        chatNameLabel.setForeground(new Color(60, 90, 120));
        bgPanel.add(chatNameLabel);

        // ✅ 텍스트 필드 스타일
        chatNameTextF.setBounds(230, 80, 260, 40);
        chatNameTextF.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        chatNameTextF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 180, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        chatNameTextF.setBackground(Color.WHITE);
        chatNameTextF.setForeground(new Color(50, 70, 90));
        chatNameTextF.setCaretColor(new Color(50, 70, 90));
        bgPanel.add(chatNameTextF);

        // ✅ 버튼 스타일 함수
        styleButton(okBtn);
        styleButton(cancelBtn);

        okBtn.setBounds(140, 230, 140, 45);
        cancelBtn.setBounds(310, 230, 140, 45);

        okBtn.addActionListener(new OkBtnActionListener());
        cancelBtn.addActionListener(new CancelBtnActionListener());

        bgPanel.add(okBtn);
        bgPanel.add(cancelBtn);

        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(false);
    }

    // ✅ 공통 버튼 스타일
    private void styleButton(JButton b) {
        b.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        b.setBackground(new Color(230, 245, 255));
        b.setForeground(new Color(50, 70, 90));
        b.setBorder(BorderFactory.createLineBorder(new Color(150, 180, 200)));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(210, 235, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(230, 245, 255));
            }
        });
    }

    class OkBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String chatRoomName = chatNameTextF.getText();
            if (chatRoomName.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "채팅방 이름을 입력해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Application.sender.sendMessage(new CreateChatRoomRequest(chatRoomName, Application.me.getId()));
            frame.dispose();

            ChatFrame chatFrame = new ChatFrame(chatRoomName);
            Application.chatPanelMap.put(chatRoomName, chatFrame.chatPanel);
            Application.chatRoomUserListPanelMap.put(chatRoomName, chatFrame.chatRoomUserListPanel);
        }
    }

    class CancelBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }
}
