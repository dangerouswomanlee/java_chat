package view.panel;

import javax.swing.*;
import java.awt.*;

public class ChatMessageItem extends JPanel {

    public ChatMessageItem(String username, String message, String time, ImageIcon profileImg, boolean isMe) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ✅ 닉네임
        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        nameLabel.setForeground(new Color(107, 79, 79)); // #6B4F4F

        // ✅ 말풍선
        BubblePanel bubble = new BubblePanel(message, isMe);

        // ✅ 시간
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(150, 150, 150));

        // ✅ 프로필 이미지(상대만)
        JLabel profileLabel = new JLabel();
        if (!isMe && profileImg != null) {
            Image scaled = profileImg.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            profileLabel.setIcon(new ImageIcon(scaled));
        }

        if (isMe) {
            // 내 메시지 오른쪽 정렬
            nameLabel.setAlignmentX(RIGHT_ALIGNMENT);
            bubble.setAlignmentX(RIGHT_ALIGNMENT);
            timeLabel.setAlignmentX(RIGHT_ALIGNMENT);

            content.add(nameLabel);
            content.add(bubble);
            content.add(timeLabel);

            add(content, BorderLayout.EAST);
        } else {
            // 상대 메시지 왼쪽 정렬
            JPanel leftWrapper = new JPanel();
            leftWrapper.setOpaque(false);
            leftWrapper.add(profileLabel);

            content.add(nameLabel);
            content.add(bubble);
            content.add(timeLabel);

            add(leftWrapper, BorderLayout.WEST);
            add(content, BorderLayout.CENTER);
        }
    }
}
