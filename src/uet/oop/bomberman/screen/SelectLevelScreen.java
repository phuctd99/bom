package uet.oop.bomberman.screen;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.gui.Frame;
import uet.oop.bomberman.gui.GameScreen;
import uet.oop.bomberman.input.Keyboard;

import uet.oop.bomberman.utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class SelectLevelScreen extends GameScreen {
    ArrayList<String> levels = new ArrayList<String>();
    int selectorIndex = 0;
    private Optional<Keyboard> _input;
    private Board _board;
    private Frame _frame;
    private BufferedImage backgroundImage;
    private BufferedImage pointerImage;

    public SelectLevelScreen(Board board, Frame frame) {
        _board = board;
        _frame = frame;

        levels.add(EGameLevel.EASY.getStringLevel());
        levels.add(EGameLevel.MEDIUM.getStringLevel());
        levels.add(EGameLevel.HARD.getStringLevel());

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/menu/bgBombman.png"));
            pointerImage = ImageIO.read(getClass().getResource("/menu/pointer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInput(Keyboard input) {
        _input = Optional.ofNullable(input);

        _input.get().keyboardInputCallback = java.util.Optional.of(new Keyboard.KeyboardInputCallback() {
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
                        Global.gameLevel = selectorIndex + 1;
                        _board.getLevelManager().loadGlobalLevel();
                        Global.currentScreen = EScreenName.GAME_PLAY_SCREEN;
                        _frame.loadInfo();
                        onDestroy();
                        break;
                    default:
                        break;
                }

                if (selectorIndex < 0) {
                    selectorIndex = levels.size() - 1;
                } else if (selectorIndex > levels.size() - 1) {
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

        drawTitle(g, "SELECT LEVEL");
        drawOptions(g);
        drawSelector(g);
    }

    private void drawTitle(Graphics g, String title) {
        Font font = new Font("Minecraft", Font.BOLD, 20 * Game.SCALE);
        Color color1 = Color.RED;
        Color color2 = Color.ORANGE;
        Color color3 = Color.YELLOW;

        GradientText gradientText = new GradientText(font, color1, color2, color3);
        gradientText.draw((Graphics2D) g, title, (Global.screenWidth - g.getFontMetrics().stringWidth(title)) / 5,
                130);
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
        int boxHeight = (textHeight) * this.levels.size() - spacing;
        int marginTop = (h - boxHeight) / 2;

        for (int i = 0; i < levels.size(); i++) {
            String mode = levels.get(i);

            int x = (w - fm.stringWidth(mode)) / 2;
            int y = marginTop + (textHeight + spacing) * i - 20;

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
        g.setColor(Color.WHITE);

        String level = this.levels.get(selectorIndex);
        int w = Global.screenWidth;
        int h = Global.screenHeight;
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent() + fm.getDescent();
        int boxHeight = textHeight * this.levels.size();
        int marginTop = (h - boxHeight) / 2;

        int spacing = 9 * Game.SCALE; // Khoảng cách giống như trong drawOptions
        int x = (w - fm.stringWidth(level)) / 2 - 50; // Đặt vị trí mũi tên ở bên trái văn bản
        int y = marginTop + fm.getAscent() + (textHeight + spacing) * selectorIndex - 28;

        g.drawImage(pointerImage, x, y - fm.getAscent(), null);
    }

    @Override
    public void update() {
    }

    @Override
    public void onDestroy() {
        this._input.get().keyboardInputCallback = Optional.ofNullable(null);
        ;
    }
}
