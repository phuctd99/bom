package uet.oop.bomberman.base;

import java.util.List;

import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.graphics.IRender;

public interface ICharacterManager extends IRender {

    public List<Character> getCharacters();

    public Character getCharacterAtExcluding(int x, int y, Character a);

    public void addCharacter(Character e);

    public void setPlayer(Character character);

    public Character getPlayer();

    public void handleOnDeath(Character character, Character killer);

    public void handleAfterDeath(Character character);

    public void addPlayer(Character e);

    public List<Character> getPlayers();
}