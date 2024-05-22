package uet.oop.bomberman.screen;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.gui.GameScreen;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.utils.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class SelectGameModeScreen extends GameScreen {
    ArrayList<String> gameModes = new ArrayList<String>();
    int selectorIndex = 0;
    private Optional<Keyboard> _input;

    public SelectGameModeScreen() {
        gameModes.add(EGameMode.ONE_PLAYER.getStringLevel());
        gameModes.add(EGameMode.TWO_PLAYER.getStringLevel());
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
        g.setColor(Color.black);
        g.fillRect(0, 0, Global.screenWidth, Global.screenHeight);

        drawTitle(g);
        drawOptions(g);
        drawSelector(g);
    }

    private void drawTitle(Graphics g) {
        String title = "SELECT GAME MODE";
        Font font = new Font("Arial", Font.BOLD, 20 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.white);

        FontMetrics fm = g.getFontMetrics();
        int x = (Global.screenWidth - fm.stringWidth(title)) / 2;
        int marginTop = 20;
        int y = marginTop + fm.getAscent();

        g.drawString(title, x, y);
    }

    private void drawOptions(Graphics g) {
        Font font = new Font("Arial", Font.PLAIN, 10 * Game.SCALE);
        g.setFont(font);
        g.setColor(Color.white);

        int w = Global.screenWidth;
        int h = Global.screenHeight;
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent() + fm.getDescent();
        int boxHeight = textHeight * this.gameModes.size();
        int marginTop = (h - boxHeight) / 2;

        for (int i=0; i < this.gameModes.size(); i++) {
            String level = this.gameModes.get(i);
            int x = (w - fm.stringWidth(level)) / 2;
            int y = marginTop + fm.getAscent() + textHeight*i;

            g.drawString(level, x, y);
        }
    }

    private void drawSelector(Graphics g) {
        String level = this.gameModes.get(selectorIndex);
        int w = Global.screenWidth;
        int h = Global.screenHeight;
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent() + fm.getDescent();
        int boxHeight = textHeight * this.gameModes.size();
        int marginTop = (h - boxHeight) / 2;

        int x = (w - fm.stringWidth(level)) / 2 - 30;
        int y = marginTop + fm.getAscent() + textHeight*selectorIndex;

        g.drawString(">", x, y);
    }

    @Override
    public void update() {

    }

    @Override
    public void onDestroy() {
        this._input = null;
    }
}
