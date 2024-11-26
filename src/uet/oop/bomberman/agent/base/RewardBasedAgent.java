package uet.oop.bomberman.agent.base;

public interface RewardBasedAgent {
    
    public void handleWinLevel();
    public void handleLoseLevel();

    public void addReward(float reward);

}
