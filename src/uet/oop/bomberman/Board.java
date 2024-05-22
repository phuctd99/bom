package uet.oop.bomberman;

import uet.oop.bomberman.base.IEntityManager;
import uet.oop.bomberman.base.IGameInfoManager;
import uet.oop.bomberman.base.IMessageManager;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.graphics.IRender;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.FileLevelLoader;
import uet.oop.bomberman.level.LevelLoader;
import uet.oop.bomberman.sound.Sound;
import uet.oop.bomberman.utils.Global;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Quản lý thao tác điều khiển, load level, render các màn hình của game
 */
public class Board implements IRender, IEntityManager, IMessageManager, IGameInfoManager {
	protected LevelLoader _levelLoader;
	protected Game _game;
	protected Keyboard _input;
	protected Screen _screen;

	public Entity[] _entities;
	public List<Character> _characters = new ArrayList<>();
	protected List<Bomb> _bombs = new ArrayList<>();
	private List<Message> _messages = new ArrayList<>();
	private List<Item> _activeItems = new ArrayList<>();

	private Character player;
	private Character player2;

	@Override
	public List<Item> getActiveItems() {
		return getPlayer().getActiveItems().collect(Collectors.toList());
	}

	private int _screenToShow = -1; // 1:endgame, 2:changelevel, 3:paused

	private int _time = Game.TIME;
	private int _points = Game.POINTS;

	public Board(Game game, Keyboard input, Screen screen) {
		_game = game;
		_input = input;
		_screen = screen;

		loadLevel(Global.gameLevel); // start in level 1
	}

	private void snapCameraToPlayer() {
		int xScroll = Screen.calculateXOffset(this, getPlayer());
		Screen.setOffset(xScroll, 0);
	}

	@Override
	public void update() {
		if (_game.isPaused())
			return;

		updateEntities();
		updateCharacters();
		updateBombs();
		updateMessages();
		updateActiveItems();
		detectEndGame();

		snapCameraToPlayer();
		processPlayerInput();
		processPlayer2Input();

		for (int i = 0; i < _characters.size(); i++) {
			Character a = _characters.get(i);
			if (a.isRemoved())
				_characters.remove(i);
		}
	}

	private void processPlayerInput() {
		Character player = getPlayer();
		if (!player.isAlive())
			return;

		processPlayerInputMove(player);

		if (player instanceof Bomber) {
			Bomber bomber = (Bomber) player;
			if (_input.player1_space)
				bomber.placeBomb();
		}
	}

	private void processPlayer2Input() {
		Character player2 = getPlayer2();
		if (!player2.isAlive())
			return;

		processPlayer2InputMove(player2);

		if (player2 instanceof Bomber) {
			Bomber bomber2 = (Bomber) player2;
			if (_input.player2_space)
				bomber2.placeBomb();
		}
	}

	private void processPlayerInputMove(Character player) {
		int xa = 0, ya = 0;
		if (_input.player1_up)
			ya--;
		if (_input.player1_down)
			ya++;
		if (_input.player1_left)
			xa--;
		if (_input.player1_right)
			xa++;

		if (xa != 0 || ya != 0) {
			player.move(xa * player.getSpeed(), ya * player.getSpeed());
			player.setMoving(true);
		} else {
			player.setMoving(false);
		}
	}

	private void processPlayer2InputMove(Character player2) {
		int xa = 0, ya = 0;
		if (_input.player2_up)
			ya--;
		if (_input.player2_down)
			ya++;
		if (_input.player2_left)
			xa--;
		if (_input.player2_right)
			xa++;

		if (xa != 0 || ya != 0) {
			player2.move(xa * player2.getSpeed(), ya * player2.getSpeed());
			player2.setMoving(true);
		} else {
			player2.setMoving(false);
		}
	}

