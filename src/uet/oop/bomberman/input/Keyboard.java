package uet.oop.bomberman.input;

import uet.oop.bomberman.utils.EGameControl;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Optional;

/**
 * Tiếp nhận và xử lý các sự kiện nhập từ bàn phím
 */
public class Keyboard implements KeyListener {

	private static Keyboard INST = null;

	public static Keyboard i() {
		if (INST == null) {
			INST = new Keyboard();
		}
		return INST;
	}

	private Keyboard() {
	}

	public interface KeyboardInputCallback {
		void onKeyPressed(EGameControl gameControl);
	}

	private boolean[] keys = new boolean[65536]; // 120 is enough to this game
	public boolean up, down, left, right, space, x, pause, resume;
	public Optional<KeyboardInputCallback> keyboardInputCallback;

	public boolean player1_up, player1_down, player1_left, player1_right, player1_bomb;
	public boolean player2_up, player2_down, player2_left, player2_right, player2_bomb;

	public void update() {
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];

		space = keys[KeyEvent.VK_SPACE];
		x = keys[KeyEvent.VK_X];

		// Player 1
		player1_up = keys[KeyEvent.VK_W];
		player1_down = keys[KeyEvent.VK_S];
		player1_left = keys[KeyEvent.VK_A];
		player1_right = keys[KeyEvent.VK_D];
		player1_bomb = keys[KeyEvent.VK_X];

		// Player 2
		player2_up = keys[KeyEvent.VK_UP];
		player2_down = keys[KeyEvent.VK_DOWN];
		player2_left = keys[KeyEvent.VK_LEFT];
		player2_right = keys[KeyEvent.VK_RIGHT];
		player2_bomb = keys[KeyEvent.VK_SPACE];

	}

	private EGameControl keyToGameControl(int keyCode) {
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
			return EGameControl.UP;
		}

		if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
			return EGameControl.DOWN;
		}

		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
			return EGameControl.LEFT;
		}

		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
			return EGameControl.RIGHT;
		}

		if (keyCode == KeyEvent.VK_SPACE) {
			return EGameControl.SPACE;
		}

		if (keyCode == KeyEvent.VK_ENTER) {
			return EGameControl.ENTER;
		}
		if (keyCode == KeyEvent.VK_X) {
			return EGameControl.X;
		}

		return EGameControl.NONE;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		if (keyboardInputCallback.isPresent()) {
			KeyboardInputCallback callback = keyboardInputCallback.get();
			callback.onKeyPressed(keyToGameControl(e.getKeyCode()));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;

	}

}