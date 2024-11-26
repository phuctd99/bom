package uet.oop.bomberman.manager;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.base.ILevelManager;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.level.FileLevelLoader;
import uet.oop.bomberman.level.LevelLoader;
import uet.oop.bomberman.utils.EGameMode;
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
            calculateEnemies();
        } catch (LoadLevelException e) {
            e.printStackTrace();
        }
        synchronized (board) {
            board.init();
            levelLoader.createEntities();
        }
    }

    private void calculateEnemies() {
        int level = Global.gameLevel;
        EGameMode gameMode = Global.gameMode;
        if(level == 1 && gameMode == EGameMode.ONE_PLAYER) {
            Global.enemies = 1;
        } else if(level == 2 && gameMode == EGameMode.ONE_PLAYER) {
            Global.enemies = 3;
        } else if(level == 3 && gameMode == EGameMode.ONE_PLAYER) {
            Global.enemies = 6;
        } else if(level == 1 && gameMode == EGameMode.TWO_PLAYER) {
            Global.enemies = 1;
        } else if(level == 2 && gameMode == EGameMode.TWO_PLAYER) {
            Global.enemies = 3;
        } else if(level == 3 && gameMode == EGameMode.TWO_PLAYER) {
            Global.enemies = 6;
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
