package uet.oop.bomberman.agent;

import java.util.ArrayList;
import java.util.List;

import uet.oop.bomberman.agent.base.Agent;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.character.action.ActionConstants;
import uet.oop.bomberman.entities.character.enemy.ai.AI;

public class MovingAgent extends Agent {
    
    private AI ai;

    public MovingAgent(Character character, AI ai) {
        super(character);
        this.ai = ai;
    }

    public Action getNextAction() {
        int direction = ai.calculateDirection();
        switch (direction) {
            case 0:
                return ActionConstants.MOVE_UP;
            case 1:
                return ActionConstants.MOVE_RIGHT;
            case 2:
                return ActionConstants.MOVE_DOWN;
            case 3:
                return ActionConstants.MOVE_LEFT;
            default:
                return ActionConstants.DO_NOTHING;
        }
    }

	@Override
	public List<Action> getNextActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getNextAction());
        return actions;
	}
    
}
