package uet.oop.bomberman.agent.state.base;

import uet.oop.bomberman.entities.character.Character;

public abstract class PlayerStateExtractor implements IStateExtractor {

    protected Character player;

    public PlayerStateExtractor(Character player) {
        this.player = player;
    }

}
