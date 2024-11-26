package uet.oop.bomberman.base;

import java.util.List;

import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.graphics.IRender;

public interface IBombManager extends IRender {

    public List<Bomb> getBombs();
    public Bomb getBombAt(double x, double y);
    public void addBomb(Bomb e);
    public FlameSegment getFlameSegmentAt(int x, int y);

}