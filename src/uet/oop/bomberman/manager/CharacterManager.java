package uet.oop.bomberman.manager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.base.ICharacterManager;
import uet.oop.bomberman.base.IGameInfoManager;
import uet.oop.bomberman.base.ILevelManager;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.character.CanUseItem;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.sound.Sound;
import uet.oop.bomberman.utils.Global;

public class CharacterManager implements ICharacterManager {

	private List<Character> characters = new ArrayList<>();
	// list
	private Character player;
	private List<Character> players = new ArrayList<>();

	private final IGameInfoManager gameInfoManager;
	private final ILevelManager levelManager;
	private int numberOfPlayers;

	public CharacterManager(IGameInfoManager gameInfoManager, ILevelManager levelManager) {
		this.gameInfoManager = gameInfoManager;
		this.levelManager = levelManager;
		// initializeNumberOfPlayers(0);
	}

	@Override
	public Character getCharacterAtExcluding(int x, int y, Character a) {
		Iterator<Character> itr = characters.iterator();

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
	public void addCharacter(Character e) {
		characters.add(e);
	}

	@Override
	public void setPlayer(Character character) {
		this.player = character;
	}

	@Override
	public Character getPlayer() {
		return this.player;
	}

	@Override
	public void addPlayer(Character e) {
		players.add(e);
		numberOfPlayers += 1;
	}

	@Override
	public List<Character> getPlayers() {
		return players;
	}

	@Override
	public void handleOnDeath(Character character, Character killer) {
		if (character.isPlayer()) {
			Sound.play("endgame3");
		} else {
			double messageX = (character.getX() * Game.SCALE) + (character.getSprite().SIZE / 2 * Game.SCALE);
			double messageY = (character.getY() * Game.SCALE) - (character.getSprite().SIZE / 2 * Game.SCALE);
			int points = character.getPoints();
			gameInfoManager.addPoints(points);
			Global.enemies--;
			Message msg = new Message("+" + points, messageX, messageY, 2, Color.white, 14);
			gameInfoManager.addMessage(msg);
			Sound.play("AA126_11");
		}
	}

	@Override
	public void handleAfterDeath(Character character) {
		if (character.isPlayer()) {
			numberOfPlayers--;
			if (numberOfPlayers == 0) {
				levelManager.endGame();
			}
		}
	}

	@Override
	public void update() {
		for (Character character : characters) {
			character.update();
			if (character instanceof CanUseItem) {
				CanUseItem characterCanUseItem = ((CanUseItem) character);
				characterCanUseItem.getActiveItems().forEach(Item::update);
			}
		}
		characters = characters.stream()
				.filter(character -> !character.isRemoved())
				.collect(Collectors.toList());
	}

	@Override
	public void render(Screen screen) {
		characters.forEach(character -> character.render(screen));
	}

	@Override
	public List<Character> getCharacters() {
		return characters;
	}

}
