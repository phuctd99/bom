package uet.oop.bomberman.manager;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.ITileManager;
import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.graphics.Screen;

public class TileManager implements ITileManager {

    private int width;
    @SuppressWarnings("unused") private int height;
    private final Tile[] tiles;

    public TileManager(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width * height];
    }

	@Override
	public Tile getTileAt(double x, double y) {
		return tiles[(int) x + (int) y * width];
	}

	@Override
	public void addTile(int pos, Tile e) {
		tiles[pos] = e;
	}

    @Override
    public void update() {
        for (Tile tile: tiles) {
            tile.update();
        }
    }

    @Override
    public void render(Screen screen) {
		// only render the visible part of screen
		int x0 = Screen.xOffset / Game.TILES_SIZE; // tile precision, -> left X
		int x1 = (Screen.xOffset + screen.getWidth() + Game.TILES_SIZE) / Game.TILES_SIZE; // -> right X
		int y0 = Screen.yOffset / Game.TILES_SIZE;
		int y1 = (Screen.yOffset + screen.getHeight()) / Game.TILES_SIZE; // render one tile plus to fix black margins

		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				tiles[x + y * width].render(screen);
			}
		}
    }
    
}
