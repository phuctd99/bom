package uet.oop.bomberman.agent.rl.model.sac;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;

public interface IPolicyNetwork {
    
    public NDArray sampleAction(NDManager manager, NDList modelOutput);

}
