package uet.oop.bomberman.entities.character;

import java.util.ArrayList;
import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;

import java.util.Iterator;
import java.util.List;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.item.BombItem;
import uet.oop.bomberman.entities.tile.item.FlameItem;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.level.Coordinates;
import uet.oop.bomberman.sound.Sound;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    public static List<Item> _items = new ArrayList<Item>();//xu li Item

    private final int baseBombLimit;
    protected int bombCooldown = 0;

    private final int baseBombRadius;

    public int getBombCooldown() {
        return bombCooldown;
    }

    private Board _board;

    public Bomber(int x, int y, double baseSpeed, int baseBombLimit, int baseBombRadius, IEntityManager entityManager, Board board) {
        super(x, y, baseSpeed, entityManager);
        this.baseBombLimit = baseBombLimit;
        this.baseBombRadius = baseBombRadius;
        this._board = board;
        _bombs = entityManager.getBombs();
        _sprite = Sprite.player_right;
    }

    @Override
    public void handleUpdate() {
        clearExpiredBombs();
        if (bombCooldown < -7500) bombCooldown = 0;
        else bombCooldown--;
        animate();

    }

    @Override
    public void render(Screen screen) {
        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    public int getBombLimit() {
        int countActiveItem = (int) getActiveItems().filter(item -> item instanceof BombItem).count();
        int bombLimitBonus = countActiveItem * BombItem.BOMB_LIMIT_BONUS;
        return this.baseBombLimit + bombLimitBonus;
    }

    public int getBombRemainingQuota() {
        return getBombLimit() - _bombs.size();
    }

    public int getBombRadius() {
        int countActiveItem = (int) getActiveItems().filter(item -> item instanceof FlameItem).count();
        int bombRadiusBonus = countActiveItem * FlameItem.BOMB_RADIUS_BONUS;
        return this.baseBombRadius + bombRadiusBonus;
    }

    public boolean placeBomb() {
        if(getBombRemainingQuota() > 0 && bombCooldown < 0) {
			
			int xt = Coordinates.pixelToTile(_x + _sprite.getSize() / 2);
			int yt = Coordinates.pixelToTile( (_y + _sprite.getSize() / 2) - _sprite.getSize() ); //subtract half player height and minus 1 y position
			
			placeBomb(xt,yt);
			
			bombCooldown = 30;
            return true;
		}
        return false;
    }

    public void placeBomb(int x, int y) {
        // TODO: thực hiện tạo đối tượng bom, đặt vào vị trí (x, y)
        Bomb b = new Bomb(x, y, getBombRadius(), entityManager);
        entityManager.addBomb(b);
        Sound.play("BOM_SET");
    }

    private void clearExpiredBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
            }
        }

    }

    @Override
    protected void handleAfterDeath() {
        _board.endGame();
    }

    @Override
    protected void calculateMove() {
    }

    @Override
    public boolean canMove(double x, double y) {
        // TODO: kiểm tra có đối tượng tại vị trí chuẩn bị di chuyển đến và có thể di chuyển tới đó hay không
       for (int c = 0; c < 4; c++) { //colision detection for each corner of the player
			double xt = ((_x + x) + c % 2 * 9) / Game.TILES_SIZE; //divide with tiles size to pass to tile coordinate
			double yt = ((_y + y) + c / 2 * 10 - 13) / Game.TILES_SIZE; //these values are the best from multiple tests
			
			Entity a = entityManager.getEntity(xt, yt, this);
			
			if(!a.canBePassedThroughBy(this))
				return false;
		}
		
		return true;
        //return false;
    }

    @Override
    public boolean collide(Entity e) {
        if (!super.collide(e)) return false;
        return true;
    }

    //sprite
    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }

    @Override
    public int getPoints() {
        return 0;
    }
}
