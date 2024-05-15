package uet.oop.bomberman.entities.tile.item;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.sound.Sound;

public abstract class Item extends Tile {
	protected int _duration = 300; // 5s
	protected boolean _active = false;
	protected int _level;

	public Item(int x, int y, Sprite sprite) {
		super(x, y, sprite);
	}

	protected abstract void handleItemActive();

	protected abstract void handleItemInactive();

	@Override
	public boolean collide(Entity e) {
		// TODO: xử lý Bomber ăn Item
		if (e instanceof Bomber) {
			Sound.play("Item");
			handleItemActive();
			_active = true;
			Game.getBoard().addActiveItem(this);
			remove();
		}
		return false;
	}

	@Override
	public void update() {
		if (!_active)
			return;
		if (_duration > 0) {
			_duration--;
		} else {
			handleItemInactive();
			_active = false;
		}
	}

	public int getDuration() {
		return _duration;
	}

	public  abstract String getDisplayActiveItem();
}
