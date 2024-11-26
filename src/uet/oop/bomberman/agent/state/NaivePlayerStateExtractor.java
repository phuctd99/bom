package uet.oop.bomberman.agent.state;

import java.util.concurrent.atomic.AtomicInteger;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.agent.state.base.PlayerStateExtractor;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.entities.tile.item.Item;

public class NaivePlayerStateExtractor extends PlayerStateExtractor {

    private static final float EPSILON = 0.01f;
    private static final int FIELD_OF_VISION = 15;

    public NaivePlayerStateExtractor(Character player) {
        super(player);
    }

    @Override
    public int getDimension() {
        return (
            FIELD_OF_VISION * FIELD_OF_VISION // isPassable
            + FIELD_OF_VISION * FIELD_OF_VISION // isItem
            + FIELD_OF_VISION * FIELD_OF_VISION // isEnemy
            + FIELD_OF_VISION * FIELD_OF_VISION // isBomb
            + FIELD_OF_VISION * FIELD_OF_VISION // isFlame
            + FIELD_OF_VISION * FIELD_OF_VISION // isDestroyable
            + FIELD_OF_VISION * FIELD_OF_VISION // isPortal
            + 1 // time
            + player.getValidActions().size() // action availability
        );
    }

    private interface BoardTilePredicate {
        float test(Board board, int x, int y);
    }

    @Override
    public float[] getEmbedding(Board board) {
        float[] embedding = new float[getDimension()];

        AtomicInteger currentIndex = new AtomicInteger(0);

        addSurroundingTileMask(board, embedding, currentIndex, this::isPassable);
        addSurroundingTileMask(board, embedding, currentIndex, this::isItem);
        addSurroundingTileMask(board, embedding, currentIndex, this::isEnemy);
        addSurroundingTileMask(board, embedding, currentIndex, this::isBomb);
        addSurroundingTileMask(board, embedding, currentIndex, this::isFlame);
        addSurroundingTileMask(board, embedding, currentIndex, this::isDestroyable);
        addSurroundingTileMask(board, embedding, currentIndex, this::isPortal);

        embedding[currentIndex.getAndIncrement()] = board.getGameInfoManager().getTime() * 1.0f / Game.TIME;

        for (Action action: player.getValidActions()) {
            if (player.canPerformAction(action)) {
                embedding[currentIndex.getAndIncrement()] = 1;
            } else {
                embedding[currentIndex.getAndIncrement()] = EPSILON;
            }
        }

        return embedding;
    }

    private void addSurroundingTileMask(Board board, float[] embedding, AtomicInteger currentIndex, BoardTilePredicate predicate) {
        for (int dy = -FIELD_OF_VISION / 2; dy <= FIELD_OF_VISION / 2; dy++) {
            for (int dx = -FIELD_OF_VISION / 2; dx <= FIELD_OF_VISION / 2; dx++) {
                int x = player.getXTile() + dx;
                int y = player.getYTile() + dy;
                float value = predicate.test(board, x, y);
                if (value == 0) {
                    value += EPSILON;
                }
                embedding[currentIndex.getAndIncrement()] = value;
            }
        }
    }

    private float isEnemy(Board board, int x, int y) {
        Character character = board.getEntityManager().getCharacterManager().getCharacterAtExcluding(x, y, player);
        if (character == null) return 0;
        return character.isPlayer() ? 0 : 1;
    }

    private float isBomb(Board board, int x, int y) {
        Entity entity = board.getEntityManager().getEntityAt(x, y);
        if (entity == null) return 0;
        if (entity instanceof Bomb) {
            return ((Bomb) entity)._timeAfter;
        }
        return 0;
    }

    private float isFlame(Board board, int x, int y) {
        Entity entity = board.getEntityManager().getEntityAt(x, y);
        if (entity == null) return 0;
        if (entity instanceof Flame || entity instanceof FlameSegment) {
            return 1;
        }
        return 0;
    }

    private float isItem(Board board, int x, int y) {
        Entity entity = board.getEntityManager().getEntityAt(x, y);
        if (entity == null) return 0;
        if (entity instanceof LayeredEntity) {
            LayeredEntity layeredEntity = (LayeredEntity) entity;
            return layeredEntity.getTopEntity() instanceof Item ? 1 : 0;
        }
        return 0;
    }

    private float isPassable(Board board, int x, int y) {
        Entity entity = board.getEntityManager().getEntityAt(x, y);
        if (entity == null) return 0;
        return entity.canBePassedThroughBy(player) ? 1 : 0;
    }

    private float isDestroyable(Board board, int x, int y) {
        Tile tile = board.getEntityManager().getTileManager().getTileAt(x, y);
        if (tile == null) return 0;
        if (tile.isDestroyable()) return 1;
        return 0;
    }

    private float isPortal(Board board, int x, int y) {
        Tile tile = board.getEntityManager().getTileManager().getTileAt(x, y);
        if (tile == null) return 0;
        if (tile instanceof LayeredEntity) {
            LayeredEntity layeredEntity = (LayeredEntity) tile;
            return layeredEntity.getTopEntity() instanceof Portal ? 1 : 0;
        }
        return 0;
    }

    @Override
    public float getValue(Board board) {
        float value = 0;

        // Penalize based on number of enemies still alive
        float enemyPoints = 0;
        for (Character character: board.getEntityManager().getCharacterManager().getCharacters()) {
            if (character == player) {
                continue;
            }
            if (character.isPlayer()) {
                continue;
            }
            enemyPoints += character.getPoints();
        }
        value -= enemyPoints;

        // Reward based on survival time
        value -= 5 * board.getGameInfoManager().getTime() / Game.TICKS_PER_SECOND;

        // Penalize based on number of destroyable tiles left
        float destroyableTiles = 0;
        for (int x = 0; x < board.getLevelManager().getBoardWidth(); x++) {
            for (int y = 0; y < board.getLevelManager().getBoardHeight(); y++) {
                Tile tile = board.getEntityManager().getTileManager().getTileAt(x, y);
                if (tile == null) {
                    continue;
                }
                if (tile.isDestroyable()) {
                    destroyableTiles++;
                }
            }
        }
        value -= destroyableTiles * 10;

        // Penalize based on number of items left
        float items = 0;
        for (int x = 0; x < board.getLevelManager().getBoardWidth(); x++) {
            for (int y = 0; y < board.getLevelManager().getBoardHeight(); y++) {
                Tile tile = board.getEntityManager().getTileManager().getTileAt(x, y);
                if (tile == null) {
                    continue;
                }
                if (!(tile instanceof LayeredEntity)) {
                    continue;
                }
                LayeredEntity layeredEntity = (LayeredEntity) tile;
                if (layeredEntity.hasItem()) {
                    items++;
                }
            }
        }
        value -= items * 50;

        return value;
    }

}
