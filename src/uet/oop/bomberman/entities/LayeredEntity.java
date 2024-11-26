package uet.oop.bomberman.entities;

import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.entities.tile.destroyable.DestroyableTile;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.graphics.Screen;

import java.util.LinkedList;

/**
 * Chứa và quản lý nhiều Entity tại cùng một vị trí
 * Ví dụ: tại vị trí dấu Item, có 3 Entity [Grass, Item, Brick]
 */
public class LayeredEntity extends Tile {
	
	protected LinkedList<Tile> _entities = new LinkedList<>();
	
	public LayeredEntity(int x, int y, Tile ... entities) {
		super(x, y, null);
		_x = x;
		_y = y;
		
		for (int i = 0; i < entities.length; i++) {
			_entities.add(entities[i]); 
			
			if(i > 1) {
				if(entities[i] instanceof DestroyableTile)
					((DestroyableTile)entities[i]).addBelowSprite(entities[i-1].getSprite());
			}
		}
	}
	
	@Override
	public void update() {
		clearRemoved();
		getTopEntity().update();
	}
	
	@Override
	public void render(Screen screen) {
		getTopEntity().render(screen);
	}
	
	public Tile getTopEntity() {
		
		return _entities.getLast();
	}
	
	private void clearRemoved() {
		Entity top  = getTopEntity();
		
		if(top.isRemoved())  {
			_entities.removeLast();
		}
	}
	
	public void addBeforeTop(Tile e) {
		_entities.add(_entities.size() - 1, e);
	}

	public boolean hasItem() {
		for (Tile e : _entities) {
			if(e instanceof Item) return true;
		}
		return false;
	}
	
	@Override
	public boolean collide(Entity e) {
		// TODO: lấy entity trên cùng ra để xử lý va chạm
		return getTopEntity().collide(e);
	}

	@Override
	public boolean canBePassedThroughBy(Entity other) {
		return getTopEntity().canBePassedThroughBy(other);
	}

	@Override
	public boolean isDestroyable() {
		return getTopEntity().isDestroyable();
	}

}
