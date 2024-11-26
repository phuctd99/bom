package uet.oop.bomberman.agent.rl.model;

import ai.djl.Model;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Parameter;
import ai.djl.training.initializer.ConstantInitializer;
import ai.djl.training.initializer.NormalInitializer;

public abstract class BaseNetwork extends BaseModel {

    protected BaseNetwork(BaseBuilder<?> builder) {
        super(builder.manager);
        setInitializer(new NormalInitializer(), Parameter.Type.WEIGHT);
        setInitializer(new ConstantInitializer(1f), Parameter.Type.GAMMA);
        setInitializer(new ConstantInitializer(0f), Parameter.Type.BETA);
    }

    @Override
    protected abstract void initializeChildBlocks(
        NDManager manager,
        DataType dataType,
        Shape... inputShapes
    );

    public abstract Model toModel();

    public static abstract class BaseBuilder<S extends BaseBuilder<?>>{
    
        private NDManager manager;
    
        public S setManager(NDManager manager) {
            this.manager = manager;
            return self();
        }
    
        @SuppressWarnings("unchecked")
        protected final S self() {
            return (S) this;
        }

        public abstract BaseNetwork build();
    
    }

}
