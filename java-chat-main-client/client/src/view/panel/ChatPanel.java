package view.panel;

import app.Application;
import dto.request.MessageRequest;
import dto.type.MessageType;
import view.frame.ScrollBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel implements ActionListener {

    String chatRoomName;

    JPanel chatBox;
    JScrollPane scrollPane;

    JTextField msgTextF = new JTextField(50);
    JButton sendBtn = new JButton("전송");

    public ChatPanel(String chatRoomName) {
        this.chatRoomName = chatRoomName;
        setLayout(null);

        setBackground(new Color(245, 252, 255));

        chatBox = new JPanel() {
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
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        chatBox.setLayout(new BoxLayout(chatBox, BoxLayout.Y_AXIS));
        chatBox.setOpaque(false);

        scrollPane = new JScrollPane(chatBox);
        scrollPane.setBounds(10, 0, 380, 450);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane);

        scrollPane.getVerticalScrollBar().setUI(new ScrollBar());
        scrollPane.getHorizontalScrollBar().setUI(new ScrollBar());

        msgTextF.setBounds(10, 460, 250, 35);
        msgTextF.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        msgTextF.setBackground(Color.WHITE);
        msgTextF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 215)),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        add(msgTextF);

        sendBtn.setBounds(270, 460, 120, 35);
        sendBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        sendBtn.setBackground(new Color(230, 245, 255));
        sendBtn.setBorder(BorderFactory.createLineBorder(new Color(150,180,200)));
        sendBtn.addActionListener(this);
        add(sendBtn);

        setBounds(10, 10, 400, 500);
    }

    // ✅ 메시지 추가 (말풍선)
    public void addBubble(String username, String message, boolean isMe) {
        String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("a h:mm"))
                .replace("AM", "오전").replace("PM", "오후");

        JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
        line.setOpaque(false);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        BubblePanel bubble = new BubblePanel(username + "\n" + message + "\n" + time, isMe);

        if (isMe) {
            line.add(Box.createHorizontalGlue());
            line.add(bubble);
        } else {
            line.add(bubble);
            line.add(Box.createHorizontalGlue());
        }

        chatBox.add(line);
        chatBox.revalidate();
        chatBox.repaint();

        SwingUtilities.invokeLater(() ->
                scrollPane.getVerticalScrollBar().setValue(
                        scrollPane.getVerticalScrollBar().getMaximum()
                )
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = msgTextF.getText().trim();
        if (message.isEmpty()) return;

        if (Application.me == null) {
            JOptionPane.showMessageDialog(this, "로그인이 완료되지 않았습니다.");
            return;
        }

        Application.sender.sendMessage(
                new MessageRequest(MessageType.CHAT, chatRoomName, Application.me.getName(), message)
        );

        addBubble(Application.me.getName(), message, true);
        msgTextF.setText("");
    }

    // ✅ 서버에서 받은 메시지 처리
    public void receiveMessage(String username, String message) {
        boolean isMe = Application.me != null && username.equals(Application.me.getName());
        addBubble(username, message, isMe);
    }
}
