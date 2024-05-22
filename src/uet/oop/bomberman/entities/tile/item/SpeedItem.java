package uet.oop.bomberman.entities.tile.item;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.graphics.Sprite;

public class SpeedItem extends Item {

	public static final double SPEED_MULTIPLIER = 0.5;

	public SpeedItem(int x, int y, Sprite sprite) {
		super(x, y, sprite);
	}

	@Override
	protected void handleItemActive() {
	}

	@Override
	protected void handleItemInactive() {
	}

	@Override
	public String getDisplayActiveItem() {
		return "Speed:";
	}
}
