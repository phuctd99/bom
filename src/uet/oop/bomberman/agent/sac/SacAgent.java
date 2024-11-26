package uet.oop.bomberman.agent.sac;

import java.util.ArrayList;
import java.util.List;

import ai.djl.training.optimizer.Optimizer;
import ai.djl.training.tracker.Tracker;
import uet.oop.bomberman.Board;
import uet.oop.bomberman.agent.base.Agent;
import uet.oop.bomberman.agent.base.RewardBasedAgent;
import uet.oop.bomberman.agent.base.SerializableAgent;
import uet.oop.bomberman.agent.rl.ExpertGuidedAgent;
import uet.oop.bomberman.agent.rl.SAC;
import uet.oop.bomberman.agent.rl.base.BaseAgentImpl;
import uet.oop.bomberman.agent.rulebased.RuleBasedBomberAgent;
import uet.oop.bomberman.agent.state.base.IStateExtractor;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.action.Action;

public class SacAgent extends Agent implements SerializableAgent, RewardBasedAgent {

    private IStateExtractor stateExtractor;
    private Board board;
    private BaseAgentImpl agent;
    private List<Action> validActions;

    private float prevValue;
    private float bonusReward = 0;

    private String dir = "models";

    public SacAgent(Character character, Board board, IStateExtractor stateExtractor) {
        super(character);
        this.board = board;
        this.prevValue = stateExtractor.getValue(board);
        this.stateExtractor = stateExtractor;
        this.validActions = character.getValidActions();
        final Optimizer optimizer = Optimizer.adam()
            .optLearningRateTracker(Tracker.fixed(3e-4f))
            .build();
        this.agent = SAC.builder()
            .setStateSize(stateExtractor.getDimension())
            .setActionSize(validActions.size())
            .setOptimizer(optimizer)
            .optPolicyUpdateInterval(32)
            .build();
        this.agent.setActionMaskGetter(() -> {
            List<Action> performableActions = character.getPerformableActions();
            Boolean[] mask = new Boolean[validActions.size()];
            for (int i = 0; i < validActions.size(); i++) {
                mask[i] = performableActions.contains(validActions.get(i));
            }
            return mask;
        });
        this.agent = ExpertGuidedAgent.builder()
            .setOriginalAgent(this.agent)
            .setExpertAgent(new RuleBasedBomberAgent(character, board))
            .setValidActions(validActions)
            .build();
    }

    public String getModelPath() {
        return dir;
    }

    public void setModelPath(String dir) {
        this.dir = dir;
    }

    @Override
    public List<Action> getNextActions() {
        collectReward();
        Action action = getAction();
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        prevValue = stateExtractor.getValue(board);
        return actions;
    }

    private void collectReward() {
        float currentValue = stateExtractor.getValue(board);
        try {
            agent.collect(currentValue - prevValue + bonusReward, false);
            bonusReward = 0;
        } catch (IllegalStateException ignored) {}
    }

    private Action getAction() {
        float[] state = stateExtractor.getEmbedding(board);
        int actionIndex = agent.react(state);
        Action action = validActions.get(actionIndex);
        return action;
    }

    @Override
    public void load(String path) {
        agent.load(path);
    }

    @Override
    public void save(String path) {
        agent.save(path);
    }

    @Override
    public void handleWinLevel() {
        agent.collect(100, true);
        save(dir);
    }

    @Override
    public void handleLoseLevel() {
        agent.collect(-100, true);
        save(dir);
    }

    @Override
    public void addReward(float reward) {
        bonusReward += reward;
    }

}
