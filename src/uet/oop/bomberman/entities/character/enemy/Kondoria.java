package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.graphics.Sprite;

public class Kondoria extends Enemy {
    public Kondoria(int x, int y, IEntityManager entityManager) {
        super(x, y, entityManager, Sprite.balloom_dead, Game.BOMBERSPEED / 4, 1000);

        _sprite = Sprite.kondoria_right1;

    }

    @Override
    protected void chooseSprite() {
        switch (getDirection()) {
            case 0:
            case 1:
                if (isMoving())
                    _sprite = Sprite.movingSprite(Sprite.kondoria_right1, Sprite.kondoria_right2,
                            Sprite.kondoria_right3, _animate, Game.TICKS_PER_SECOND);
                else
                    _sprite = Sprite.kondoria_left1;
                break;
            case 2:
            case 3:
                if (isMoving())
                    _sprite = Sprite.movingSprite(Sprite.kondoria_left1, Sprite.kondoria_left2, Sprite.kondoria_left3,
                            _animate, Game.TICKS_PER_SECOND);
                else
                    _sprite = Sprite.kondoria_left1;
                break;
        }
    }
}
