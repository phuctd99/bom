package uet.oop.bomberman.base;

import java.awt.Graphics;
import java.util.List;

import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.graphics.IRender;
import uet.oop.bomberman.graphics.Screen;

public interface IGameInfoManager extends IMessageManager, IRender {

    public int subtractTime();

    public int getTime();

    public int getPoints();

    public void addPoints(int points);

    public boolean isPaused();

    public void pause();

    public void unpause();

    public void endGame();

    public List<Item> getPlayerActiveItems();

    public void setEntityManager(IEntityManager entityManager);

    public void render(Screen screen, Graphics g);

}