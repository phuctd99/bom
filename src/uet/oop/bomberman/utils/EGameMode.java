package uet.oop.bomberman.utils;

public enum EGameMode {
    ONE_PLAYER("1 Player"),
    TWO_PLAYER("2 Players");

    private final String mode;

    EGameMode(String level) {
        this.mode = level;
    }

    public String getStringLevel() {
        return this.mode;
    }
}
