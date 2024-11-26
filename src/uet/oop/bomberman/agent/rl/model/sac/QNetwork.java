package uet.oop.bomberman.agent.rl.model.sac;

import java.util.Arrays;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.training.ParameterStore;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.util.PairList;
import uet.oop.bomberman.agent.rl.model.BaseNetwork;

public class QNetwork extends BaseNetwork {

    private Block block;

    protected QNetwork(Builder builder) {
        super(builder);
        SequentialBlock block = new SequentialBlock();
        for (int i = 0; i < builder.numHiddenLayer; i++) {
            block.add(Linear.builder().setUnits(builder.hiddenSize).build());
            block.add(Activation.reluBlock());
        }
        block.add(Linear.builder().setUnits(1).build());
        this.block = addChildBlock("QNetwork", block);
    }

    @Override
    public Shape[] getOutputShapes(Shape[] inputShapes) {
        final Shape[] OUTPUT_SHAPE_SINGLE = new Shape[] {new Shape()};
        long batchSize = -1;
        for (Shape shape : inputShapes) {
            if (shape.dimension() <= 1) {
                return OUTPUT_SHAPE_SINGLE;
            }
            long _batch_size = shape.get(0);
            if (batchSize == -1) {
                batchSize = _batch_size;
            } else if (batchSize != _batch_size) {
                throw new IllegalArgumentException("Inconsistent batch size");
            }
        }
        return new Shape[] {new Shape(batchSize)};
    }

    @Override
    protected NDList forwardInternal(ParameterStore parameterStore, NDList inputs, boolean training,
            PairList<String, Object> params) {
        int lastDimension = inputs.get(0).getShape().dimension() - 1;
        NDArray input = NDArrays.concat(inputs, lastDimension);
        NDArray output = block.forward(parameterStore, new NDList(input), training, params).singletonOrThrow();
        return new NDList(output.squeeze(-1));
    }

    @Override
    protected void initializeChildBlocks(NDManager manager, DataType dataType, Shape... inputShapes) {
        int dimensions = inputShapes[0].dimension();
        int lastDimensionSize = 0;
        for (Shape shape: inputShapes) {
            lastDimensionSize += shape.get(dimensions - 1);
        }
        block.initialize(manager, dataType, new Shape(lastDimensionSize));
    }

    public Model toModel() {
        Model model = Model.newInstance("QNetwork");
        model.setBlock(this);
        return model;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseBuilder<Builder> {

        private int stateSize;
        private int actionSize;
        private int hiddenSize = 64;
        private int numHiddenLayer = 1;

        private Builder() {
        }

        public Builder setStateSize(int inputSize) {
            this.stateSize = inputSize;
            return self();
        }

        public Builder setActionSize(int actionSize) {
            this.actionSize = actionSize;
            return self();
        }

        public Builder optHiddenSize(int hiddenSize) {
            this.hiddenSize = hiddenSize;
            return self();
        }

        public Builder optNumLayer(int numLayer) {
            this.numHiddenLayer = numLayer;
            return self();
        }

        @Override
        public QNetwork build() {
            return new QNetwork(this);
        }

    }

    public static void main(String[] args) {
        NDManager manager = NDManager.newBaseManager();
        QNetwork network = QNetwork.builder()
            .setManager(manager)
            .setStateSize(4)
            .setActionSize(2)
            .optHiddenSize(64)
            .optNumLayer(2)
            .build();
        Model model = network.toModel();
        Predictor<NDList, NDList> predictor = model.newPredictor(new NoopTranslator());
        long batchSize = 8;
        NDArray state = manager.ones(new Shape(batchSize, 4));
        NDArray action = manager.ones(new Shape(batchSize, 2));
        NDList result = null;
        try {
            result = predictor.predict(new NDList(state, action));
        } catch (TranslateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        NDArray resultArray = result.singletonOrThrow();
        if (!resultArray.getShape().equals(new Shape(batchSize, 1))) {
            throw new IllegalStateException("Invalid output shape");
        }
    }
    
}
