package view.frame;

import app.Application;
import dto.request.LoginRequest;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private LobbyFrame lobbyFrame;

    JTextField idTextF = new JTextField(20);
    JPasswordField passwordField = new JPasswordField(20);
    JButton loginBtn = new JButton("로그인");
    JButton signupBtn = new JButton("회원가입");

    public LoginFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;

        setTitle("AngelTalk Login");
        setSize(430, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, getHeight(), Color.WHITE,
                        0, 0, new Color(200, 240, 240)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        JLabel logoLabel = new JLabel();
        logoLabel.setBounds(115, 60, 200, 150);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon originIcon = new ImageIcon("image/angel_talk_logo.png");
        Image scaledImg = originIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImg));
        bgPanel.add(logoLabel);

        // 색상 정의
        Color buttonBg = Color.decode("#F8E9D8");
        Color buttonBorder = Color.decode("#CBB9A8");
        Color textColor = Color.decode("#6B4F4F");

        // ID 입력
        idTextF.setBounds(65, 250, 290, 45);
        idTextF.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        idTextF.setBorder(BorderFactory.createLineBorder(buttonBorder));
        idTextF.setBackground(Color.WHITE);
        idTextF.setForeground(textColor);
        idTextF.setCaretColor(textColor);
        bgPanel.add(idTextF);

        // PW 입력
        passwordField.setBounds(65, 305, 290, 45);
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createLineBorder(buttonBorder));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        bgPanel.add(passwordField);

        // 로그인 버튼
        loginBtn.setBounds(65, 370, 290, 45);
        loginBtn.setBackground(buttonBg);
        loginBtn.setForeground(textColor);
        loginBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        loginBtn.setBorder(BorderFactory.createLineBorder(buttonBorder));
        loginBtn.addActionListener(e -> loginAction());
        bgPanel.add(loginBtn);

        // 회원가입 버튼
        signupBtn.setBounds(65, 425, 290, 45);
        signupBtn.setBackground(buttonBg);
        signupBtn.setForeground(textColor);
        signupBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        signupBtn.setBorder(BorderFactory.createLineBorder(buttonBorder));
        signupBtn.addActionListener(e -> {
            this.dispose();
            new SignupFrame(this);
        });
        bgPanel.add(signupBtn);

        setVisible(true);
    }

    private void loginAction() {
        String id = idTextF.getText();
        String password = new String(passwordField.getPassword());

        if (id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "아이디를 입력하세요.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "비밀번호를 입력하세요.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Application.userId = id;
        Application.sender.sendMessage(new LoginRequest(id, password));

        this.dispose();
        lobbyFrame.setVisible(true);
    }
}
