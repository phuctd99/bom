package uet.oop.bomberman.base;

public interface IGameInfoManager extends IActiveItemManager {

    public int subtractTime();
    public int getTime();

    public int getPoints();
    public void addPoints(int points);

}