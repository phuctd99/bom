package uet.oop.bomberman;

import uet.oop.bomberman.agent.Agent;
import uet.oop.bomberman.base.Copyable;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.base.IGameInfoManager;
import uet.oop.bomberman.base.ILevelManager;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.character.exceptions.CharacterActionException;
import uet.oop.bomberman.graphics.IRender;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.manager.EntityManager;
import uet.oop.bomberman.manager.GameInfoManager;
import uet.oop.bomberman.manager.LevelManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý thao tác điều khiển, load level, render các màn hình của game
 */
public class Board implements Copyable, IRender {
	protected Game _game;

	private List<Agent> agents = new ArrayList<>();

	private IEntityManager entityManager;
	private IGameInfoManager gameInfoManager;
	private ILevelManager levelManager;

	public Board(Game game, Screen screen) {
		_game = game;
		levelManager = new LevelManager(this);
		levelManager.loadGlobalLevel();
	}

	@Override
	public synchronized void update() {
		if (gameInfoManager.isPaused())
			return;

		entityManager.update();
		gameInfoManager.update();

		processAgentAction();
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
	public synchronized void render(Screen screen) {
		if (gameInfoManager.isPaused())
			return;
		if (gameInfoManager.getTime() <= 0) {
			levelManager.endGame();
		}
		entityManager.render(screen);
	}

	public synchronized void init() {
		gameInfoManager = new GameInfoManager();
		entityManager = new EntityManager(
			levelManager.getBoardWidth(),
			levelManager.getBoardHeight(),
			gameInfoManager,
			levelManager
		);
		gameInfoManager.setEntityManager(entityManager);
		gameInfoManager.pause();
		_game.setScreenToShow(2);
		_game.resetScreenDelay();
	}

	public void clear() {
		clearAgents();
	}

	public IEntityManager getEntityManager() {
		return entityManager;
	}

	public IGameInfoManager getGameInfoManager() {
		return gameInfoManager;
	}

	public ILevelManager getLevelManager() {
		return levelManager;
	}

	@Override
	public Board copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
