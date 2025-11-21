package view.panel;

import domain.User;
import view.frame.ScrollBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChatRoomUserListPanel extends JPanel {

    JPanel labelPanel = new JPanel();
    JLabel label = new JLabel("사용자 목록");

    Font korFont = new Font("맑은 고딕", Font.PLAIN, 14);

    public ChatRoomUserListPanel(JFrame frame) {
        setLayout(null);

        // ✅ 배경 그라데이션을 위해 투명 처리
        setOpaque(false);

        label.setBounds(10, 5, 200, 30);
        label.setFont(korFont);
        label.setForeground(new Color(110, 85, 60)); // 감성 브라운
        add(label);

        labelPanel.setOpaque(false);
        labelPanel.setLayout(new GridLayout(50, 1));

        JScrollPane scrPane = new JScrollPane(labelPanel);
        scrPane.setBounds(5, 40, 390, 160);
        scrPane.setFont(korFont);
        scrPane.setOpaque(false);
        scrPane.getViewport().setOpaque(false);
        
        scrPane.getVerticalScrollBar().setUI(new view.frame.ScrollBar());
        scrPane.getHorizontalScrollBar().setUI(new view.frame.ScrollBar());


        // ✅ 부드러운 베이지 라운드 테두리 & 내부 여백
        scrPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,180,150), 1, true),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        add(scrPane);

        frame.add(this);
        setBounds(410, 10, 400, 210);
    }

    // ✅ 배경 그라데이션
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(
                0, 0, new Color(248, 254, 255),   // 연한 하늘색
                0, getHeight(), new Color(233, 250, 255)  // 더 연한 하늘색
        );
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
    }

    public void paintChatUsers(List<User> chatUsers) {
        labelPanel.removeAll();

        for (User user : chatUsers) {
            JLabel nameLabel = new JLabel(user.getName());
            nameLabel.setFont(korFont);
            nameLabel.setForeground(new Color(110,85,60)); // 감성 브라운
            labelPanel.add(nameLabel);
        }

        labelPanel.revalidate();
        labelPanel.repaint();
    }
}
