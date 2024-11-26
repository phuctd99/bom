package uet.oop.bomberman.agent.rl.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import ai.djl.ndarray.NDManager;
import ai.djl.training.optimizer.Optimizer;
import ai.djl.translate.TranslateException;
import uet.oop.bomberman.agent.rl.dtypes.Memory;

public abstract class BaseAgentImpl extends BaseAgent {
    
    private static final float EXPLORE_RATE = 0.05f;
    protected final Random random = new Random();

    protected Memory memory = new Memory(65535);
    protected Optimizer optimizer;
    protected int stateSize;
    protected int actionSize;

    protected NDManager manager;

    protected Supplier<Boolean[]> getActionMask;

    protected AgentMetadata metadata = new AgentMetadata();

    public static class AgentMetadata implements Serializable {
        public int episode = 0;
    }

    protected BaseAgentImpl(Optimizer optimizer, int stateSize, int actionSize) {
        this(
            builder()
            .setOptimizer(optimizer)
            .setStateSize(stateSize)
            .setActionSize(actionSize)
        );
    }

    protected BaseAgentImpl(BaseBuilder<?> builder) {
        this.manager = NDManager.newBaseManager();
        this.optimizer = builder.optimizer;
        this.stateSize = builder.stateSize;
        this.actionSize = builder.actionSize;
    }

    public void setActionMaskGetter(Supplier<Boolean[]> getActionMask) {
        this.getActionMask = getActionMask;
    }

    @Override
    public int react(float[] state) {
        int action = sampleAction(state);
        return react(state, action);
    }

    @Override
    public int react(float[] state, int action) {
        if (!isEval()) {
            memory.setState(state);
        }
        if (!isEval()) {
            memory.setAction(action);
        }

        return action;

    }

    private int sampleAction(float[] state) {
        int action;
        if (random.nextFloat() < EXPLORE_RATE) {
            action = randomAction();
        } else {
            try (NDManager submanager = manager.newSubManager()) {
                action = sampleAction(submanager, state);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                action = randomAction();
            }
            System.out.println(" action: " + action);
        }
        return action;
    }

    private int randomAction() {
        int action;
        Boolean[] actionMask = getActionMask.get();
        List<Integer> validActions = new ArrayList<>();
        for (int i = 0; i < actionSize; i++) {
            if (actionMask[i]) {
                validActions.add(i);
            }
        }
        action = validActions.get(random.nextInt(validActions.size()));
        return action;
    }

    public abstract int sampleAction(NDManager submanager, float[] state);
    public abstract void load(String path);
    public abstract void save(String path);

    @Override
    public void collect(float reward, boolean done) {
        if (!isEval()) {
            memory.setRewardAndMask(reward, done);
            if (done) {
                try {
                    updateModel(manager);
                } catch (TranslateException e) {
                    throw new IllegalStateException(e);
                }
                metadata.episode += 1;
                memory.reset();
            }
        }
    }

    private static BaseBuilder<?> builder() {
        return new BaseBuilder<>();
    }

    public abstract void updateModel(NDManager submanager) throws TranslateException;

    public static class BaseBuilder<T extends BaseBuilder<?>> {

        private Optimizer optimizer;
        private int stateSize;
        private int actionSize;

        public T setOptimizer(Optimizer optimizer) {
            this.optimizer = optimizer;
            return self();
        }

        public T setStateSize(int stateSize) {
            this.stateSize = stateSize;
            return self();
        }

        public T setActionSize(int actionSize) {
            this.actionSize = actionSize;
            return self();
        }

        @SuppressWarnings("unchecked")
        protected final T self() {
            return (T) this;
        }
    }

}
