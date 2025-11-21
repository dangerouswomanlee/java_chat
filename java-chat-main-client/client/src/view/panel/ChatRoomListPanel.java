package view.panel;

import app.Application;
import domain.ChatRoom;
import dto.request.EnterChatRequest;
import view.frame.ChatFrame;
import view.frame.ScrollBar; // ✅ 스크롤바 import

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatRoomListPanel extends JPanel {

    JPanel labelPanel = new JPanel();
    JLabel label = new JLabel("채팅방 목록 (채팅방 이름 클릭 시 채팅방으로 이동)");

    Font korFont = new Font("맑은 고딕", Font.PLAIN, 14);

    public ChatRoomListPanel(JFrame frame) {
        setLayout(null);
        setOpaque(false); // ✅ 배경 투명 (직접 칠함)

        label.setBounds(10, 5, 350, 30);
        label.setFont(korFont);
        label.setForeground(new Color(110, 85, 60)); // 감성 브라운 폰트
        add(label);

        labelPanel.setOpaque(false);
        labelPanel.setLayout(new GridLayout(30, 1));

        JScrollPane scrPane = new JScrollPane(labelPanel);
        scrPane.setBounds(5, 40, 390, 200);
        scrPane.setOpaque(false);
        scrPane.getViewport().setOpaque(false);

        // ✅ 베이지 라운드 테두리 & 여백
        scrPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,180,150), 1, true),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        // ✅ 스크롤바 감성적 처리
        scrPane.getVerticalScrollBar().setUI(new ScrollBar());
        scrPane.getHorizontalScrollBar().setUI(new ScrollBar());

        add(scrPane);

        frame.add(this);
        setBounds(410, 210, 400, 250);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(
                0, 0, new Color(248, 254, 255),
                0, getHeight(), new Color(233, 250, 255)
        );
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
    }

    public void paintChatRoomList() {
        labelPanel.removeAll();

        for (ChatRoom chatRoom : Application.chatRooms) {
            JLabel nameLabel = new JLabel(chatRoom.getName());
            nameLabel.setFont(korFont);
            nameLabel.setForeground(new Color(110, 85, 60));
            nameLabel.addMouseListener(new ChatRoomMouseAdapter(chatRoom.getName()));
            labelPanel.add(nameLabel);
        }

        labelPanel.revalidate();
        labelPanel.repaint();
    }

    public void addChatRoomLabel(String chatRoomName) {
        JLabel nameLabel = new JLabel(chatRoomName);
        nameLabel.setFont(korFont);
        nameLabel.setForeground(new Color(110, 85, 60));
        nameLabel.addMouseListener(new ChatRoomMouseAdapter(chatRoomName));
        labelPanel.add(nameLabel);

        labelPanel.revalidate();
        labelPanel.repaint();
    }

    class ChatRoomMouseAdapter extends MouseAdapter {
        String chatRoomName;

        public ChatRoomMouseAdapter(String chatRoomName) {
            this.chatRoomName = chatRoomName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (Application.chatPanelMap.containsKey(chatRoomName)) {
                JOptionPane.showMessageDialog(null,
                        "chat room is already opened.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ChatFrame chatFrame = new ChatFrame(chatRoomName);
            Application.chatPanelMap.put(chatRoomName, chatFrame.getChatPanel());
            Application.chatRoomUserListPanelMap.put(chatRoomName, chatFrame.getChatRoomUserListPanel());

            Application.sender.sendMessage(new EnterChatRequest(chatRoomName, Application.me.getId()));
        }
    }
}
