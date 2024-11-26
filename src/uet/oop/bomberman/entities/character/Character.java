package uet.oop.bomberman.entities.character;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.entities.AnimatedEntitiy;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.character.action.ActionConstants;
import uet.oop.bomberman.entities.character.action.ActionMove;
import uet.oop.bomberman.entities.character.exceptions.ActionOnCooldownException;
import uet.oop.bomberman.entities.character.exceptions.CannotPerformActionException;
import uet.oop.bomberman.entities.character.exceptions.CharacterActionException;
import uet.oop.bomberman.entities.character.exceptions.InvalidActionException;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.level.Coordinates;

/**
 * Bao gồm Bomber và Enemy
 */
public abstract class Character extends AnimatedEntitiy {

	protected IEntityManager entityManager;

	private int direction = -1;
	private boolean alive = true;
	private int timerDeathAnimation = 40;

	private final double baseSpeed;
	private Queue<Waypoint> waypoints = new LinkedList<>();

	public Character(int x, int y, double baseSpeed, IEntityManager entityManager) {
		_x = x;
		_y = y;
		this.entityManager = entityManager;
		this.baseSpeed = baseSpeed;
	}

	@Override
	public final void update() {
		if (!alive) {
			if (timerDeathAnimation > 0) {
				timerDeathAnimation -= 1;
			} else {
				handleAfterDeath();
			}
			return;
		}
		updateMove();
		handleUpdate();
	};

	protected abstract void handleUpdate();

	@Override
	public abstract void render(Screen screen);

	private static final List<Action> VALID_ACTIONS = new ArrayList<Action>() {
		{
			addAll(ActionConstants.LIST_ACTION_MOVE);
			add(ActionConstants.DO_NOTHING);
		}
	};

	public List<Action> getValidActions() {
		return VALID_ACTIONS;
	}

	public boolean isValidAction(Action action) {
		return getValidActions().contains(action);
	}

	public final void performAction(Action action) throws InvalidActionException, CannotPerformActionException {
		performAction(action, false);
	}

	protected void performAction(Action action, boolean isDryRun)
			throws InvalidActionException, CannotPerformActionException {
		if (!isValidAction(action))
			throw new InvalidActionException();
		if (action instanceof ActionMove) {
			ActionMove actionMove = (ActionMove) action;
			double dx = actionMove.getDx() * Game.TILES_SIZE;
			double dy = actionMove.getDy() * Game.TILES_SIZE;
			if (isMoving())
				throw new ActionOnCooldownException();
			if (!isDryRun)
				move(dx, dy);
		}
	};

	public final boolean canPerformAction(Action action) {
		try {
			performAction(action, true);
			return true;
		} catch (CharacterActionException ex) {
			return false;
		}
	};

	public List<Action> getPerformableActions() {
		return getValidActions().stream()
				.filter(this::canPerformAction)
				.collect(Collectors.toList());
	}

	/**
	 * Check if can be moved with vector (xa, ya).
	 * 
	 * @param xa
	 * @param ya
	 */
	public void move(double xa, double ya) {
		double moveDurationBase = Game.TICKS_PER_SECOND / getSpeed();
		Waypoint waypointX = new Waypoint(
				xa,
				0,
				moveDurationBase);
		Waypoint waypointY = new Waypoint(
				0,
				ya,
				moveDurationBase);
		if (xa != 0 && ya != 0 && canMove(xa, ya) && canMove(xa, 0)) {
			waypoints.add(waypointX);
			waypoints.add(waypointY);
		} else if (xa != 0 && ya != 0 && canMove(xa, ya) && canMove(0, ya)) {
			waypoints.add(waypointY);
			waypoints.add(waypointX);
		} else if (xa != 0 && canMove(xa, 0)) {
			waypoints.add(waypointX);
		} else if (ya != 0 && canMove(0, ya)) {
			waypoints.add(waypointY);
		} else {
			System.out.println(String.format(
					"Cannot move character %s to (%s, %s)",
					getClass().getSimpleName(), xa, ya));
		}
	}

	private void updateMove() {
		if (waypoints.isEmpty())
			return;
		Waypoint waypoint = waypoints.peek();
		if (!waypoint.started) {
			waypoint.started = true;
			waypoint.moveDestX = _x + waypoint.moveX;
			waypoint.moveDestY = _y + waypoint.moveY;
		}

		waypoint.moveDuration -= 1;
		if (waypoint.moveDuration > 0) {
			_x += waypoint.moveDx;
			_y += waypoint.moveDy;
		} else {
			// Correct rounding errors by force teleporting to destination
			_x = waypoint.moveDestX;
			_y = waypoint.moveDestY;
			// Remove waypoint
			waypoints.poll();
		}

		// Adjust direction
		if (waypoint.moveDx > 0)
			direction = 1;
		if (waypoint.moveDx < 0)
			direction = 3;
		if (waypoint.moveDy > 0)
			direction = 2;
		if (waypoint.moveDy < 0)
			direction = 0;

		Entity collidingEntity = entityManager.getEntityAtExcluding(
				Coordinates.pixelToTile(getCenterX()),
				Coordinates.pixelToTile(getCenterY()),
				this);
		if (collidingEntity != null) {
			this.collide(collidingEntity);
			collidingEntity.collide(this);
		}
	}

	/**
	 * Được gọi khi đối tượng bị tiêu diệt
	 */
	public final void handleOnDeath() {
		if (!alive)
			return;
		alive = false;
		// TODO: determine killer
		entityManager.getCharacterManager().handleOnDeath(this, null);
	}

	/**
	 * Xử lý hiệu ứng bị tiêu diệt
	 */
	private void handleAfterDeath() {
		entityManager.getCharacterManager().handleAfterDeath(this);
		remove();
	}

	/**
	 * Kiểm tra xem đối tượng có di chuyển tới vị trí đã tính toán hay không
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canMove(double dx, double dy) {
		double x = getCenterX() + dx;
		double y = getCenterY() + dy;
		Entity a = entityManager.getEntityAtExcluding(
				Coordinates.pixelToTile(x),
				Coordinates.pixelToTile(y),
				this);
		if (a == null)
			return true;
		return a.canBePassedThroughBy(this);
	}

	protected double getXMessage() {
		return (_x * Game.SCALE) + (_sprite.SIZE / 2 * Game.SCALE);
	}

	protected double getYMessage() {
		return (_y * Game.SCALE) - (_sprite.SIZE / 2 * Game.SCALE);
	}

	public boolean isPlayer() {
		return entityManager.getPlayers().contains(this);
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isMoving() {
		return waypoints.size() > 0;
	}

	public final double getSpeed() {
		double speedMultiplier = getSpeedMultiplier();
		return speedMultiplier * this.baseSpeed;
	}

	protected double getSpeedMultiplier() {
		return 1;
	}

	public abstract int getPoints();

	public int getDirection() {
		return direction;
	}

	public int getTimerDeathAnimation() {
		return timerDeathAnimation;
	}

	public void setTimerDeathAnimation(int timerDeathAnimation) {
		this.timerDeathAnimation = timerDeathAnimation;
	}

	@Override
	public boolean collide(Entity e) {
		if (e instanceof Flame) {
			this.handleOnDeath();
			return false;
		}
		if (e instanceof Character) {
			Character other = (Character) e;
			if (this.isPlayer() && !other.isPlayer()) {
				this.handleOnDeath();
				return false;
			}
			if (other.isPlayer() && !this.isPlayer()) {
				other.handleOnDeath();
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canBePassedThroughBy(Entity other) {
		return true;
	}

}
