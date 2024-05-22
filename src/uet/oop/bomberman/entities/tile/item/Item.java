package uet.oop.bomberman.entities.tile.item;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.tile.NonDestroyableTile;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.sound.Sound;

public abstract class Item extends NonDestroyableTile {
	protected int _duration = 30 * Game.TICKS_PER_SECOND; // 30s
	protected boolean _active = false;

	protected int _level;

	public Item(int x, int y, Sprite sprite) {
		super(x, y, sprite);
	}

	protected abstract void handleItemActive();

	protected abstract void handleItemInactive();

	@Override
	public boolean collide(Entity e) {
		if (isRemoved()) return false;
		if (e instanceof Character && ((Character)e).isPlayer()) {
			Character player = (Character) e;
			Sound.play("Item");
			handleItemActive();
			_active = true;
			player.addActiveItem(this);
			remove();
		}
		return false;
	}

	@Override
	public boolean canBePassedThroughBy(Entity e) {
		return (e instanceof Character && ((Character)e).isPlayer());
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

	public abstract String getDisplayActiveItem();

	public boolean isActive() {
		return _active;
	}
}