	@Override
	public void render(Screen screen) {
		if (_game.isPaused())
			return;

		// only render the visible part of screen
		int x0 = Screen.xOffset >> 4; // tile precision, -> left X
		int x1 = (Screen.xOffset + screen.getWidth() + Game.TILES_SIZE) / Game.TILES_SIZE; // -> right X
		int y0 = Screen.yOffset >> 4;
		int y1 = (Screen.yOffset + screen.getHeight()) / Game.TILES_SIZE; // render one tile plus to fix black margins

		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				_entities[x + y * _levelLoader.getWidth()].render(screen);
			}
		}

		renderBombs(screen);
		renderCharacter(screen);

	}

	public void nextLevel() {
		loadLevel(_levelLoader.getLevel() + 1);
	}

	public void loadLevel(int level) {
		_time = Game.TIME;
		_screenToShow = 2;
		_game.resetScreenDelay();
		_game.pause();
		_characters.clear();
		_bombs.clear();
		_messages.clear();

		try {
			_levelLoader = new FileLevelLoader(this, level);
			_entities = new Entity[_levelLoader.getHeight() * _levelLoader.getWidth()];

			_levelLoader.createEntities();
		} catch (LoadLevelException e) {
			endGame();
		}
	}

	protected void detectEndGame() {
		if (_time <= 0)
			endGame();
	}

	public void endGame() {
		_screenToShow = 1;
		_game.resetScreenDelay();
		_game.pause();
	}

	@Override
	public boolean isEnemyCleared() {
		return !_characters.stream()
				.anyMatch(character -> character != getPlayer());
	}

	public void drawScreen(Graphics g) {
		switch (_screenToShow) {
			case 1:
				_screen.drawEndGame(g, _points);
				break;
			case 2:
				_screen.drawChangeLevel(g, _levelLoader.getLevel());
				break;
			case 3:
				_screen.drawPaused(g);
				break;
		}
	}

	@Override
	public Entity getEntity(double x, double y, Character m) {

		Entity res = null;

		res = getFlameSegmentAt((int) x, (int) y);
		if (res != null)
			return res;

		res = getBombAt(x, y);
		if (res != null)
			return res;

		res = getCharacterAtExcluding((int) x, (int) y, m);
		if (res != null)
			return res;

		res = getEntityAt((int) x, (int) y);

		return res;
	}

	@Override
	public List<Bomb> getBombs() {
		return _bombs;
	}

	@Override
	public Bomb getBombAt(double x, double y) {
		Iterator<Bomb> bs = _bombs.iterator();
		Bomb b;
		while (bs.hasNext()) {
			b = bs.next();
			if (b.getX() == (int) x && b.getY() == (int) y)
				return b;
		}

		return null;
	}

	@Override
	public Character getPlayer() {
		return this.player;
	}

	public Character getPlayer2() {
		return this.player2;
	}

	@Override
	public Character getCharacterAtExcluding(int x, int y, Character a) {
		Iterator<Character> itr = _characters.iterator();

		Character cur;
		while (itr.hasNext()) {
			cur = itr.next();
			if (cur == a) {
				continue;
			}

			if (cur.getXTile() == x && cur.getYTile() == y) {
				return cur;
			}

		}

		return null;
	}

	@Override
	public FlameSegment getFlameSegmentAt(int x, int y) {
		Iterator<Bomb> bs = _bombs.iterator();
		Bomb b;
		while (bs.hasNext()) {
			b = bs.next();

			FlameSegment e = b.flameAt(x, y);
			if (e != null) {
				return e;
			}
		}

		return null;
	}

	@Override
	public Entity getEntityAt(double x, double y) {
		return _entities[(int) x + (int) y * _levelLoader.getWidth()];
	}

	@Override
	public void addActiveItem(Item item) {
		_activeItems.add(item);
	}

	@Override
	public void addEntity(int pos, Entity e) {
		_entities[pos] = e;
	}

	@Override
	public void addCharacter(Character e) {
		_characters.add(e);
	}

	@Override
	public void addBomb(Bomb e) {
		_bombs.add(e);
	}

	@Override
	public void addMessage(Message e) {
		_messages.add(e);
	}

	protected void renderCharacter(Screen screen) {
		Iterator<Character> itr = _characters.iterator();

		while (itr.hasNext())
			itr.next().render(screen);
	}

	protected void renderBombs(Screen screen) {
		Iterator<Bomb> itr = _bombs.iterator();

		while (itr.hasNext())
			itr.next().render(screen);
	}

	public void renderMessages(Graphics g) {
		Message m;
		for (int i = 0; i < _messages.size(); i++) {
			m = _messages.get(i);

			g.setFont(new Font("Arial", Font.PLAIN, m.getSize()));
			g.setColor(m.getColor());
			g.drawString(m.getMessage(), (int) m.getX() - Screen.xOffset * Game.SCALE, (int) m.getY());
		}
	}

	protected void updateEntities() {
		if (_game.isPaused())
			return;
		for (int i = 0; i < _entities.length; i++) {
			_entities[i].update();
		}
	}

	protected void updateCharacters() {
		if (_game.isPaused())
			return;
		Iterator<Character> itr = _characters.iterator();

		while (itr.hasNext() && !_game.isPaused())
			itr.next().update();
	}

	protected void updateBombs() {
		if (_game.isPaused())
			return;
		Iterator<Bomb> itr = _bombs.iterator();

		while (itr.hasNext())
			itr.next().update();
	}

	protected void updateActiveItems() {
		if (_game.isPaused())
			return;
		Iterator<Item> itr = _activeItems.iterator();

		while (itr.hasNext())
			itr.next().update();
	}

	protected void updateMessages() {
		if (_game.isPaused())
			return;
		Message m;
		int left;
		for (int i = 0; i < _messages.size(); i++) {
			m = _messages.get(i);
			left = m.getDuration();

			if (left > 0)
				m.setDuration(--left);
			else
				_messages.remove(i);
		}
	}

	@Override
	public int subtractTime() {
		if (!_game.isPaused() && _time > 0)
			return --_time;
		else
			return _time;
	}

	public Keyboard getInput() {
		return _input;
	}

	public LevelLoader getLevel() {
		return _levelLoader;
	}

	public Game getGame() {
		return _game;
	}

	public int getShow() {
		return _screenToShow;
	}

	public void setShow(int i) {
		_screenToShow = i;
	}

	@Override
	public int getTime() {
		return _time;
	}

	@Override
	public int getPoints() {
		return _points;
	}

	@Override
	public void addPoints(int points) {
		this._points += points;
	}

	public int getWidth() {
		return _levelLoader.getWidth();
	}

	public int getHeight() {
		return _levelLoader.getHeight();
	}

	@Override
	public void setPlayer(Character character) {
		this.player = character;
	}

	public void setPlayer2(Character character2) {
		this.player2 = character2;
	}

	@Override
	public void handleOnDeath(Character character, Character killer) {
		if (character.isPlayer()) {
			// TODO: handle player death
			Sound.play("endgame3");
		} else {
			// TODO: document how to calculate message coord
			double messageX = (character.getX() * Game.SCALE) + (character.getSprite().SIZE / 2 * Game.SCALE);
			double messageY = (character.getY() * Game.SCALE) - (character.getSprite().SIZE / 2 * Game.SCALE);
			int points = character.getPoints();
			addPoints(points);
			Message msg = new Message("+" + points, messageX, messageY, 2, Color.white, 14);
			addMessage(msg);
			Sound.play("AA126_11");
		}
	}

}
