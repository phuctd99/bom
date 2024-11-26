package uet.oop.bomberman.base;

public interface ILevelManager {
    
    public void nextLevel();

    public void loadGlobalLevel();

    public void endGame();

    public int getBoardWidth();
    public int getBoardHeight();

}
