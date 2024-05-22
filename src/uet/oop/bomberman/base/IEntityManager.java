package uet.oop.bomberman.base;

import java.util.List;

import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Character;

public interface IEntityManager extends IActiveItemManager {

    public List<Bomb> getBombs();

    public Entity getEntity(double x, double y, Character m);
    public Entity getEntityAt(double x, double y);
    public Bomb getBombAt(double x, double y);
    public Character getCharacterAtExcluding(int x, int y, Character a);
    public FlameSegment getFlameSegmentAt(int x, int y);

    public void addEntity(int pos, Entity e);
    public void addCharacter(Character e);
    public void addBomb(Bomb e);

    public void setPlayer(Character character);
    public Character getPlayer();

    public boolean isEnemyCleared();
    public void handleOnDeath(Character character, Character killer);

}