package uet.oop.bomberman.agent.base;

import java.util.List;

import uet.oop.bomberman.entities.character.action.Action;

public interface IAgent {

    public List<Action> getNextActions();

}
