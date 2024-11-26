package uet.oop.bomberman.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import uet.oop.bomberman.base.IBombManager;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.graphics.Screen;

public class BombManager implements IBombManager {

    private List<Bomb> bombs = new ArrayList<>();

    public BombManager() {
    }

	@Override
	public void addBomb(Bomb e) {
		bombs.add(e);
	}

	@Override
	public List<Bomb> getBombs() {
		return bombs;
	}

	@Override
	public Bomb getBombAt(double x, double y) {
		Iterator<Bomb> bs = bombs.iterator();
		Bomb b;
		while (bs.hasNext()) {
			b = bs.next();
			if (b.getX() == (int) x && b.getY() == (int) y)
				return b;
		}

		return null;
	}

	@Override
	public FlameSegment getFlameSegmentAt(int x, int y) {
		Iterator<Bomb> bs = bombs.iterator();
		Bomb b;
		while (bs.hasNext()) {
			b = bs.next();

			FlameSegment e = b.flameAt(x, y);
			if (e != null) {
				return e;
			}
		}

		return null;
	}

	@Override
	public void update() {
		bombs.forEach(Bomb::update);
		bombs = bombs.stream()
			.filter(bomb -> !bomb.isRemoved())
			.collect(Collectors.toList());
	}

	@Override
	public void render(Screen screen) {
		bombs.forEach(bomb -> bomb.render(screen));
	}

}
