package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.graphics.Sprite;

public class Minvo extends Enemy {
    public Minvo(int x, int y, IEntityManager entityManager) {
        super(x, y, entityManager, Sprite.minvo_dead, Game.BOMBERSPEED * 1.5, 800);
        _sprite = Sprite.minvo_right1;
    }

    @Override
    protected void chooseSprite() {
        switch (getDirection()) {
            case 0:
            case 1:
                if (isMoving())
                    _sprite = Sprite.movingSprite(Sprite.minvo_right1, Sprite.minvo_right2, Sprite.minvo_right3,
                            _animate, Game.TICKS_PER_SECOND);
                else
                    _sprite = Sprite.minvo_left1;
                break;
            case 2:
            case 3:
                if (isMoving())
                    _sprite = Sprite.movingSprite(Sprite.minvo_left1, Sprite.minvo_left2, Sprite.minvo_left3, _animate,
                            Game.TICKS_PER_SECOND);
                else
                    _sprite = Sprite.minvo_left1;
                break;
        }
    }
}
