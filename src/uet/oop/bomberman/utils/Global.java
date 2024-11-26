package uet.oop.bomberman.utils;

public class Global {
    public static int screenWidth;
    public static int screenHeight;

    public static EScreenName currentScreen = EScreenName.SELECT_GAME_MODE;
    public static EScreenName previousScreen = EScreenName.GAME_PLAY_SCREEN;

    // GAME PLAY
    public static int gameLevel = 1;
    public static EGameMode gameMode;
    public static int enemies = 0;
}
