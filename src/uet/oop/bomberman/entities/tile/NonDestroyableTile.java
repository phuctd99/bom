package uet.oop.bomberman.entities.tile;

import uet.oop.bomberman.graphics.Sprite;

public abstract class NonDestroyableTile extends Tile {

    public NonDestroyableTile(int x, int y, Sprite sprite) {
        super(x, y, sprite);
    }

    @Override
    public boolean isDestroyable() {
        return false;
    }
    
}
