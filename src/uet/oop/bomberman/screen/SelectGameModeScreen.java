package uet.oop.bomberman.screen;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.gui.GameScreen;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class SelectGameModeScreen extends GameScreen {
    ArrayList<String> gameModes = new ArrayList<String>();
    int selectorIndex = 0;
    private Optional<Keyboard> _input;
    private BufferedImage backgroundImage;
    private int OFFSET = 40;
    private BufferedImage pointerImage;

    public SelectGameModeScreen() {
        gameModes.add(EGameMode.ONE_PLAYER.getStringLevel());
        gameModes.add(EGameMode.TWO_PLAYER.getStringLevel());

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/menu/bgBombman.png"));
            pointerImage = ImageIO.read(getClass().getResource("/menu/pointer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInput(Keyboard input) {
        _input = Optional.ofNullable(input);

        _input.get().keyboardInputCallback = Optional.of(new Keyboard.KeyboardInputCallback() {
            @Override
            public void onKeyPressed(EGameControl gameControl) {
                switch (gameControl) {
                    case UP:
                        selectorIndex--;
                        break;
                    case DOWN:
                        selectorIndex++;
                        break;
                    case ENTER:
                        Global.currentScreen = EScreenName.SELECT_LEVEL_SCREEN;
                        if (selectorIndex == 1) {
                            Global.gameMode = EGameMode.TWO_PLAYER;
                        } else {
                            Global.gameMode = EGameMode.ONE_PLAYER;
                        }
                        break;
                }

                if (selectorIndex < 0) {
                    selectorIndex = gameModes.size() - 1;
                } else if (selectorIndex > gameModes.size() - 1) {
                    selectorIndex = 0;
                }
            }
        });
    }

    @Override
    public void drawScreen(Graphics g) {
        // set background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Global.screenWidth, Global.screenHeight, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Global.screenWidth, Global.screenHeight);
        }

        drawTitle(g, "SELECT GAME MODE");
        drawOptions(g);
        drawSelector(g);
    }

    private void drawTitle(Graphics g, String title) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Minecraft", Font.BOLD, 20 * Game.SCALE);

        // Create gradient colors
        Color color1 = Color.RED;
        Color color2 = Color.ORANGE;
        Color color3 = Color.YELLOW;

        // Create GradientText object
        GradientText gradientText = new GradientText(font, color1, color2, color3);

        // Calculate the position for the title
        int x = (Global.screenWidth - g.getFontMetrics().stringWidth(title)) / 15;
        int y = 190;

        // Draw the gradient text
        gradientText.draw((Graphics2D) g, title, (Global.screenWidth - g.getFontMetrics().stringWidth(title)) / 15,
                190);

        // Create outline for the text
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, title);
        Shape outline = gv.getOutline(x, y);

        // Draw the outline
        g2d.setColor(Color.BLACK);
        g2d.draw(outline);
    }

    private void drawOptions(Graphics g) {
        Font font = new Font("Minecraft", Font.PLAIN, 10 * Game.SCALE);
        g.setFont(font);

        // Position of Options
        int w = Global.screenWidth;
        int h = Global.screenHeight;
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent() + fm.getDescent();

        // Khoảng cách giữa các lựa chọn
        int spacing = 10 * Game.SCALE;

        // Tính toán tổng chiều cao của các lựa chọn cộng với khoảng cách giữa chúng
        int boxHeight = (textHeight) * this.gameModes.size() - spacing;
        int marginTop = (h - boxHeight) / 2;

        for (int i = 0; i < gameModes.size(); i++) {
            String mode = gameModes.get(i);

            int x = (w - fm.stringWidth(mode)) / 2;
            int y = marginTop + fm.getAscent() + (textHeight + spacing) * i;

            if (i == selectorIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            g.drawString(mode, x, y);
        }
    }

    private void drawSelector(Graphics g) {
        Font font = new Font("Minecraft", Font.PLAIN, 12 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.white);

        String level = this.gameModes.get(selectorIndex);
        int w = Global.screenWidth;
        int h = Global.screenHeight;
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent() + fm.getDescent();
        int boxHeight = textHeight * this.gameModes.size();
        int marginTop = (h - boxHeight) / 2 + 15;

        int spacing = 10 * Game.SCALE;
        int x = (w - fm.stringWidth(level)) / 2 - 50;
        int y = marginTop + fm.getAscent() + (textHeight + spacing) * selectorIndex;

        g.drawImage(pointerImage, x, y - fm.getAscent(), null);
    }

    @Override
    public void update() {

    }

    @Override
    public void onDestroy() {
        this._input.get().keyboardInputCallback = Optional.ofNullable(null);
    }
}
