package uet.oop.bomberman.screen;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class GradientText {
    private Font font;
    private Color color1;
    private Color color2;
    private Color color3;

    public GradientText(Font font, Color color1, Color color2, Color color3) {
        this.font = font;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    public void draw(Graphics2D g2d, String text, int x, int y) {
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, text);
        Shape outline = gv.getOutline(x, y);

        Rectangle bounds = outline.getBounds();
        float thirdWidth = bounds.width / 2f;

        GradientPaint gradientPaint1 = new GradientPaint(
                bounds.x, bounds.y, color1,
                bounds.x + thirdWidth, bounds.y, color2);
        GradientPaint gradientPaint2 = new GradientPaint(
                bounds.x + thirdWidth, bounds.y, color2,
                bounds.x + 2 * thirdWidth, bounds.y, color3);
        GradientPaint gradientPaint3 = new GradientPaint(
                bounds.x + 2 * thirdWidth, bounds.y, color3,
                bounds.x + bounds.width, bounds.y, color3);

        Shape originalClip = g2d.getClip();

        g2d.setClip(bounds.x, bounds.y, (int) thirdWidth, bounds.height);
        g2d.setPaint(gradientPaint1);
        g2d.fill(outline);

        g2d.setClip(bounds.x + (int) thirdWidth, bounds.y, (int) thirdWidth, bounds.height);
        g2d.setPaint(gradientPaint2);
        g2d.fill(outline);

        g2d.setClip(bounds.x + 2 * (int) thirdWidth, bounds.y, (int) thirdWidth, bounds.height);
        g2d.setPaint(gradientPaint3);
        g2d.fill(outline);

        g2d.setClip(originalClip);
    }
}
