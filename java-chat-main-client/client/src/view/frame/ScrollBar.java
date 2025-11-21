package view.frame;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollBar extends BasicScrollBarUI {

    // ✅ 색감 설정
    private final Color trackColor = new Color(230, 245, 255); // 연한 하늘색
    private final Color thumbColor = new Color(180, 215, 245); // 조금 진한 하늘색
    private final Color borderColor = new Color(160, 140, 120); // 브라운

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

        g2.setColor(borderColor);
        g2.drawRect(trackBounds.x, trackBounds.y, trackBounds.width - 1, trackBounds.height - 1);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!scrollbar.isEnabled() || thumbBounds.width > thumbBounds.height) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width - 1, thumbBounds.height - 1, 10, 10);

        g2.setColor(borderColor);
        g2.drawRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width - 1, thumbBounds.height - 1, 10, 10);
        g2.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createEmptyButton();
    }

    private JButton createEmptyButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setFocusable(false);
        button.setBorder(null);
        button.setOpaque(false);
        return button;
    }
}
