package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.graphics.Sprite;

public class Oneal extends Enemy {
	// private Random random = new Random();
	public Oneal(int x, int y, IEntityManager entityManager) {
		super(x, y, entityManager, Sprite.balloom_dead, Game.BOMBERSPEED, 100);

		_sprite = Sprite.oneal_left1;
	}

	@Override
	protected void chooseSprite() {
		switch (getDirection()) {
			case 0:
			case 1:
				if (isMoving())
					_sprite = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3,
							_animate, Game.TICKS_PER_SECOND);
				else
					_sprite = Sprite.oneal_left1;
				break;
			case 2:
			case 3:
				if (isMoving())
					_sprite = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, _animate,
							Game.TICKS_PER_SECOND);
				else
					_sprite = Sprite.oneal_left1;
				break;
		}
	}
}
