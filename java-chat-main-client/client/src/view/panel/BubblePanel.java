package view.panel;

import javax.swing.*;
import java.awt.*;

public class BubblePanel extends JPanel {

	private final int maxWidth = 260;

	public BubblePanel(String text, boolean isMe) {
		setOpaque(false);
		setLayout(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT));
		
        String[] lines = text.split("\n");
        String username = lines.length > 0 ? lines[0] : "";
        String message = lines.length > 1 ? lines[1] : "";
        String time = lines.length > 2 ? lines[2] : "";

        StringBuilder displayText = new StringBuilder();
        if (!username.trim().isEmpty()) {
            displayText.append(username).append("\n");
        }
        displayText.append(message).append("\n").append(time);
        
		JTextArea textArea = new JTextArea(displayText.toString());
		textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		textArea.setForeground(new Color(110, 85, 60)); 
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
		textArea.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

		JPanel bubble = new GradientBubblePanel(isMe);
		bubble.setLayout(new BorderLayout());
		bubble.add(textArea, BorderLayout.CENTER);

		FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
		int textWidth = fm.stringWidth(text.replace("<html>", "").replace("</html>", ""));

		int bubbleWidth = Math.min(maxWidth, textWidth + 30);
		bubbleWidth = Math.max(80, bubbleWidth);

		textArea.setSize(new Dimension(bubbleWidth, Short.MAX_VALUE));
		Dimension d = textArea.getPreferredSize();

		bubble.setPreferredSize(new Dimension(bubbleWidth, d.height + 15));

		add(bubble);
	}

	static class GradientBubblePanel extends JPanel {

	    private final boolean isMe;

	    // 내 메시지 (분홍) & 상대방 (베이지)
	    private final Color topColorMe = new Color(0xFFE6F2);
	    private final Color bottomColorMe = new Color(0xFFCCE5);

	    private final Color topColorOther = new Color(0xFFF7E6);
	    private final Color bottomColorOther = new Color(0xFFECC9);

	    GradientBubblePanel(boolean isMe) {
	        this.isMe = isMe;
	        setOpaque(false);
	        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        int width = getWidth();
	        int height = getHeight();
	        int arc = 20;

	        GradientPaint gp = new GradientPaint(
	                0, 0,
	                isMe ? topColorMe : topColorOther,
	                0, height,
	                isMe ? bottomColorMe : bottomColorOther
	        );

	        g2.setPaint(gp);
	        g2.fillRoundRect(0, 0, width - 1, height - 1, arc, arc);

	        g2.setColor(new Color(150, 130, 110)); // 테두리 브라운 유지
	        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

	        super.paintComponent(g);
	    }
	}


	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
	}
}
