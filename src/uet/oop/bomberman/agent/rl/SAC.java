package uet.oop.bomberman.agent.rl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
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
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import uet.oop.bomberman.agent.rl.base.BaseAgentImpl;
import uet.oop.bomberman.agent.rl.base.BaseGAE;
import uet.oop.bomberman.agent.rl.dtypes.MemoryBatch;
import uet.oop.bomberman.agent.rl.model.sac.DeterministicPolicyNetwork;
import uet.oop.bomberman.agent.rl.model.sac.PolicyNetwork;
import uet.oop.bomberman.agent.rl.model.sac.QNetwork;

public class SAC extends BaseAgentImpl {

    private static final String FILE_NAME_Q1 = "q1";
    private static final String FILE_NAME_Q2 = "q2";
    private static final String FILE_NAME_POLICY = "policy";
    private static final String FILE_NAME_METADATA = "metadata.json";
    private static final String FILE_NAME_LOGS = "logs.csv";
    private static final float EPSILON = 1e-6f;

    private final int batchSize;
    private final int trainingIters;
    private final int policyUpdateInterval;
    private final float gamma;
    private final float alpha;

    private QNetwork q1Network;
    private QNetwork q2Network;
    private PolicyNetwork policyNetwork;

    private Model q1Model;
    private Model q2Model;
    private Model policyModel;
    private Predictor<NDList, NDList> policyPredictor;
    private int policyUpdateCounter = 0;

    private List<AgentLog> logs = new ArrayList<>();

    public static class AgentLog {
        public int episode;
        public int steps;
        public float totalReward;

        public float avgLoss;
        public float avgQ1Loss;
        public float avgQ2Loss;
        public float avgPolicyLoss;
    }
    
    public SAC(Builder builder) {
        super(builder);
        this.batchSize = builder.batchSize;
        this.trainingIters = builder.trainingIters;
        this.policyUpdateInterval = builder.policyUpdateInterval;
        this.policyUpdateCounter = policyUpdateInterval;
        this.gamma = builder.gamma;
        this.alpha = builder.alpha;
        this.reset();
    }

