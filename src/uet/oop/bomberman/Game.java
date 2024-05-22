package uet.oop.bomberman;

import uet.oop.bomberman.base.IGameInfoManager;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.gui.Frame;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.screen.SelectGameModeScreen;
import uet.oop.bomberman.screen.SelectLevelScreen;
import uet.oop.bomberman.utils.EScreenName;
import uet.oop.bomberman.utils.Global;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

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
	public static final double BOMBERSPEED = 1.0;// toc do bomber

	public static final int TIME = 200;
	public static final int POINTS = 0;

	protected static int SCREENDELAY = 3;

	protected int _screenDelay = SCREENDELAY;

	private Keyboard _input;
	private boolean _running = false;
	private boolean _paused = true;
	private Board _board;
	private Screen screen;
	private Frame _frame;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

	// game variable
	private int frames;
	private int updates;
	private long timer;

	// game screens
	private SelectLevelScreen selectLevelScreen;
	private SelectGameModeScreen selectGameModeScreen;
	
	public Game(Frame frame) {
		_frame = frame;
		_frame.setTitle(TITLE);

		screen = new Screen(WIDTH, HEIGHT);
		_input = new Keyboard();

		_board = new Board(this, _input, screen);
		addKeyListener(_input);

		initScreen();
	
	}
	
	
	private void renderGame(Graphics g) {
		screen.clear();

		_board.render(screen);

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen._pixels[i];
		}
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		_board.renderMessages(g);
	}
	
	private void renderScreen(Graphics g) {
		screen.clear();
		_board.drawScreen(g);
	}

	private void initScreen() {
		Global.currentScreen = EScreenName.SELECT_GAME_MODE;

		this.selectGameModeScreen = new SelectGameModeScreen();
		this.selectLevelScreen = new SelectLevelScreen(_board);
	}

	private void update() {
		_input.update();
		switch (Global.currentScreen) {
			case GAME_PLAY_SCREEN:
				_board.update();
				if (_input.pause) { // Kiểm tra nếu phím "p" được nhấn
					_board.setShow(3); // Hiển thị màn hình tạm dừng
					_paused = true; // Đặt trạng thái game là tạm dừng
					return;
				}
				break;
			case SELECT_LEVEL_SCREEN:
				// TODO: call select level screen update
				selectLevelScreen.update();
				break;
			case SELECT_GAME_MODE:
				selectGameModeScreen.update();
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

		switch (Global.currentScreen) {
			case GAME_PLAY_SCREEN:
				if (_paused) {
					if (_screenDelay <= 0) {
						_board.setShow(-1);
						_paused = false;
					}

					renderScreen(g);
				} else {
					renderGame(g);
				}

				if (_input.resume) {
					_paused = false;
					_board.setShow(-1);
					}
				frames++;
				if (System.currentTimeMillis() - timer > 1000) {
					_frame.setTime(_board.subtractTime());
					_frame.setPoints(_board.getPoints());
					_frame.renderItemTime();
					timer += 1000;
					_frame.setTitle(TITLE + " | " + updates + " rate, " + frames + " fps");
					updates = 0;
					frames = 0;

					if (_board.getShow() == 2)
						--_screenDelay;
				}
				break;
			case SELECT_LEVEL_SCREEN:
				// TODO: render select level screen
				if (Global.currentScreen != Global.previousScreen) {
					selectLevelScreen.setInput(_input);
				}
				selectLevelScreen.drawScreen(g);
				break;
			case SELECT_GAME_MODE:
				if (Global.currentScreen != Global.previousScreen) {
					selectGameModeScreen.setInput(_input);
				}
				selectGameModeScreen.drawScreen(g);
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
		
		long  lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 60.0; //nanosecond, 60 frames per second
		double delta = 0;
		requestFocus();
		while (_running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				updates++;
				delta--;
			}

			showScreen();
		}
	}

	public void resetScreenDelay() {
		_screenDelay = SCREENDELAY;
	}
	public boolean isPaused() {
		return _paused;
	}

	public void pause() {
		_paused = !_paused;
	}

	public IGameInfoManager getGameInfoManager() {
		return _board;
	}

}
