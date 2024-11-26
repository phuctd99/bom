package uet.oop.bomberman.base;

import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.graphics.IRender;

public interface ITileManager extends IRender {

    public Tile getTileAt(double x, double y);
    public void addTile(int pos, Tile e);

}