package uet.oop.bomberman.agent.state.base;

import uet.oop.bomberman.Board;

public interface IStateExtractor {

    public int getDimension();
    
    public float[] getEmbedding(Board board);

    public float getValue(Board board);

}
