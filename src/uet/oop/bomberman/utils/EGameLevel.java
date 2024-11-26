package uet.oop.bomberman.utils;

public enum EGameLevel {
    EASY("EASY"),
    MEDIUM("MEDIUM"),
    HARD("HARD"),
    BACK("BACK (B)");

    private final String level;

    EGameLevel(String level) {
        this.level = level;
    }

    public String getStringLevel() {
        return this.level;
    }
}
