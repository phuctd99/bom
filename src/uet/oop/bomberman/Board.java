package uet.oop.bomberman;

import uet.oop.bomberman.agent.base.Agent;
import uet.oop.bomberman.agent.base.RewardBasedAgent;
import uet.oop.bomberman.base.Copyable;
import uet.oop.bomberman.base.IBombManager;
import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.base.IGameInfoManager;
import uet.oop.bomberman.base.ILevelManager;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.character.action.ActionConstants;
import uet.oop.bomberman.entities.character.exceptions.ActionOnCooldownException;
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

	private IBombManager bombManager;

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

	public void setLevelManager(ILevelManager levelManager) {
		this.levelManager = levelManager;
		levelManager.loadGlobalLevel();
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
					if (action == ActionConstants.DO_NOTHING) {
						// Penalize the agent for doing nothing while they can perform something else
						// for (Action performableAction: agent.getCharacter().getPerformableActions()) {
						// 	if (performableAction != ActionConstants.DO_NOTHING) {
						// 		if (agent instanceof RewardBasedAgent) {
						// 			((RewardBasedAgent)agent).addReward(-10);
						// 		}
						// 		break;
						// 	}
						// }
					}
				} catch (ActionOnCooldownException ex) {
					// Penalize the agent for spamming actions
					if (agent instanceof RewardBasedAgent) {
						System.out.println(
							"Character "
							+ agent.getCharacter().getClass().getSimpleName()
							+ " action on cooldown: "
							+ action.getClass().getSimpleName()
						);
						((RewardBasedAgent)agent).addReward(-10);
					}
				} catch (CharacterActionException ignored) {
					// Penalize the agent for doing invalid actions
					if (agent instanceof RewardBasedAgent) {
						((RewardBasedAgent)agent).addReward(-10);
					}
				}
			}
		}
	}

	public void handleWinLevel() {
		for (Agent agent : agents) {
			if (agent instanceof RewardBasedAgent) {
				((RewardBasedAgent)agent).handleWinLevel();
			}
		}
	}

	public void handleLoseLevel() {
		for (Agent agent : agents) {
			if (agent instanceof RewardBasedAgent) {
				((RewardBasedAgent)agent).handleLoseLevel();
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
				levelManager);
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

	public IBombManager getBombManager() {
		return bombManager;
	}

	@Override
	public Board copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
