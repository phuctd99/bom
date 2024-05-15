package uet.oop.bomberman.gui;
import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.tile.item.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Swing Panel hiển thị thông tin thời gian, điểm mà người chơi đạt được
 */
public class InfoPanel extends JPanel {
	private JLabel timeLabel;
	private JLabel pointsLabel;
	private JLabel itemTimeLabel;
	private Game game;
	public InfoPanel(Game game) {
		setLayout(new GridLayout());
		timeLabel = new JLabel("Time: " + game.getBoard().getTime());
		timeLabel.setForeground(Color.white);
		timeLabel.setHorizontalAlignment(JLabel.CENTER);

		pointsLabel = new JLabel("Points: " + game.getBoard().getPoints());
		pointsLabel.setForeground(Color.white);
		pointsLabel.setHorizontalAlignment(JLabel.CENTER);

		itemTimeLabel = new JLabel("");
		itemTimeLabel.setForeground(Color.white);
		itemTimeLabel.setHorizontalAlignment(JLabel.LEFT);
		itemTimeLabel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

		add(itemTimeLabel);
		add(timeLabel);
		add(pointsLabel);
		setBackground(Color.black);
		setPreferredSize(new Dimension(0, 40));
	}

	public void setTime(int t) {
		timeLabel.setText("Time: " + t);
	}

	public void setPoints(int t) {
		pointsLabel.setText("Score: " + t);
	}

	public void setItemTime(int t) {
		String label = "";
		List<Item> items = game.getBoard().getActiveItems();
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if ((item.getDuration()) == 0) {
				continue;
			}
			label += item.getDisplayActiveItem() + item.getDuration() / 60 + " ";
		}
		itemTimeLabel.setText(label);
	}

}
