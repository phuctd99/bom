package uet.oop.bomberman.agent.rl.dtypes;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Snapshot {
    private final float[] state;
    private final float reward;
    private final boolean mask;

    public Snapshot(float[] state, float reward, boolean mask) {
        this.state = state.clone();
        this.reward = reward;
        this.mask = mask;
    }

    public final float[] getState() {
        return state;
    }

    public final float getReward() {
        return reward;
    }

    public final boolean isMasked() {
        return mask;
    }

    @Override
    public String toString() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("state", state);
            map.put("reward", reward);
            map.put("mask", mask);
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
