package uet.oop.bomberman.utils;

public enum EGameMode {
    ONE_PLAYER("1 PLAYER"),
    TWO_PLAYER("2 PLAYER");

    private final String mode;

    EGameMode(String level) {
        this.mode = level;
    }

    public String getStringLevel() {
        return this.mode;
    }
}
