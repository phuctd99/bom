package uet.oop.bomberman;

import uet.oop.bomberman.agent.Agent;
import uet.oop.bomberman.base.Copyable;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.base.IGameInfoManager;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.character.exceptions.CharacterActionException;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.graphics.IRender;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.level.FileLevelLoader;
import uet.oop.bomberman.level.LevelLoader;
import uet.oop.bomberman.manager.EntityManager;
import uet.oop.bomberman.manager.GameInfoManager;
import uet.oop.bomberman.utils.Global;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý thao tác điều khiển, load level, render các màn hình của game
 */
public class Board implements Copyable, IRender {
	protected LevelLoader _levelLoader;
	protected Game _game;
	protected Screen _screen;

	private List<Agent> agents = new ArrayList<>();

	private IEntityManager entityManager;
	private IGameInfoManager gameInfoManager;

	public Board(Game game, Screen screen) {
		_game = game;
		_screen = screen;

		loadLevel(Global.gameLevel); // start in level 1
	}

	private void snapCameraToPlayer() {
		int xScroll = Screen.calculateXOffset(this, entityManager.getPlayer());
		Screen.setOffset(xScroll, 0);
	}

	@Override
	public void update() {
		if (gameInfoManager.isPaused())
			return;

		entityManager.update();
		gameInfoManager.update();

		processAgentAction();

		snapCameraToPlayer();
	}

	private void clearAgents() {
		agents.clear();
	}

	public void addAgent(Agent agent) {
		agents.add(agent);
	}

	private void processAgentAction() {

		for (Agent agent : agents) {
			List<Action> actions = agent.getNextActions();
			for (Action action : actions) {
				try {
					agent.getCharacter().performAction(action);
				} catch (CharacterActionException ignored) {
				}
			}
		}
	}

	@Override
	public void render(Screen screen) {
		if (gameInfoManager.isPaused())
			return;
		entityManager.render(screen);
	}

	public void nextLevel() {
		loadLevel(_levelLoader.getLevel() + 1);
	}

	public void loadLevel(int level) {
		try {
			clearAgents();
			_levelLoader = new FileLevelLoader(this, level);
			gameInfoManager = new GameInfoManager(_game);
			entityManager = new EntityManager(_levelLoader, gameInfoManager);
			gameInfoManager.setEntityManager(entityManager);
			gameInfoManager.pause();

			_levelLoader.createEntities();
		} catch (LoadLevelException e) {
			e.printStackTrace();
		}

		_game.setScreenToShow(2);
		_game.resetScreenDelay();
	}

	public int getWidth() {
		return _levelLoader.getWidth();
	}

	public int getHeight() {
		return _levelLoader.getHeight();
	}

	public IEntityManager getEntityManager() {
		return entityManager;
	}

	public IGameInfoManager getGameInfoManager() {
		return gameInfoManager;
	}

	@Override
	public Board copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
