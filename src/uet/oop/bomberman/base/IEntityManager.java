package uet.oop.bomberman.base;

import java.util.List;

import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.graphics.IRender;

public interface IEntityManager extends IRender {

    public Entity getEntityAtExcluding(double x, double y, Character m);

    public default Entity getEntityAt(double x, double y) {
        return getEntityAtExcluding(x, y, null);
    };

    public boolean isEnemyCleared();

    public Character getPlayer();

    public List<Character> getPlayers();

    public ITileManager getTileManager();

    public ICharacterManager getCharacterManager();

    public IBombManager getBombManager();

}