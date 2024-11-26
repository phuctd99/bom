package uet.oop.bomberman.agent.rl.model.sac;

import uet.oop.bomberman.agent.rl.model.BaseNetwork;

public abstract class PolicyNetwork extends BaseNetwork implements IPolicyNetwork {
    
    public PolicyNetwork(BaseNetwork.BaseBuilder<?> builder) {
        super(builder);
    }

}
