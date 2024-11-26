package uet.oop.bomberman.entities.character.enemy.ai;

import java.util.List;

import uet.oop.bomberman.base.IBombManager;
import uet.oop.bomberman.base.ICharacterManager;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.bomb.Bomb;

public class AIHigh extends AI {
    private final Character character;
    private final ICharacterManager characterManager;
    private final IBombManager bombManager;

    public AIHigh(Character character, ICharacterManager entityManager, IBombManager bombManager) {
        this.character = character;
        this.characterManager = entityManager;
        this.bombManager = bombManager;
    }

    @Override
    public int calculateDirection() {
        int safeDirection = calculateSafeDirection();
        if (safeDirection != -1) {
            return safeDirection;
        }

        int vertical = random.nextInt(2);

        if (vertical == 1) {
            int v = calculateRowDirection();
            if (v != -1)
                return v;
            else
                return calculateColDirection();

        } else {
            int h = calculateColDirection();

            if (h != -1)
                return h;
            else
                return calculateRowDirection();
        }
    }

    protected int calculateColDirection() {
        Character player = characterManager.getPlayer();

        if (player.getXTile() < character.getXTile())
            return 3;
        else if (player.getXTile() > character.getXTile())
            return 1;

        return -1;
    }

    protected int calculateRowDirection() {
        Character player = characterManager.getPlayer();

        if (player.getYTile() < character.getYTile())
            return 0;
        else if (player.getYTile() > character.getYTile())
            return 2;
        return -1;
    }

    protected int calculateSafeDirection() {
        List<Bomb> bombs = bombManager.getBombs();
        for (Bomb bomb : bombs) {
            int bombX = bomb.getXTile();
            int bombY = bomb.getYTile();
            int characterX = character.getXTile();
            int characterY = character.getYTile();
            double distance = Math.sqrt(Math.pow(bombX - characterX, 2) + Math.pow(bombY - characterY, 2));

            if (distance < 10) {
                if (bombX < characterX)
                    return 1; // Move right
                else if (bombX > characterX)
                    return 3; // Move left
                else if (bombY < characterY)
                    return 2; // Move down
                else if (bombY > characterY)
                    return 0; // Move up
            }
        }
        return -1;
    }

}