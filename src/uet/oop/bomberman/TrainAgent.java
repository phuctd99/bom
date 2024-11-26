package uet.oop.bomberman;

import uet.oop.bomberman.gui.Frame;
import uet.oop.bomberman.manager.LevelManager;
import uet.oop.bomberman.utils.EScreenName;
import uet.oop.bomberman.utils.Global;

public class TrainAgent {

    public static void main(String[] args) {
        new TrainAgent();
    }

    private TrainAgent() {
        Global.isAIPlayer = true;
        Frame frame = new Frame();
        Game game = frame._gamepane.getGame();
        Global.currentScreen = EScreenName.GAME_PLAY_SCREEN;
        game.getBoard().setLevelManager(new LoopingLevelManager(game.getBoard()));
        game.headless = true;
        frame.setVisible(true);
        frame.start();
    }

    private class LoopingLevelManager extends LevelManager {
        public LoopingLevelManager(Board board) {
            super(board);
        }

        @Override
        public void endGame() {
            board.handleLoseLevel();
            // Restart level
            loadGlobalLevel();
        }

        @Override
        public void nextLevel() {
            board.handleWinLevel();
            // Restart level
            loadGlobalLevel();
        }

        
    }

}
