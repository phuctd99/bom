package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.graphics.Screen;

public class Flame extends Entity {

	protected IEntityManager entityManager;
	protected int _direction;
	private int _radius;
	protected int xOrigin, yOrigin;
	protected FlameSegment[] _flameSegments = new FlameSegment[0];

	/**
	 *
	 * @param x hoành độ bắt đầu của Flame
	 * @param y tung độ bắt đầu của Flame
	 * @param direction là hướng của Flame
	 * @param radius độ dài cực đại của Flame
	 */
	public Flame(int x, int y, int direction, int radius, IEntityManager entityManager) {
		xOrigin = x;
		yOrigin = y;
		_x = x;
		_y = y;
		_direction = direction;
		_radius = radius;
		this.entityManager = entityManager;
		createFlameSegments();
	}

	/**
	 * Tạo các FlameSegment, mỗi segment ứng một đơn vị độ dài
	 */
	private void createFlameSegments() {
		/**
		 * tính toán độ dài Flame, tương ứng với số lượng segment
		 */
		_flameSegments = new FlameSegment[calculatePermitedDistance()];

		/**
		 * biến last dùng để đánh dấu cho segment cuối cùng
		 */

		// TODO: tạo các segment dưới đây
                boolean last = false;
		
		int x = (int)_x;
		int y = (int)_y;
		for (int i = 0; i < _flameSegments.length; i++) {
			last = i == _flameSegments.length -1 ? true : false;
			
			switch (_direction) {
				case 0: y--; break;
				case 1: x++; break;
				case 2: y++; break;
				case 3: x--; break;
			}
			_flameSegments[i] = new FlameSegment(x, y, _direction, last);
			Entity entity = entityManager.getEntityAt(x, y);
			if (entity!=null) {
				entity.collide(this);
			}
		}
	}

	/**
	 * Tính toán độ dài của Flame, nếu gặp vật cản là Brick/Wall, độ dài sẽ bị cắt ngắn
	 * @return
	 */
	private int calculatePermitedDistance() {
		// TODO: thực hiện tính toán độ dài của Flame
                int radius = 0;
		int x = (int)_x;
		int y = (int)_y;
		while(radius < _radius) {
			if(_direction == 0) y--;
			if(_direction == 1) x++;
			if(_direction == 2) y++;
			if(_direction == 3) x--;
			
			Entity a = entityManager.getEntityAt(x, y);
			
			if(a instanceof Bomb) ++radius; //explosion has to be below the bom
			
			if(!canSpawnFlameOn(a)) {
				break;
			}
			
			++radius;

			// Stop if encounter brick
			if (!a.canBePassedThroughBy(this)) {
				break;
			}
		}
		return radius;
	}

	private boolean canSpawnFlameOn(Entity entity) {
		if (entity.canBePassedThroughBy(this)) return true;
		if (entity instanceof Tile) {
			Tile tile = (Tile) entity;
			if (tile.isDestroyable()) return true;
		}
		return false;
	}
	
	public FlameSegment flameSegmentAt(int x, int y) {
		for (int i = 0; i < _flameSegments.length; i++) {
			if(_flameSegments[i].getX() == x && _flameSegments[i].getY() == y)
				return _flameSegments[i];
		}
		return null;
	}

	@Override
	public void update() {}
	
	@Override
	public void render(Screen screen) {
		for (int i = 0; i < _flameSegments.length; i++) {
			_flameSegments[i].render(screen);
		}
	}

	@Override
	public boolean collide(Entity e) {
		if (e instanceof Character) {
			((Character)e).handleOnDeath();
		}
		return true;
	}

	@Override
	public boolean canBePassedThroughBy(Entity other) {
		return true;
	}
	
}
