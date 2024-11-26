package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.graphics.Sprite;

public class Balloon extends Enemy {
	
	
	public Balloon(int x, int y, IEntityManager entityManager) {
		super(x, y, entityManager, Sprite.balloom_dead, Game.BOMBERSPEED / 2, 100);
		
		_sprite = Sprite.balloom_left1;
		
	}

	@Override
	protected void chooseSprite() {
		switch(getDirection()) {
			case 0:
			case 1:
					_sprite = Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, _animate, Game.TICKS_PER_SECOND);
				break;
			case 2:
			case 3:
					_sprite = Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, _animate, Game.TICKS_PER_SECOND);
				break;
		}
	}
}
