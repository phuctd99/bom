package uet.oop.bomberman.utils;

public enum EGameLevel {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String level;

    EGameLevel(String level) {
        this.level = level;
    }

    public String getStringLevel() {
        return this.level;
    }
}
