package uet.oop.bomberman.screen;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class GradientText {

    private Font font;
    private Color color1, color2, color3;

    public GradientText(Font font, Color color1, Color color2, Color color3) {
        this.font = font;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    public void draw(Graphics2D g2d, String text, int x, int y) {
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();

        // Create LinearGradientPaint
        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color[] colors = { color1, color2, color3 };
        LinearGradientPaint gradient = new LinearGradientPaint(0, 0, fm.stringWidth(text), 0, fractions, colors);

        g2d.setPaint(gradient);

        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout textLayout = new TextLayout(text, font, frc);
        Rectangle2D bounds = textLayout.getBounds();
        Shape shape = textLayout.getOutline(AffineTransform.getTranslateInstance(x, y + bounds.getHeight()));

        g2d.fill(shape);
    }
}
