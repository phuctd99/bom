package uet.oop.bomberman.agent;

import java.util.ArrayList;
import java.util.List;

import uet.oop.bomberman.agent.base.Agent;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.character.action.ActionConstants;
import uet.oop.bomberman.entities.character.action.ActionMove;
import uet.oop.bomberman.input.Keyboard;

public class KeyboardAgent extends Agent {

	public KeyboardAgent(Character character) {
		super(character);
	}

	@Override
	public List<Action> getNextActions() {
		List<Action> actions = getMoveActions();

		if (character instanceof Bomber) {
			if (Keyboard.i().space) {
				actions.add(ActionConstants.PLACE_BOMB);
			}
		}
		return actions;
	}

	private List<Action> getMoveActions() {
		int xa = 0, ya = 0;
		if (Keyboard.i().up)
			ya--;
		if (Keyboard.i().down)
			ya++;
		if (Keyboard.i().left)
			xa--;
		if (Keyboard.i().right)
			xa++;

		List<Action> actions = new ArrayList<>();
		if (xa != 0 || ya != 0) {
			ActionMove actionMove = new ActionMove(xa, ya);
			actions.add(actionMove);
		}
		return actions;
	}

}
