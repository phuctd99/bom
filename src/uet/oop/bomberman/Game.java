package uet.oop.bomberman;

import uet.oop.bomberman.base.IGameInfoManager;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.gui.Frame;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.screen.SelectGameModeScreen;
import uet.oop.bomberman.screen.DeadScreen;
import uet.oop.bomberman.screen.SelectLevelScreen;
import uet.oop.bomberman.utils.EScreenName;
import uet.oop.bomberman.utils.Global;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Optional;

/**
 * Tạo vòng lặp cho game, lưu trữ một vài tham số cấu hình toàn cục,
 * Gọi phương thức render(), update() cho tất cả các entity
 */
public class Game extends Canvas {

	public static final int TILES_SIZE = 16,
			WIDTH = TILES_SIZE * (31 / 2),
			HEIGHT = 13 * TILES_SIZE;

	public static int SCALE = 3;

	public static final String TITLE = "BombermanGame";
	public static final int TICKS_PER_SECOND = 60;

	public static final int BOMBRATE = 1;
	public static final int BOMBRADIUS = 1;
	public static final double BOMBERSPEED = 3.0;// toc do bomber

	public static final int TIME = 200 * TICKS_PER_SECOND;
	public static final int POINTS = 0;

	protected static int SCREENDELAY = 3 * TICKS_PER_SECOND;

	protected int _screenDelay = SCREENDELAY;

	private boolean _running = false;
	private Board _board;
	private Screen screen;
	private Frame _frame;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	// game variable
	private int frames;
	private int updates;
	private long timer;

	// game screens
	public SelectLevelScreen selectLevelScreen;
	private SelectGameModeScreen selectGameModeScreen;
	public DeadScreen deadScreen;

	public boolean headless = false;

	private int _screenToShow = -1; // 1:endgame, 2:changelevel, 3:paused

	public Game(Frame frame) {
		_frame = frame;
		_frame.setTitle(TITLE);

		screen = new Screen(WIDTH, HEIGHT);

		_board = new Board(this, screen);
		addKeyListener(Keyboard.i());

		initScreen();

	}

	private void renderGame(Graphics g) {
		screen.clear();

		_board.render(screen);

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen._pixels[i];
		}

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		_board.getGameInfoManager().render(screen, g);
	}

	private void renderScreen(Graphics g) {
		screen.clear();
		drawScreen(g);
	}

	private void drawScreen(Graphics g) {
		switch (getScreenToShow()) {
			case 1:
				screen.drawEndGame(g, _board.getGameInfoManager().getPoints());
				break;
			case 2:
				screen.drawChangeLevel(g, Global.gameLevel);
				break;
			case 3:
				screen.drawPaused(g);
				break;
		}
	}

	private void initScreen() {
		Global.currentScreen = EScreenName.SELECT_GAME_MODE;
		this.selectGameModeScreen = new SelectGameModeScreen();
		this.selectLevelScreen = new SelectLevelScreen(_board, _frame);
		this.deadScreen = new DeadScreen(this);
	}

	private void update() {
		Keyboard.i().update();
		switch (Global.currentScreen) {
			case GAME_PLAY_SCREEN:
				_board.update();
				if (_screenToShow == -1 && Keyboard.i().pause) { // Kiểm tra nếu phím "p" được nhấn
					_screenToShow = 3; // Hiển thị màn hình tạm dừng
					_board.getGameInfoManager().pause(); // Đặt trạng thái game là tạm dừng
					return;
				}
				if (_screenToShow == 3 && Keyboard.i().resume) {
					_board.getGameInfoManager().unpause();
					_screenToShow = -1;
					_screenDelay = 0;
				}
				snapCameraToPlayer();
				break;
			case SELECT_LEVEL_SCREEN:
				selectLevelScreen.update();
				break;
			case SELECT_GAME_MODE:
				selectGameModeScreen.update();
				break;
			case END_GAME_SCREEN:
				deadScreen.update();
				break;
		}

	}

	private void showScreen() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		IGameInfoManager gameInfoManager = _board.getGameInfoManager();

		switch (Global.currentScreen) {
			case GAME_PLAY_SCREEN:
				Keyboard.i().keyboardInputCallback = Optional.empty();
				if (gameInfoManager.isPaused()) {
					if (_screenToShow == 2 && _screenDelay <= 0) {
						_screenToShow = -1;
						gameInfoManager.unpause();
					}

					renderScreen(g);
				} else {
					renderGame(g);
				}

				if (Keyboard.i().resume) {
					gameInfoManager.unpause();
					_screenToShow = -1;
					_screenDelay = 0;
				}

				if (_screenToShow == 2) --_screenDelay;

				frames++;
				if (System.currentTimeMillis() - timer > 1000) {
					_frame.setTime(gameInfoManager.getTime());
					_frame.setPoints(gameInfoManager.getPoints());
					_frame.setLevel(Global.gameLevel);
					_frame.setEnemy(Global.enemies);
					_frame.renderItemTime();

					timer += 1000;
					_frame.setTitle(TITLE + " | " + updates + " rate, " + frames + " fps");
					updates = 0;
					frames = 0;
				}
				break;
			case SELECT_LEVEL_SCREEN:
				// TODO: render select level screen
				selectLevelScreen.setInput(Keyboard.i());
				selectLevelScreen.drawScreen(g);
				break;
			case SELECT_GAME_MODE:
				selectGameModeScreen.setInput(Keyboard.i());
				selectGameModeScreen.drawScreen(g);
				break;
			case END_GAME_SCREEN:
				deadScreen.setInput();
				deadScreen.drawScreen(g);
				break;
		}

		g.dispose();
		bs.show();
	}

	private void initGame() {
		this.timer = System.currentTimeMillis();
		this.frames = 0;
		this.updates = 0;
	}

	public void start() {
		_running = true;

		initGame();

		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 60.0; // nanosecond, 60 frames per second
		double delta = 0;
		// requestFocus();
		while (_running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (headless) {
				// Headless mode: update as fast as possible, without rendering
				update();
				showScreen();
			} else {
				// Keep updating to catch up with 60 frames per second
				while (delta >= 1) {
					update();
					updates++;
					delta--;
				}
	
				showScreen();
			}
		}
	}

	public void stop() {
		_running = false;
	}

	private void snapCameraToPlayer() {
        int xScroll = calculateXOffset(_board.getEntityManager().getPlayer());
        Screen.setOffset(xScroll, 0);
    }

    private int calculateXOffset(Entity entity) {
    	if(entity == null) return 0;
    	int temp = Screen.xOffset;
    	
    	double x = entity.getX() / 16;
    	double complement = 0.5;
    	int firstBreakpoint = _board.getLevelManager().getBoardWidth() / 4;
    	int lastBreakpoint = _board.getLevelManager().getBoardWidth() - firstBreakpoint;
    	
    	if( x > firstBreakpoint + complement && x < lastBreakpoint - complement) {
    		temp = (int)entity.getX()  - (Game.WIDTH / 2);
    	}
    	
    	return temp;
    }

	public void resetScreenDelay() {
		_screenDelay = SCREENDELAY;
	}

	public Board getBoard() {
		return _board;
	}

	public void restartGame() {
		Global.currentScreen = EScreenName.GAME_PLAY_SCREEN;
		_board.getLevelManager().loadGlobalLevel();
	}

	public void startNewGame() {
		Global.currentScreen = EScreenName.SELECT_LEVEL_SCREEN;
	}

	public int getScreenToShow() {
		return _screenToShow;
	}

	public void setScreenToShow(int screenToShow) {
		this._screenToShow = screenToShow;
	}

}