    @Override
    public int sampleAction(NDManager submanager, float[] state) {
        try {
            NDList output = policyPredictor.predict(new NDList(submanager.create(state)));
            int action = policyNetwork.sampleAction(submanager, output)
                .toType(DataType.INT32, true)
                .getInt();
            return action;
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    protected NDArray calculateExpectedReturns(NDArray rewards) {
        NDArray expectedReturns = rewards.duplicate();
        for (long i = expectedReturns.getShape().get(0) - 2; i >= 0; i--) {
            NDIndex index = new NDIndex(i);
            expectedReturns.set(index, expectedReturns.get(i).add(expectedReturns.get(i + 1).mul(gamma)));
        }

        return expectedReturns;
    }

    @Override
    public void updateModel(NDManager submanager) throws TranslateException {
        MemoryBatch memoryBatch = memory.getOrderedBatch(submanager);
        NDArray states = memoryBatch.getStates();
        NDArray actions = memoryBatch.getActions();
        NDArray nextStates = memoryBatch.getNextStates();
        NDArray rewards = memoryBatch.getRewards();
        NDArray masks = memoryBatch.getMasks();
        NDArray expectedReturns = calculateExpectedReturns(rewards);

        final int[] lastDimension = new int[] { states.getShape().dimension() - 1 };

        try (
            Trainer q1Trainer = q1Model.newTrainer(getTrainingConfig());
            Trainer q2Trainer = q2Model.newTrainer(getTrainingConfig());
            Trainer policyTrainer = policyModel.newTrainer(getTrainingConfig())
        ) {
            q1Trainer.notifyListeners(listener -> listener.onTrainingBegin(q1Trainer));
            q2Trainer.notifyListeners(listener -> listener.onTrainingBegin(q2Trainer));
            policyTrainer.notifyListeners(listener -> listener.onTrainingBegin(policyTrainer));

            float totalLossQ1 = 0.0f;
            float totalLossQ2 = 0.0f;
            float totalLossPolicy = 0.0f;
    
            for (int iter = 0; iter < trainingIters; iter++) {

                NDArray randomIndex = submanager.randomInteger(
                    0,
                    actions.size(),
                    new Shape(batchSize),
                    DataType.INT32
                );

                NDArray batchStates = states.get(randomIndex);
                NDArray batchActions = actions.get(randomIndex);
                NDArray batchNextStates = nextStates.get(randomIndex);  
                NDArray batchRewards = rewards.get(randomIndex);
                NDArray batchExpectedReturns = expectedReturns.get(randomIndex);
                NDArray batchMasks = masks.get(randomIndex);
                
                // After each line, the shape of the NDArray is shown in the comment
                // where N is the batch size, e.g. (N, 1), (N, actionSize)

                NDArray indexAction = batchActions.expandDims(1); // (N, 1)

                NDArray nextPi = policyTrainer.evaluate(new NDList(batchNextStates)).singletonOrThrow(); // (N, actionSize)
                NDArray nextQ1 = q1Trainer.evaluate(new NDList(batchNextStates, nextPi)).singletonOrThrow(); // (N)
                NDArray nextQ2 = q2Trainer.evaluate(new NDList(batchNextStates, nextPi)).singletonOrThrow(); // (N)
                NDArray nextPiSingle = nextPi.max(lastDimension); // (N)
                NDArray nextEntropy = nextPiSingle.maximum(EPSILON).log().neg(); // (N)
                NDArray nextMinQ = nextQ1.minimum(nextQ2).add(nextEntropy.mul(alpha)); // (N)
                NDArray maskNotDone = batchMasks.sub(1).neg();
                NDArray estimatedQ = batchRewards.add(nextMinQ.mul(gamma).mul(maskNotDone)); // (N)
    
                NDArray actualPiZeros = submanager.zeros(new Shape(batchSize, actionSize)); // (N, actionSize)
                NDArray actualPiOnes = submanager.ones(new Shape(batchSize, actionSize)); // (N, actionSize)
                NDArray actualPi = actualPiZeros.scatter(indexAction, actualPiOnes, 1); // (N, actionSize)

                try (
                    GradientCollector collector = policyTrainer.newGradientCollector();
                ) {
                    NDArray q1 = q1Trainer.forward(new NDList(batchStates, actualPi)).singletonOrThrow();
                    NDArray q2 = q2Trainer.forward(new NDList(batchStates, actualPi)).singletonOrThrow();

                    NDArray q1Loss = q1.sub(estimatedQ).square().mean();
                    NDArray q2Loss = q2.sub(estimatedQ).square().mean();

                    totalLossQ1 += q1Loss.getFloat();
                    totalLossQ2 += q2Loss.getFloat();
                 
                    NDArray pi = policyTrainer.forward(new NDList(batchStates)).singletonOrThrow(); // (N, actionSize)
                    NDArray piSingle = pi.max(lastDimension); // (N)
                    NDArray entropy = piSingle.maximum(EPSILON).log().neg(); // (N)
                    NDArray minQ = q1.minimum(q2).add(entropy.mul(alpha)); // (N)
                    NDArray policyLoss = minQ.neg().mean();

                    totalLossPolicy += policyLoss.getFloat();

                    NDArray gradient = q1Model.getBlock()
                        .getParameters()
                        .get(2).getValue()
                        .getArray()
                        .getGradient()
                        .duplicate();
                    if (gradient.isNaN().any().getBoolean() || gradient.isInfinite().any().getBoolean()) {
                            throw new IllegalStateException();
                    }

                    NDArray loss = q1Loss.add(q2Loss);
                    
                    collector.backward(loss);
                    q1Trainer.step();
                    q2Trainer.step();

                    policyUpdateCounter -= 1;
                    if (policyUpdateCounter <= 0) {
                        policyUpdateCounter = policyUpdateInterval;
                        collector.zeroGradients();
                        collector.backward(policyLoss);
                        policyTrainer.step();
                    }
                }
            }

            q1Trainer.notifyListeners(listener -> listener.onEpoch(q1Trainer));
            q2Trainer.notifyListeners(listener -> listener.onEpoch(q2Trainer));
            policyTrainer.notifyListeners(listener -> listener.onEpoch(policyTrainer));

            q1Trainer.notifyListeners(listener -> listener.onTrainingEnd(q1Trainer));
            q2Trainer.notifyListeners(listener -> listener.onTrainingEnd(q2Trainer));
            policyTrainer.notifyListeners(listener -> listener.onTrainingEnd(policyTrainer));

            float totalLoss = totalLossQ1 + totalLossQ2 + totalLossPolicy;

            System.out.println("Iters: " + trainingIters);
            System.out.println("Avg loss: " + totalLoss / trainingIters);
            float totalRewards = rewards.sum().getFloat();
            System.out.println("Total rewards: " + totalRewards);

            AgentLog log = new AgentLog();
            log.episode = metadata.episode;
            log.steps = (int) states.getShape().get(0);
            log.avgLoss = totalLoss / trainingIters;
            log.avgQ1Loss = totalLossQ1 / trainingIters;
            log.avgQ2Loss = totalLossQ2 / trainingIters;
            log.avgPolicyLoss = totalLossPolicy / trainingIters;
            log.totalReward = totalRewards;
            logs.add(log);

            System.out.println();

        }
    }

    @Override
    public void load(String path) {
        try {
            Path dir = Paths.get(path);
            if (!Files.exists(dir)) {
                System.out.println("No pre-trained model found");
                return;
            }
            q1Model = loadParams(path, FILE_NAME_Q1, q1Model);
            q2Model = loadParams(path, FILE_NAME_Q2, q2Model);
            policyModel = loadParams(path, FILE_NAME_POLICY, policyModel);

            Path pathMetadata = Paths.get(path, FILE_NAME_METADATA);
            if (Files.exists(pathMetadata)) {
                metadata = new ObjectMapper().readValue(pathMetadata.toFile(), AgentMetadata.class);
            } else {
                System.out.println("No metadata found");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Model loadParams(String dir, String fileName, Model model) throws IOException, MalformedModelException {
        Path path = Paths.get(dir, fileName);
        model.load(path, "model");
        // try (DataInputStream is = new DataInputStream(Files.newInputStream(path))) {
        //     model.getBlock().loadParameters(manager, is);
        // }
        return model;
    }

    @Override
    public void save(String path) {
        try {
            Path dir = Paths.get(path);
            Files.createDirectories(dir);
            saveParams(path, FILE_NAME_Q1, q1Model);
            saveParams(path, FILE_NAME_Q2, q2Model);
            saveParams(path, FILE_NAME_POLICY, policyModel);

            Path pathMetadata = Paths.get(path, FILE_NAME_METADATA);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathMetadata.toFile()))) {
                new ObjectMapper().writeValue(bw, metadata);
            }

            Path pathCsv = Paths.get(path, FILE_NAME_LOGS);
            if (!Files.exists(pathCsv)) {
                pathCsv.toFile().createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathCsv.toFile(), false))) {
                    String header = Arrays.stream(AgentLog.class.getFields())
                        .filter(field -> Modifier.isPublic(field.getModifiers()))
                        .map(Field::getName)
                        .collect(Collectors.joining(","));
                    bw.write(header);
                    bw.newLine();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathCsv.toFile(), true))) {
                for (AgentLog log : logs) {
                    String line = Arrays.stream(AgentLog.class.getFields())
                        .filter(field -> Modifier.isPublic(field.getModifiers()))
                        .map(field -> {
                            try {
                                return field.get(log).toString();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                return "";
                            }
                        })
                        .collect(Collectors.joining(","));
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveParams(String dir, String fileName, Model model) throws IOException {
        Path path = Paths.get(dir, fileName);

        Files.createDirectories(path);
        model.save(path, "model");
    }

    @Override
    public void reset() {
        if (manager != null) {
            manager.close();
        }
        manager = NDManager.newBaseManager();

        q1Network = QNetwork.builder()
            .setManager(manager)
            .setStateSize(stateSize)
            .setActionSize(actionSize)
            .build();
        q1Network.initialize(manager, DataType.FLOAT32, new Shape(stateSize), new Shape(actionSize));
        q1Model = q1Network.toModel();

        q2Network = QNetwork.builder()
            .setManager(manager)
            .setStateSize(stateSize)
            .setActionSize(actionSize)
            .build();
        q2Network.initialize(manager, DataType.FLOAT32, new Shape(stateSize), new Shape(actionSize));
        q2Model = q2Network.toModel();

        policyNetwork = DeterministicPolicyNetwork.builder()
            .setManager(manager)
            .setStateSize(stateSize)
            .setActionSize(actionSize)
            .build();
        policyNetwork.initialize(manager, DataType.FLOAT32, new Shape(stateSize));
        policyModel = policyNetwork.toModel();
        policyPredictor = policyModel.newPredictor(new NoopTranslator());
    }

    private DefaultTrainingConfig getTrainingConfig() {
        DefaultTrainingConfig trainingConfig = new DefaultTrainingConfig(Loss.l2Loss())
            .addTrainingListeners(TrainingListener.Defaults.basic())
            .optOptimizer(optimizer);
        return trainingConfig;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseGAE.BaseBuilder<Builder> {

        private int batchSize = 128;
        private int trainingIters = 10;
        private int policyUpdateInterval = 1;
        private float gamma = 0.99f;
        private float alpha = 0.2f;

        public Builder optBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return self();
        }

        public Builder optTrainingIters(int trainingIters) {
            this.trainingIters = trainingIters;
            return self();
        }

        public Builder optPolicyUpdateInterval(int policyUpdateInterval) {
            this.policyUpdateInterval = policyUpdateInterval;
            return self();
        }

        public Builder optGamma(float gamma) {
            this.gamma = gamma;
            return self();
        }

        public Builder optAlpha(float alpha) {
            this.alpha = alpha;
            return self();
        }

        public SAC build() {
            return new SAC(this);
        }

    }

}
