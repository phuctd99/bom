package uet.oop.bomberman.agent.ppo;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.agent.state.NaivePlayerStateExtractor;
import uet.oop.bomberman.entities.character.Character;

public class NaivePPOAgent extends PPOAgent {

    public NaivePPOAgent(Character character, Board board) {
        super(character, board, new NaivePlayerStateExtractor(character));
    }
    
}
