package uet.oop.bomberman.agent.rl.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.training.optimizer.Optimizer;
import ai.djl.training.tracker.Tracker;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import uet.oop.bomberman.agent.rl.model.DistributionValueModel;
import uet.oop.bomberman.agent.rl.utils.ActionSampler;

public abstract class BaseGAE extends BaseAgentImpl {

    private final float gae_lambda;
    private final float gamma;
    protected final int num_of_action;
    protected final int dim_of_state_space;
    private final int hidden_size;

    protected Model model;
    protected Predictor<NDList, NDList> predictor;

    public BaseGAE(int dim_of_state_space, int num_of_action, int hidden_size, float gamma, float gae_lambda,
            float learning_rate) {
        super(
            Optimizer.adam()
                .optLearningRateTracker(Tracker.fixed(learning_rate))
                .build(),
            dim_of_state_space,
            num_of_action
        );
        this.gae_lambda = gae_lambda;
        this.gamma = gamma;
        this.dim_of_state_space = dim_of_state_space;
        this.num_of_action = num_of_action;
        this.hidden_size = hidden_size;
        this.reset();
    }

    @Override
    public void load(String path) {
        try {
            Path dir = Paths.get("models");
            File file = new File("models/PPO.params");
            Files.createDirectories(dir);
            DataInputStream is = new DataInputStream(Files.newInputStream(file.toPath()));
            model.getBlock().loadParameters(manager, is);
        } catch (NoSuchFileException e) {
            System.out.println("No pre-trained model found");
        } catch (MalformedModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void save(String path) {
        try {
            File file = new File("models/PPO.params");

            DataOutputStream os = new DataOutputStream(Files.newOutputStream(file.toPath()));
            model.getBlock().saveParameters(os);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int sampleAction(NDManager submanager, float[] state) {
        try {
            NDArray prob = predictor.predict(new NDList(submanager.create(state))).get(0);
            int action = ActionSampler.sampleMultinomial(prob, random);
            return action;
        } catch (TranslateException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void reset() {
        if (manager != null) {
            manager.close();
        }
        manager = NDManager.newBaseManager();
        model = DistributionValueModel.newModel(manager, dim_of_state_space, hidden_size, num_of_action);
        predictor = model.newPredictor(new NoopTranslator());
    }

    protected NDList estimateAdvantage(NDArray values, NDArray rewards) {
        NDArray expected_returns = rewards.duplicate();
        NDArray advantages = rewards.sub(values.squeeze());
        for (long i = expected_returns.getShape().get(0) - 2; i >= 0; i--) {
            NDIndex index = new NDIndex(i);
            expected_returns.set(index, expected_returns.get(i).add(expected_returns.get(i + 1).mul(gamma)));
            advantages.set(index,
                    advantages.get(i).add(values.get(i + 1).add(advantages.get(i + 1).mul(gae_lambda)).mul(gamma)));
        }

        return new NDList(expected_returns, advantages);
    }

}
