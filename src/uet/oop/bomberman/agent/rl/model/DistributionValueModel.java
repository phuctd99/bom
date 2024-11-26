package uet.oop.bomberman.agent.rl.model;

import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.Parameter;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.training.ParameterStore;
import ai.djl.training.initializer.ConstantInitializer;
import ai.djl.training.initializer.NormalInitializer;
import ai.djl.training.initializer.XavierInitializer;
import ai.djl.util.PairList;

public class DistributionValueModel extends BaseModel {
    private static final float LAYERNORM_MOMENTUM = 0.9999f;
    private static final float LAYERNORM_EPSILON = 1e-5f;

    private final Block linear_input;
    private final Block linear_action;
    private final Block linear_value;

    public NDArray last_linear_input;
    public NDArray last_linear_action;
    public NDArray last_linear_action_norm;
    public NDArray last_linear_action_dist;
    public NDArray last_linear_value;

    private final int hidden_size;
    private final int output_size;
    private final Parameter gamma;
    private final Parameter beta;

    private boolean isFirstUpdate = true;
    private float moving_mean = 0.0f;
    private float moving_var = 1.0f;

    private DistributionValueModel(NDManager manager, int hidden_size, int output_size) {
        super(manager);
        Block input_block = new SequentialBlock()
            .add(Linear.builder().setUnits(hidden_size).build())
            .add(Activation.reluBlock())
            .add(Linear.builder().setUnits(hidden_size).build())
            .add(Activation.reluBlock())
            ;
        Block action_block = new SequentialBlock()
            .add(Linear.builder().setUnits(output_size).build())
            ;
        this.linear_input = addChildBlock("linear_input", input_block);
        this.linear_action = addChildBlock("linear_action", action_block);
        this.linear_value = addChildBlock("linear_value", Linear.builder().setUnits(1).build());

        Parameter pGamma = Parameter.builder()
            .setName("gamma")
            .setType(Parameter.Type.GAMMA)
            .optRequiresGrad(true)
            .optShape(new Shape(1))
            .build();
        this.gamma = addParameter(pGamma);

        Parameter pBeta = Parameter.builder()
            .setName("beta")
            .setType(Parameter.Type.BETA)
            .optRequiresGrad(true)
            .optShape(new Shape(1))
            .build();
        this.beta = addParameter(pBeta);


        this.hidden_size = hidden_size;
        this.output_size = output_size;
    }

    public static Model newModel(NDManager manager, int input_size, int hidden_size, int output_size) {
        Model model = Model.newInstance("DistributionValueModel");
        BaseModel net = new DistributionValueModel(manager, hidden_size, output_size);
        net.initialize(net.getManager(), DataType.FLOAT32, new Shape(input_size));
        model.setBlock(net);

        return model;
    }

    @Override
    protected NDList forwardInternal(ParameterStore parameter_store, NDList inputs, boolean training,
            PairList<String, Object> params) {

        NDList hidden = linear_input.forward(parameter_store, inputs, training);
        last_linear_input = hidden.singletonOrThrow();

        NDArray output_action = linear_action.forward(parameter_store, hidden, training).singletonOrThrow();
        last_linear_action = output_action;
        // output_action = normalize(output_action, training);
        // last_linear_action_norm = output_action;

        NDArray distribution = output_action.softmax(output_action.getShape().dimension() - 1);
        last_linear_action_dist = distribution;

        NDArray value = linear_value.forward(parameter_store, hidden, training).singletonOrThrow();
        last_linear_value = value;

        return new NDList(distribution, value);
    }

    @Override
    public Shape[] getOutputShapes(Shape[] inputShapes) {
        return new Shape[] { new Shape(output_size), new Shape(1) };
    }

    @Override
    public void initializeChildBlocks(NDManager manager, DataType data_type, Shape... input_shapes) {
        setInitializer(new NormalInitializer(), Parameter.Type.WEIGHT);
        setInitializer(new ConstantInitializer(1f), Parameter.Type.GAMMA);
        setInitializer(new ConstantInitializer(0f), Parameter.Type.BETA);
        
        linear_input.initialize(manager, data_type, input_shapes);
        linear_action.initialize(manager, data_type, new Shape(hidden_size));
        linear_value.initialize(manager, data_type, new Shape(hidden_size));
        gamma.initialize(manager, data_type);
        beta.initialize(manager, data_type);
    }

    private NDArray normalize(NDArray arr, boolean training) {
        int last_dimension = arr.getShape().dimension() - 1;
        NDArray score_mean = arr.mean(new int[]{last_dimension}).expandDims(last_dimension);
        NDArray score_var = arr.sub(score_mean).pow(2)
            .mean(new int[]{last_dimension})
            .expandDims(last_dimension);
        // float score_mean = arr.mean().getFloat();
        // float score_var = arr.sub(score_mean).pow(2).mean().getFloat();
        // if (isFirstUpdate) {
        //     moving_mean = score_mean;
        //     moving_var = score_var;
        //     isFirstUpdate = false;
        // } else {
        //     moving_mean = moving_mean * LAYERNORM_MOMENTUM + score_mean * (1.0f - LAYERNORM_MOMENTUM);
        //     moving_var = moving_var * LAYERNORM_MOMENTUM + score_var * (1.0f - LAYERNORM_MOMENTUM);
        // }
        return arr
            .sub(score_mean)
            .div(score_var.add(LAYERNORM_EPSILON).sqrt())
            .mul(gamma.getArray())
            .add(beta.getArray());
    }

}
