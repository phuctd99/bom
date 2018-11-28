package uet.oop.bomberman;

import uet.oop.bomberman.gui.Frame;
import uet.oop.bomberman.sound.Sound;

public class BombermanGame {
	
	public static void main(String[] args) {
            Sound.play("soundtrack");
		new Frame();
	}
}
