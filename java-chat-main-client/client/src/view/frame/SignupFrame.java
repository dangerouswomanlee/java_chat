package view.frame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class SignupFrame extends JFrame implements AddressSelectedCallback {

	private LoginFrame loginFrame;

	private JTextField idField, nicknameField, emailField1, emailField2, nameField, birthField, phoneField2,
			phoneField3, zipField, addressField, detailAddressField;
	private JPasswordField passwordField, passwordConfirmField;
	private JProgressBar passwordStrengthBar;
	private JComboBox<String> phoneComboBox, genderComboBox;
	private JLabel photoLabel;
	private JButton idCheckButton, nicknameCheckButton, calendarButton, zipButton, photoUploadButton, signUpButton,
			cancelButton;

	private String photoPath = null;

	public SignupFrame(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;

		setTitle("회원가입");
		setSize(550, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ✅ 배경 그라데이션 적용
		JPanel bgPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				GradientPaint gp = new GradientPaint(0, 0, new Color(245, 252, 255), 0, getHeight(),
						new Color(233, 250, 255));
				g2.setPaint(gp);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		bgPanel.setLayout(null);
		setContentPane(bgPanel);

		createComponents(bgPanel);
		addEventListeners();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	// ✅ 텍스트필드 스타일
	private void styleTextField(JTextField f) {
		f.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(150, 180, 200)),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		f.setBackground(Color.WHITE);
		f.setForeground(new Color(50, 70, 90));
		f.setCaretColor(new Color(50, 70, 90));
	}

	// ✅ 버튼 스타일
	private void styleButton(JButton b) {
		b.setFont(new Font("맑은 고딕", Font.BOLD, 14));
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

	@Override
	public void onAddressSelected(String postcode, String address) {
		zipField.setText(postcode);
		addressField.setText(address);
		detailAddressField.requestFocus();
	}

	private void createComponents(JPanel p) {

		addLabel(p, "아이디:", 30, 30, 100, 30);
		idField = new JTextField();
		idField.setBounds(150, 30, 150, 30);
		styleTextField(idField);
		p.add(idField);
		idCheckButton = new JButton("중복 확인");
		idCheckButton.setBounds(310, 30, 100, 30);
		styleButton(idCheckButton);
		p.add(idCheckButton);

		addLabel(p, "비밀번호:", 30, 70, 100, 30);
		passwordField = new JPasswordField();
		passwordField.setBounds(150, 70, 150, 30);
		styleTextField(passwordField);
		p.add(passwordField);

		addLabel(p, "비밀번호 확인:", 30, 110, 120, 30);
		passwordConfirmField = new JPasswordField();
		passwordConfirmField.setBounds(150, 110, 150, 30);
		styleTextField(passwordConfirmField);
		p.add(passwordConfirmField);

		passwordStrengthBar = new JProgressBar(0, 100);
		passwordStrengthBar.setBounds(310, 110, 100, 30);
		passwordStrengthBar.setStringPainted(true);
		p.add(passwordStrengthBar);

		addLabel(p, "닉네임:", 30, 150, 100, 30);
		nicknameField = new JTextField();
		nicknameField.setBounds(150, 150, 150, 30);
		styleTextField(nicknameField);
		p.add(nicknameField);
		nicknameCheckButton = new JButton("중복 확인");
		nicknameCheckButton.setBounds(310, 150, 100, 30);
		styleButton(nicknameCheckButton);
		p.add(nicknameCheckButton);

		addLabel(p, "이메일:", 30, 190, 100, 30);
		emailField1 = new JTextField();
		emailField1.setBounds(150, 190, 100, 30);
		styleTextField(emailField1);
		p.add(emailField1);
		addLabel(p, "@", 255, 190, 20, 30);
		emailField2 = new JTextField();
		emailField2.setBounds(275, 190, 100, 30);
		styleTextField(emailField2);
		p.add(emailField2);

		addLabel(p, "이름:", 30, 230, 100, 30);
		nameField = new JTextField();
		nameField.setBounds(150, 230, 150, 30);
		styleTextField(nameField);
		p.add(nameField);

		addLabel(p, "성별:", 310, 230, 40, 30);
		genderComboBox = new JComboBox<>(new String[] { "남", "여" });
		genderComboBox.setBounds(350, 230, 60, 30);
		p.add(genderComboBox);

		addLabel(p, "생년월일:", 30, 270, 100, 30);
		birthField = new JTextField();
		birthField.setBounds(150, 270, 150, 30);
		styleTextField(birthField);
		p.add(birthField);
		calendarButton = new JButton("📅");
		calendarButton.setBounds(310, 270, 50, 30);
		styleButton(calendarButton);
		p.add(calendarButton);

		addLabel(p, "전화번호:", 30, 310, 100, 30);
		phoneComboBox = new JComboBox<>(new String[] { "010", "011", "016", "017" });
		phoneComboBox.setBounds(150, 310, 80, 30);
		p.add(phoneComboBox);
		phoneField2 = new JTextField();
		phoneField2.setBounds(240, 310, 80, 30);
		styleTextField(phoneField2);
		p.add(phoneField2);
		phoneField3 = new JTextField();
		phoneField3.setBounds(330, 310, 80, 30);
		styleTextField(phoneField3);
		p.add(phoneField3);

		addLabel(p, "우편번호:", 30, 350, 100, 30);
		zipField = new JTextField();
		zipField.setBounds(150, 350, 150, 30);
		styleTextField(zipField);
		p.add(zipField);
		zipButton = new JButton("우편번호");
		zipButton.setBounds(310, 350, 100, 30);
		styleButton(zipButton);
		p.add(zipButton);

		addLabel(p, "주소:", 30, 390, 100, 30);
		addressField = new JTextField();
		addressField.setBounds(150, 390, 335, 30);
		styleTextField(addressField);
		p.add(addressField);

		addLabel(p, "상세주소:", 30, 430, 100, 30);
		detailAddressField = new JTextField();
		detailAddressField.setBounds(150, 430, 335, 30);
		styleTextField(detailAddressField);
		p.add(detailAddressField);

		addLabel(p, "프로필 사진", 430, 10, 80, 20);
		photoLabel = new JLabel("사진 없음", SwingConstants.CENTER);
		photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		photoLabel.setBounds(420, 40, 100, 100);
		p.add(photoLabel);
		photoUploadButton = new JButton("사진 업로드");
		photoUploadButton.setBounds(420, 150, 100, 30);
		styleButton(photoUploadButton);
		p.add(photoUploadButton);

		JButton randomButton = new JButton("랜덤 생성");
		randomButton.setBounds(420, 190, 100, 30);
		styleButton(randomButton);
		p.add(randomButton);
		randomButton.addActionListener(e -> generateRandomAvatar());

		signUpButton = new JButton("회원가입");
		signUpButton.setBounds(150, 490, 100, 40);
		styleButton(signUpButton);
		p.add(signUpButton);

		cancelButton = new JButton("뒤로 가기");
		cancelButton.setBounds(280, 490, 100, 40);
		styleButton(cancelButton);
		p.add(cancelButton);
	}

	private void addLabel(JPanel p, String text, int x, int y, int w, int h) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, w, h);
		label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		label.setForeground(new Color(70, 100, 130));
		p.add(label);
	}

	/* 이하 기존 로직 그대로 유지 (생성, DB, 이벤트 등) */
	// ⚠️ 아래 부분은 **당신 코드 그대로**입니다. 건드리지 않았습니다.

	private void addEventListeners() {
		passwordField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updatePasswordStrength();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updatePasswordStrength();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updatePasswordStrength();
			}
		});

		idCheckButton.addActionListener(e -> {
			String id = idField.getText().trim();
			if (id.isEmpty()) {
				JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.");
				return;
			}
			if (checkDuplicate("USER_ID", id)) {
				JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.", "중복 확인", JOptionPane.ERROR_MESSAGE);
				idField.setText("");
				idField.requestFocus();
			} else
				JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.", "중복 확인", JOptionPane.INFORMATION_MESSAGE);
		});

		nicknameCheckButton.addActionListener(e -> {
			String nickname = nicknameField.getText().trim();
			if (nickname.isEmpty()) {
				JOptionPane.showMessageDialog(this, "닉네임을 입력해주세요.");
				return;
			}
			if (checkDuplicate("NICKNAME", nickname)) {
				JOptionPane.showMessageDialog(this, "이미 사용 중인 닉네임입니다.", "중복 확인", JOptionPane.ERROR_MESSAGE);
				nicknameField.setText("");
				nicknameField.requestFocus();
			} else
				JOptionPane.showMessageDialog(this, "사용 가능한 닉네임입니다.", "중복 확인", JOptionPane.INFORMATION_MESSAGE);
		});

		zipButton.addActionListener(e -> {
			PostcodeSearchDialog dialog = new PostcodeSearchDialog(this, this);
			dialog.setVisible(true);
		});

		photoUploadButton.addActionListener(e -> {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					File f = fc.getSelectedFile();
					BufferedImage img = ImageIO.read(f);
					new ImageCrop(img, clipped -> {
						BufferedImage resized = resizeImage(clipped, photoLabel.getWidth(), photoLabel.getHeight());
						photoLabel.setIcon(new ImageIcon(resized));
						photoLabel.setText("");
						try {
							File temp = File.createTempFile("clipped_", ".png");
							ImageIO.write(clipped, "png", temp);
							photoPath = temp.getAbsolutePath();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		signUpButton.addActionListener(e -> {
		    if (!validateInputs())
		        return;

		    String url = "jdbc:oracle:thin:@localhost:1521:XE";
		    String user = "chaeyeon";
		    String pass = "1234";

		    String sql = "INSERT INTO SIGNUP ("
		            + "USER_ID, USER_PW, NICKNAME, USER_NAME, EMAIL, GENDER, BIRTH_DATE, "
		            + "PHONE_NUMBER, ZIP_CODE, ADDRESS, DETAIL_ADDRESS, PROFILE_PHOTO_PATH, REG_DATE"
		            + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE)";

		    FileInputStream fis = null; // ★ 밖에서 선언

		    try (Connection conn = DriverManager.getConnection(url, user, pass);
		         PreparedStatement pstmt = conn.prepareStatement(sql)) {

		        // 기본 정보
		        pstmt.setString(1, idField.getText());
		        pstmt.setString(2, new String(passwordField.getPassword()));
		        pstmt.setString(3, nicknameField.getText());
		        pstmt.setString(4, nameField.getText());
		        pstmt.setString(5, emailField1.getText() + "@" + emailField2.getText());
		        pstmt.setString(6, (String) genderComboBox.getSelectedItem());

		        // 생년월일
		        String b = birthField.getText();
		        java.sql.Date birthDate = java.sql.Date.valueOf(
		                b.substring(0, 4) + "-" + b.substring(4, 6) + "-" + b.substring(6, 8)
		        );
		        pstmt.setDate(7, birthDate);

		        // 전화번호
		        pstmt.setString(8,
		                phoneComboBox.getSelectedItem() + "-"
		                        + phoneField2.getText() + "-"
		                        + phoneField3.getText()
		        );

		        pstmt.setString(9, zipField.getText());
		        pstmt.setString(10, addressField.getText());
		        pstmt.setString(11, detailAddressField.getText());

		        // ★ BLOB 저장 — try-with-resources 사용 금지
		        if (photoPath != null) {
		            File imageFile = new File(photoPath);
		            fis = new FileInputStream(imageFile);
		            pstmt.setBinaryStream(12, fis, (int) imageFile.length());
		        } else {
		            pstmt.setNull(12, java.sql.Types.BLOB);
		        }

		        // ★ 여기서 Oracle이 fis를 읽음 → 반드시 open 상태여야 함
		        int result = pstmt.executeUpdate();

		        if (result > 0) {
		            JOptionPane.showMessageDialog(this, "회원가입 완료! 로그인 창으로 이동합니다.");
		            dispose();
		            if (loginFrame != null)
		                loginFrame.setVisible(true);
		        }

		    } catch (Exception ex) {
		        ex.printStackTrace();
		    } finally {
		        if (fis != null) try { fis.close(); } catch (IOException ex) {}
		    }
		});


		cancelButton.addActionListener(e -> dispose());
	}

	private boolean validateInputs() {
		if (idField.getText().isEmpty() || nicknameField.getText().isEmpty() || passwordField.getPassword().length == 0
				|| nameField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "필수 항목을 모두 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	private void updatePasswordStrength() {
		String pw = new String(passwordField.getPassword());
		int strength = Math.min(pw.length() * 10, 100);
		passwordStrengthBar.setValue(strength);
	}

	private boolean checkDuplicate(String c, String v) {
		String url = "jdbc:oracle:thin:@localhost:1521:XE", user = "chaeyeon", pass = "1234";
		String sql = "SELECT COUNT(*) CNT FROM SIGNUP WHERE " + c + "=?";
		try (Connection conn = DriverManager.getConnection(url, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, v);
			var rs = pstmt.executeQuery();
			if (rs.next())
				return rs.getInt("CNT") > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void generateRandomAvatar() {
		try {
			String seed = String.valueOf(System.currentTimeMillis());
			String url = "https://api.dicebear.com/9.x/bottts/png?seed=" + seed;
			BufferedImage img = ImageIO.read(new URL(url));
			BufferedImage resized = resizeImage(img, photoLabel.getWidth(), photoLabel.getHeight());
			photoLabel.setIcon(new ImageIcon(resized));
			photoLabel.setText("");
			File tempFile = File.createTempFile("avatar_", ".png");
			ImageIO.write(img, "png", tempFile);
			photoPath = tempFile.getAbsolutePath();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private BufferedImage resizeImage(BufferedImage img, int w, int h) {
		BufferedImage r = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = r.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, w, h, null);
		g.dispose();
		return r;
	}
}
