package uet.oop.bomberman.input;

import uet.oop.bomberman.utils.EGameControl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Optional;

/**
 * Tiếp nhận và xử lý các sự kiện nhập từ bàn phím
 */
public class Keyboard implements KeyListener {

	public interface KeyboardInputCallback {
		void onKeyPressed(EGameControl gameControl);
	}

	private boolean[] keys = new boolean[65536];
	public boolean up, down, left, right, space, pause, resume;
	public boolean player1_up, player2_up, player1_down, player2_down, player1_left, player2_left, player1_right,
			player2_right, player1_space, player2_space;
	public Optional<KeyboardInputCallback> keyboardInputCallback;

	public void update() {
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
		space = keys[KeyEvent.VK_SPACE] || keys[KeyEvent.VK_X];

		player1_up = keys[KeyEvent.VK_UP];
		player1_down = keys[KeyEvent.VK_DOWN];
		player1_left = keys[KeyEvent.VK_LEFT];
		player1_right = keys[KeyEvent.VK_RIGHT];
		player1_space = keys[KeyEvent.VK_SPACE];

		player2_up = keys[KeyEvent.VK_W];
		player2_down = keys[KeyEvent.VK_S];
		player2_left = keys[KeyEvent.VK_A];
		player2_right = keys[KeyEvent.VK_D];
		player2_space = keys[KeyEvent.VK_X];

		pause = keys[KeyEvent.VK_ESCAPE];
		resume = keys[KeyEvent.VK_ENTER];
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

		if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_X) {
			return EGameControl.SPACE;
		}

		if (keyCode == KeyEvent.VK_ENTER) {
			return EGameControl.ENTER;
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
