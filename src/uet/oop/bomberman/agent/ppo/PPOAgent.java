package uet.oop.bomberman.agent.ppo;

import java.util.ArrayList;
import java.util.List;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.agent.base.Agent;
import uet.oop.bomberman.agent.base.RewardBasedAgent;
import uet.oop.bomberman.agent.base.SerializableAgent;
import uet.oop.bomberman.agent.rl.PPO;
import uet.oop.bomberman.agent.state.base.IStateExtractor;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.action.Action;

public class PPOAgent extends Agent implements SerializableAgent, RewardBasedAgent {

    private IStateExtractor stateExtractor;
    private Board board;
    private PPO ppo;
    private List<Action> validActions;

    private float prevValue;
    private float bonusReward = 0;

    public PPOAgent(Character character, Board board, IStateExtractor stateExtractor) {
        super(character);
        this.board = board;
        this.prevValue = stateExtractor.getValue(board);
        this.stateExtractor = stateExtractor;
        this.validActions = character.getValidActions();
        this.ppo = new PPO(
            stateExtractor.getDimension(),
            validActions.size(),
            64,
            0.97f,
            0.95f,
            3e-4f,
            8,
            64,
            0.05f
        );
        this.ppo.setActionMaskGetter(() -> {
            List<Action> performableActions = character.getPerformableActions();
            Boolean[] mask = new Boolean[validActions.size()];
            for (int i = 0; i < validActions.size(); i++) {
                mask[i] = performableActions.contains(validActions.get(i));
            }
            return mask;
        });
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
            ppo.collect(currentValue - prevValue + bonusReward, false);
            bonusReward = 0;
        } catch (IllegalStateException ignored) {}
    }

    private Action getAction() {
        float[] state = stateExtractor.getEmbedding(board);
        int actionIndex = ppo.react(state);
        Action action = validActions.get(actionIndex);
        return action;
    }

    @Override
    public void load(String path) {
        ppo.load(path);
    }

    @Override
    public void save(String path) {
        ppo.save(path);
    }

    @Override
    public void handleWinLevel() {
        ppo.collect(100, true);
        save(null);
    }

    @Override
    public void handleLoseLevel() {
        ppo.collect(-100, true);
        save(null);
    }

    @Override
    public void addReward(float reward) {
        bonusReward += reward;
    }

}
