package uet.oop.bomberman.entities.tile;

import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.base.ILevelManager;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.sound.Sound;

public class Portal extends NonDestroyableTile {
	protected ILevelManager levelManager;
	protected IEntityManager entityManager;

	public Portal(int x, int y, ILevelManager levelManager, IEntityManager entityManager, Sprite sprite) {
		super(x, y, sprite);
		this.levelManager = levelManager;
		this.entityManager = entityManager;
	}

	@Override
	public boolean collide(Entity e) {// xu li khi 2 entity va cham
										// true cho di qua
										// false khong cho di qua
		// TODO: xử lý khi Bomber đi vào
		if (e instanceof Character && ((Character) e).isPlayer()) {

			if (canBePassedThroughBy(e)) {
				levelManager.nextLevel();
				Sound.play("CRYST_UP");
			}

		}

		return true;
	}

	@Override
	public boolean canBePassedThroughBy(Entity other) {
		if (other instanceof Character && ((Character) other).isPlayer()) {

			if (!entityManager.isEnemyCleared())
				return false;

			return true;
		}
		return true;
	}

}
