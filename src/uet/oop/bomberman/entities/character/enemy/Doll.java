/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.graphics.Sprite;

public class Doll extends Enemy {

    public Doll(int x, int y, IEntityManager entityManager) {
        super(x, y, entityManager, Sprite.balloom_dead, Game.BOMBERSPEED, 100);

        _sprite = Sprite.doll_left1;

    }

    @Override
    protected void chooseSprite() {
        switch (getDirection()) {
            case 0:
            case 1:
                if (isMoving()) {
                    _sprite = Sprite.movingSprite(Sprite.doll_right1, Sprite.doll_right2, Sprite.doll_right3, _animate,
                            Game.TICKS_PER_SECOND);
                } else {
                    _sprite = Sprite.doll_left1;
                }
                break;
            case 2:
            case 3:
                if (isMoving()) {
                    _sprite = Sprite.movingSprite(Sprite.doll_left1, Sprite.doll_left2, Sprite.doll_left3, _animate,
                            Game.TICKS_PER_SECOND);
                } else {
                    _sprite = Sprite.doll_left1;
                }
                break;
        }
    }
}
