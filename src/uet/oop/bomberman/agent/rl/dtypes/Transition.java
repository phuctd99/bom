package uet.oop.bomberman.agent.rl.dtypes;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Transition extends Snapshot {

    private final float[] state_next;
    private final int action;

    public Transition(float[] state, float[] state_next, int action, float reward, boolean mask) {
        super(state, reward, mask);
        this.state_next = state_next != null ? state_next.clone() : null;
        this.action = action;
    }

    public final float[] getNextState() {
        return state_next;
    }

    public final int getAction() {
        return action;
    }

    @Override
    public String toString() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("state", getState());
            map.put("state_next", state_next);
            map.put("action", action);
            map.put("reward", getReward());
            map.put("mask", isMasked());
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
