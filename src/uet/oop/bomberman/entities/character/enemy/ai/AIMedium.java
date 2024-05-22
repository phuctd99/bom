package uet.oop.bomberman.entities.character.enemy.ai;

import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.enemy.Enemy;

public class AIMedium extends AI {
	Character player;
	Enemy _e;
	
	public AIMedium(Character player, Enemy e) {
		this.player = player;
		_e = e;
	}

	@Override
	public int calculateDirection() {
		// TODO: cài đặt thuật toán tìm đường đi
                if(player == null)
			return random.nextInt(4);
		
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
		if(player.getXTile() < _e.getXTile())
			return 3;
		else if(player.getXTile() > _e.getXTile())
			return 1;
		
		return -1;
	}
	
	protected int calculateRowDirection() {
		if(player.getYTile() < _e.getYTile())
			return 0;
		else if(player.getYTile() > _e.getYTile())
			return 2;
		return -1;
	}

}
