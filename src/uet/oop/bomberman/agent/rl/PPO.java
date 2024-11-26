package uet.oop.bomberman.agent.rl;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.GradientCollector;
import ai.djl.training.Trainer;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.TranslateException;
import uet.oop.bomberman.agent.rl.base.BaseGAE;
import uet.oop.bomberman.agent.rl.dtypes.MemoryBatch;
import uet.oop.bomberman.agent.rl.utils.ModelHelper;

public class PPO extends BaseGAE {
    private final int inner_updates;
    private final int inner_batch_size;
    private final float ratio_lower_bound;
    private final float ratio_upper_bound;

    private NDArray last_gradient_01 = null;

    public PPO(int dim_of_state_space, int num_of_action, int hidden_size, float gamma, float gae_lambda,
            float learning_rate, int inner_updates, int inner_batch_size, float ratio_clip) {
        super(dim_of_state_space, num_of_action, hidden_size, gamma, gae_lambda, learning_rate);
        this.inner_updates = inner_updates;
        this.inner_batch_size = inner_batch_size;
        this.ratio_lower_bound = 1.0f - ratio_clip;
        this.ratio_upper_bound = 1.0f + ratio_clip;
    }

    @Override
    public void updateModel(NDManager submanager) throws TranslateException {
        MemoryBatch batch = memory.getOrderedBatch(submanager);
        NDArray states = batch.getStates();
        NDArray actions = batch.getActions();

        try (Trainer trainer = model.newTrainer(getTrainingConfig())) {

            trainer.initialize(new Shape(1, dim_of_state_space));
            trainer.notifyListeners(listener -> listener.onTrainingBegin(trainer));

            NDList net_output = trainer.evaluate(new NDList(states));
    
            NDArray distribution = ModelHelper.gather(
                net_output.get(0).duplicate(),
                actions.toIntArray()
            );
            NDArray values = net_output.get(1).duplicate();
    
            NDArray rewards = batch.getRewards();
            NDList estimates = estimateAdvantage(values.duplicate(), rewards);
            NDArray expected_returns = estimates.get(0);
            NDArray advantages = estimates.get(1);
    
            float rewardsValue = rewards.sum().getFloat();
            float lossValue = 0.0f;
    
            long iters = inner_updates * (1 + actions.size() / inner_batch_size);
            for (int i = 0; i < iters; i++) {

                NDArray index = manager.randomInteger(0, actions.size(), new Shape(inner_batch_size), DataType.INT32);
                
                NDArray states_subset = states.get(index);
                NDArray actions_subset = actions.get(index);
                NDArray distribution_subset = distribution.get(index);
                NDArray expected_returns_subset = expected_returns.get(index);
                NDArray advantages_subset = advantages.get(index);

                try (GradientCollector collector = trainer.newGradientCollector()) {
        
                    NDList net_output_updated = trainer.forward(new NDList(states_subset));
                    NDArray distribution_updated = ModelHelper.gather(
                        net_output_updated.get(0),
                        actions_subset.toIntArray()
                    );
                    NDArray values_updated = net_output_updated.get(1);
        
                    NDArray loss_critic = (expected_returns_subset.sub(values_updated.squeeze())).square().mean();
        
                    NDArray ratios = distribution_updated.sub(distribution_subset).exp();
                    NDArray td_objective = ratios.mul(advantages_subset);
                    NDArray clipped_td_objective = ratios.clip(ratio_lower_bound, ratio_upper_bound).mul(advantages_subset);
                    NDArray loss_actor = td_objective.minimum(clipped_td_objective).mean();

                    NDArray distribution_entropy = net_output_updated.get(0).mul(
                        net_output_updated.get(0).add(1e-2).log()
                    ).sum().neg();

                    NDArray loss = loss_critic;
                    loss = loss.sub(loss_actor);
                    loss = loss.sub(distribution_entropy.mul(0.05));

                    collector.backward(loss);

                    float _loss = 0.0f;
                    try {
                        _loss = (float) loss.getDouble();
                    } catch (IllegalStateException ex) {
                        ex.printStackTrace();
                        _loss = loss.getFloat();
                    }
                    lossValue += _loss;

                    NDArray gradient = model.getBlock()
                        .getChildren().get(1).getValue()
                        .getParameters().get(0).getValue()
                        .getArray()
                        .getGradient()
                        .duplicate();
                    if (gradient.isNaN().any().getBoolean() || gradient.isInfinite().any().getBoolean()) {
                        throw new IllegalStateException();
                    }
                    last_gradient_01 = gradient;

                    trainer.step();
                }
            }

            trainer.notifyListeners(listener -> listener.onEpoch(trainer));
            trainer.notifyListeners(listener -> listener.onTrainingEnd(trainer));

            System.out.println("Iters: " + iters);
            System.out.println("Avg loss: " + lossValue / iters);
            System.out.println("Total rewards: " + rewardsValue);

            System.out.println();

        }
    }

    private DefaultTrainingConfig getTrainingConfig() {
        DefaultTrainingConfig trainingConfig = new DefaultTrainingConfig(Loss.l2Loss())
            .addTrainingListeners(TrainingListener.Defaults.basic())
            .optOptimizer(optimizer);
        return trainingConfig;
    }

    private NDArray getSample(NDManager submanager, NDArray array, int[] index) {

        Shape shape = Shape.update(array.getShape(), 0, inner_batch_size);
        NDArray sample = submanager.zeros(shape, array.getDataType());
        for (int i = 0; i < index.length; i++) {
            sample.set(new NDIndex(i), array.get(index[i]));
        }
        return sample;
    }

}
