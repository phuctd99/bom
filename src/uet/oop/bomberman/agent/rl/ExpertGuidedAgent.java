package uet.oop.bomberman.agent.rl;

import java.util.List;
import java.util.Random;

import ai.djl.ndarray.NDManager;
import ai.djl.translate.TranslateException;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.agent.base.IAgent;
import uet.oop.bomberman.agent.rl.base.BaseAgentImpl;
import uet.oop.bomberman.entities.character.action.Action;

public class ExpertGuidedAgent extends BaseAgentImpl {

    private static final float EXPERT_RATE = 0.5f;
    private static final int SWITCHING_DURATION = 10 * Game.TICKS_PER_SECOND;
    

    private BaseAgentImpl originalAgent;
    private IAgent expertAgent;
    private List<Action> validActions;
    private final Random RANDOM = new Random();

    private boolean isExpertGuided = false;
    private int switchingDuration = 0;

    protected ExpertGuidedAgent(Builder builder) {
        super(builder);
        this.originalAgent = builder.originalAgent;
        this.expertAgent = builder.expertAgent;
        this.validActions = builder.validActions;
    }

    @Override
    public int react(float[] state) {
        switchingDuration -= 1;
        if (switchingDuration <= 0) {
            float chance = RANDOM.nextFloat();
            if (chance < EXPERT_RATE) {
                isExpertGuided = true;
            } else {
                isExpertGuided = false;
            }
            switchingDuration = SWITCHING_DURATION;
        }
        if (isExpertGuided) {
            Action action = expertAgent.getNextActions().get(0);
            int actionId = validActions.indexOf(action);
            return react(state, actionId);
        } else {
            return originalAgent.react(state);
        }
    }

    @Override
    public int react(float[] state, int action) {
        return originalAgent.react(state, action);
    }

    @Override
    public void collect(float reward, boolean done) {
        originalAgent.collect(reward, done);
    }

    @Override
    public void reset() {
        originalAgent.reset();
    }

    @Override
    public int sampleAction(NDManager submanager, float[] state) {
        return originalAgent.sampleAction(submanager, state);
    }

    @Override
    public void load(String path) {
        originalAgent.load(path);
    }

    @Override
    public void save(String path) {
        originalAgent.save(path);
    }

    @Override
    public void updateModel(NDManager submanager) throws TranslateException {
        originalAgent.updateModel(submanager);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseBuilder<Builder> {

        private BaseAgentImpl originalAgent;
        private IAgent expertAgent;
        private List<Action> validActions;

        public Builder setOriginalAgent(BaseAgentImpl originalAgent) {
            this.originalAgent = originalAgent;
            return this;
        }

        public Builder setExpertAgent(IAgent expertAgent) {
            this.expertAgent = expertAgent;
            return this;
        }

        public Builder setValidActions(List<Action> validActions) {
            this.validActions = validActions;
            return this;
        }

        public ExpertGuidedAgent build() {
            return new ExpertGuidedAgent(this);
        }

    }
    
}
