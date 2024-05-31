package uet.oop.bomberman.entities.character;

import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;

public class Bomber2 extends Bomber {

    public Bomber2(int x, int y, double baseSpeed, int baseBombLimit, int baseBombRadius,
            IEntityManager entityManager) {
        super(x, y, baseSpeed, baseBombLimit, baseBombRadius, entityManager);
    }

    @Override
    public void render(Screen screen) {
        if (isAlive())
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    @Override
    protected void chooseSprite() {
        switch (getDirection()) {
            case 0:
                _sprite = Sprite.player2_up;
                if (isMoving()) {
                    _sprite = Sprite.movingSprite(Sprite.player2_up_1, Sprite.player2_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player2_right;
                if (isMoving()) {
                    _sprite = Sprite.movingSprite(Sprite.player2_right_1, Sprite.player2_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player2_down;
                if (isMoving()) {
                    _sprite = Sprite.movingSprite(Sprite.player2_down_1, Sprite.player2_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player2_left;
                if (isMoving()) {
                    _sprite = Sprite.movingSprite(Sprite.player2_left_1, Sprite.player2_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player2_right;
                if (isMoving()) {
                    _sprite = Sprite.movingSprite(Sprite.player2_right_1, Sprite.player2_right_2, _animate, 20);
                }
                break;
        }
    }
}
