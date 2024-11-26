package uet.oop.bomberman.entities.tile;


import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Sprite;

public class Grass extends NonDestroyableTile {

	public Grass(int x, int y, Sprite sprite) {
		super(x, y, sprite);
	}

	/**
	 * Cho bất kì đối tượng khác đi qua
	 * @param e
	 * @return
	 */
	@Override
	public boolean collide(Entity e) {
		return true;
	}

	@Override
	public boolean canBePassedThroughBy(Entity other) {
		return true;
	}
}
