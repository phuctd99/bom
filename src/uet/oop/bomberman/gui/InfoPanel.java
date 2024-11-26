package uet.oop.bomberman.gui;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.utils.EGameMode;
import uet.oop.bomberman.utils.EScreenName;
import uet.oop.bomberman.utils.Global;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Swing Panel hiển thị thông tin thời gian, điểm mà người chơi đạt được
 */
public class InfoPanel extends JPanel {
	private JLabel timeLabel;
	private JLabel p2TimeLabel;
	private JLabel pointsLabel;
	private JLabel itemTimeLabel;
	private JLabel levelLabel;
	private JLabel enemyLabel;

	private final Game game;

	public InfoPanel(Game game) {
		this.game = game;
		setLayout(new GridLayout());

		levelLabel = new JLabel("Level: " + Global.gameLevel);
		levelLabel.setForeground(Color.white);
		levelLabel.setHorizontalAlignment(JLabel.CENTER);

		enemyLabel = new JLabel("Enemies: " + Global.enemies);
		enemyLabel.setForeground(Color.white);
		enemyLabel.setHorizontalAlignment(JLabel.CENTER);

		timeLabel = new JLabel("Time: " + game.getBoard().getGameInfoManager().getTime());
		timeLabel.setForeground(Color.white);
		timeLabel.setHorizontalAlignment(JLabel.CENTER);

		pointsLabel = new JLabel("Points: " + game.getBoard().getGameInfoManager().getPoints());
		pointsLabel.setForeground(Color.white);
		pointsLabel.setHorizontalAlignment(JLabel.CENTER);

		itemTimeLabel = new JLabel("");
		itemTimeLabel.setForeground(Color.white);
		itemTimeLabel.setHorizontalAlignment(JLabel.CENTER);
		itemTimeLabel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

		p2TimeLabel = new JLabel("");
		p2TimeLabel.setForeground(Color.white);
		p2TimeLabel.setHorizontalAlignment(JLabel.CENTER);
		p2TimeLabel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

		setBackground(Color.black);
		setPreferredSize(new Dimension(0, 40));
	}

	public void setTime(int t) {
		timeLabel.setText("Time: " + t);
	}

	public void setPoints(int t) {
		pointsLabel.setText("Score: " + t);
	}

	public void setLevel(int t) {
		levelLabel.setText("Level: " + t);
	}

	public void setEnemies(int t) {
		enemyLabel.setText("Enemies: " + t);
	}

	public void renderItemTime() {
		String label = "";

		List<Item> items = game.getBoard().getGameInfoManager().getPlayerActiveItems();
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if ((item.getDuration()) == 0) {
				continue;
			}
			label += item.getDisplayActiveItem() + item.getDuration() / Game.TICKS_PER_SECOND + " ";
		}

		if (Global.gameMode == EGameMode.TWO_PLAYER) {
			String label2 = "";
			List<Item> p2Items = game.getBoard().getGameInfoManager().getPlayer2ActiveItems();
			for (int i = 0; i < p2Items.size(); i++) {
				Item item = p2Items.get(i);
				if ((item.getDuration()) == 0) {
					continue;
				}
				label2 += item.getDisplayActiveItem() + item.getDuration() / Game.TICKS_PER_SECOND + " ";
			}

			if (label2 != "") {
				label2 = "P2 " + label2;
			}
			if (label != "") {
				label = "P1 " + label;
			}

			p2TimeLabel.setText(label2);
			itemTimeLabel.setText(label);
		} else {
			itemTimeLabel.setText(label);
		}
	}

	public void hideInfoPanel() {
		if (Global.currentScreen == EScreenName.GAME_PLAY_SCREEN) {
			System.out.println("vao day");
			remove(itemTimeLabel);
			remove(timeLabel);
			remove(pointsLabel);
			remove(enemyLabel);
			remove(p2TimeLabel);
		}
	}

	public void loadInfo() {
		add(itemTimeLabel);
		add(timeLabel);
		add(pointsLabel);
		add(enemyLabel);

		if (Global.gameMode == EGameMode.TWO_PLAYER) {
			add(p2TimeLabel);
		}
	}
}
