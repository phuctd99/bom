package uet.oop.bomberman.screen;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.gui.GameScreen;
import uet.oop.bomberman.input.Keyboard;

import uet.oop.bomberman.utils.EGameControl;
import uet.oop.bomberman.utils.EGameLevel;
import uet.oop.bomberman.utils.EGameMode;
import uet.oop.bomberman.utils.EScreenName;
import uet.oop.bomberman.utils.Global;

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
    private BufferedImage backgroundImage;

    public SelectLevelScreen(Board board) {
        _board = board;

        levels.add(EGameLevel.EASY.getStringLevel());
        levels.add(EGameLevel.MEDIUM.getStringLevel());
        levels.add(EGameLevel.HARD.getStringLevel());

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/menu/background.jpg"));
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
                        onDestroy();
                        break;
                    default: break;
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

        drawTitle(g);
        drawOptions(g);
        drawSelector(g);
    }

    private void drawTitle(Graphics g) {
        String title = "SELECT LEVEL";
        Font font = new Font("Minecraft", Font.BOLD, 20 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.white);

        FontMetrics fm = g.getFontMetrics();
        int x = (Global.screenWidth - fm.stringWidth(title)) / 2;
        int marginTop = 20;
        int y = marginTop + fm.getAscent();

        g.drawString(title, x, y);
    }

    private void drawOptions(Graphics g) {
        Font font = new Font("Minecraft", Font.PLAIN, 10 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.white);

        int w = Global.screenWidth;
        int h = Global.screenHeight;
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent() + fm.getDescent();
        int boxHeight = textHeight * this.levels.size();
        int marginTop = (h - boxHeight) / 2;

        for (int i = 0; i < this.levels.size(); i++) {
            String level = this.levels.get(i);
            int x = (w - fm.stringWidth(level)) / 2;
            int y = marginTop + fm.getAscent() + textHeight * i;

            g.drawString(level, x, y);
        }
    }

    private void drawSelector(Graphics g) {
        Font font = new Font("Minecraft", Font.PLAIN, 10 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.white);

        String level = this.levels.get(selectorIndex);
        int w = Global.screenWidth;
        int h = Global.screenHeight;
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent() + fm.getDescent();
        int boxHeight = textHeight * this.levels.size();
        int marginTop = (h - boxHeight) / 2;

        int x = (w - fm.stringWidth(level)) / 2 - 30;
        int y = marginTop + fm.getAscent() + textHeight * selectorIndex;

        g.drawString(">", x, y);
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
