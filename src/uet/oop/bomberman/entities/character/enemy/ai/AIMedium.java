package uet.oop.bomberman.entities.character.enemy.ai;

import uet.oop.bomberman.base.ICharacterManager;
import uet.oop.bomberman.entities.character.Character;

public class AIMedium extends AI {

	private final Character character;
	private final ICharacterManager characterManager;
	
	public AIMedium(Character character, ICharacterManager entityManager) {
		this.character = character;
		this.characterManager = entityManager;
	}

	@Override
	public int calculateDirection() {

		int vertical = random.nextInt(2);
		
		if(vertical == 1) {
			int v = calculateRowDirection();
			if(v != -1)
				return v;
			else
				return calculateColDirection();
			
		} else {
			int h = calculateColDirection();
			
			if(h != -1)
				return h;
			else
				return calculateRowDirection();
		}
	}

	protected int calculateColDirection() {
		Character player = characterManager.getPlayer();

		if(player.getXTile() < character.getXTile())
			return 3;
		else if(player.getXTile() > character.getXTile())
			return 1;
		
		return -1;
	}
	
	protected int calculateRowDirection() {
		Character player = characterManager.getPlayer();

		if(player.getYTile() < character.getYTile())
			return 0;
		else if(player.getYTile() > character.getYTile())
			return 2;
		return -1;
	}

}
