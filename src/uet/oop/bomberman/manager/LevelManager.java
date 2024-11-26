package uet.oop.bomberman.manager;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.base.ILevelManager;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.level.FileLevelLoader;
import uet.oop.bomberman.level.LevelLoader;
import uet.oop.bomberman.utils.EScreenName;
import uet.oop.bomberman.utils.Global;

public class LevelManager implements ILevelManager {

    private LevelLoader levelLoader;
    private Board board;

    public LevelManager(Board board) {
        this.board = board;
    }

    @Override
    public void nextLevel() {
        Global.gameLevel += 1;
        loadGlobalLevel();
    }

    @Override
    public void loadGlobalLevel() {
        loadLevel(Global.gameLevel);
    }

    private void loadLevel(int level) {
        board.clear();
        try {
            levelLoader = new FileLevelLoader(board, level);
        } catch (LoadLevelException e) {
            e.printStackTrace();
        }
        synchronized (board) {
            board.init();
            levelLoader.createEntities();
        }
    }

    @Override
    public void endGame() {
        Global.currentScreen = EScreenName.END_GAME_SCREEN;
        board.getGameInfoManager().pause();
    }

    @Override
    public int getBoardWidth() {
        return levelLoader.getWidth();
    }

    @Override
    public int getBoardHeight() {
        return levelLoader.getHeight();
    }
    
}
