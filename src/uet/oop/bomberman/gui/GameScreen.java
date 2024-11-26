package uet.oop.bomberman.gui;

import java.awt.*;

public abstract class GameScreen {
    public abstract void drawScreen(Graphics g);
    public abstract void update();
    public abstract void onDestroy();
}
