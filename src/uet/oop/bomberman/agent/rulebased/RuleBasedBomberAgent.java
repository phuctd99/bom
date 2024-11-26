package uet.oop.bomberman.agent.rulebased;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.agent.base.Agent;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.action.Action;
import uet.oop.bomberman.entities.character.action.ActionConstants;
import uet.oop.bomberman.entities.tile.Tile;

public class RuleBasedBomberAgent extends Agent {

    private Board board;

    public RuleBasedBomberAgent(Character character, Board board) {
        super(character);
        this.board = board;
    }

    @Override
    public List<Action> getNextActions() {
        int x = character.getXTile();
        int y = character.getYTile();
        boolean canPlaceBomb = character.canPerformAction(ActionConstants.PLACE_BOMB);
        boolean canDestroy = getDestroyableNeighbourEntities(x, y) > 0;
        if (canPlaceBomb && canDestroy) {
            return Arrays.asList(ActionConstants.PLACE_BOMB);
        }
        if (!character.isMoving()) {
            int width = board.getLevelManager().getBoardWidth();
            int height = board.getLevelManager().getBoardHeight();

            // BFS to find shortest path
            Queue<Coordinate> queue = new LinkedList<>();
            queue.add(new Coordinate(x, y, 0));
            boolean[][] visited = new boolean[width][height];
            int[][] distance = new int[width][height];
            visited[x][y] = true;
            int[][] parentX = new int[width][height];
            int[][] parentY = new int[width][height];
            while (!queue.isEmpty()) {
                Coordinate current = queue.poll();
                int[] dx = {-1, 1, 0, 0};
                int[] dy = {0, 0, -1, 1};
                for (int i = 0; i < dx.length; i++) {
                    int newX = current.x + dx[i];
                    int newY = current.y + dy[i];
                    if (newX < 0 || newX >= width || newY < 0 || newY >= height) {
                        continue;
                    }
                    if (visited[newX][newY]) {
                        continue;
                    }
                    Tile tile = board.getEntityManager().getTileManager().getTileAt(newX, newY);
                    if (!tile.canBePassedThroughBy(character)) {
                        continue;
                    }
                    visited[newX][newY] = true;
                    parentX[newX][newY] = current.x;
                    parentY[newX][newY] = current.y;
                    distance[newX][newY] = current.distance + 1;
                    queue.add(new Coordinate(newX, newY, current.distance + 1));
                }
            }

            int[][] tileScore = new int[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Entity entity = board.getEntityManager().getEntityAtExcluding(i, j, character);
                    if (entity == null) {
                        tileScore[i][j] = Integer.MIN_VALUE;
                        continue;
                    }
                    if (i == x && j == y) {
                        tileScore[i][j] = Integer.MIN_VALUE;
                        continue;
                    }
                    if (!entity.canBePassedThroughBy(character)) {
                        tileScore[i][j] = Integer.MIN_VALUE;
                        continue;
                    }
                    if (!visited[i][j]) {
                        tileScore[i][j] = Integer.MIN_VALUE;
                        continue;
                    }
                    if (getNeighbourBombs(i, j) > 0) {
                        tileScore[i][j] = Integer.MIN_VALUE;
                        continue;
                    }
                    if (entity instanceof Flame || entity instanceof FlameSegment) {
                        tileScore[i][j] = Integer.MIN_VALUE;
                        continue;
                    }
                    if (entity instanceof Character && !((Character) entity).isPlayer()) {
                        tileScore[i][j] = Integer.MIN_VALUE;
                        continue;
                    }
                    int destroyableEntities = getDestroyableNeighbourEntities(i, j);
                    tileScore[i][j] += destroyableEntities;

                    if (entity instanceof LayeredEntity && ((LayeredEntity) entity).hasItem()) {
                        tileScore[i][j] += 5;
                    }

                    if (distance[i][j] < 5) {
                        float bonus = tileScore[i][j] * (5 - distance[i][j]) / 5.0f;
                        tileScore[i][j] += bonus;
                    }
                }
            }
            int bestScore = Integer.MIN_VALUE;
            int bestX = x;
            int bestY = y;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (tileScore[i][j] > bestScore) {
                        bestScore = tileScore[i][j];
                        bestX = i;
                        bestY = j;
                    }
                }
            }
            if (bestX == x && bestY == y) {
                return Arrays.asList(ActionConstants.DO_NOTHING);
            }
            // trace back to current tile and output the immediate next action that player should perform
            int currentX = bestX;
            int currentY = bestY;
            while (parentX[currentX][currentY] != x || parentY[currentX][currentY] != y) {
                int nextX = parentX[currentX][currentY];
                int nextY = parentY[currentX][currentY];
                currentX = nextX;
                currentY = nextY;
            }
            if (currentX == x - 1) {
                return Arrays.asList(ActionConstants.MOVE_LEFT);
            }
            if (currentX == x + 1) {
                return Arrays.asList(ActionConstants.MOVE_RIGHT);
            }
            if (currentY == y - 1) {
                return Arrays.asList(ActionConstants.MOVE_UP);
            }
            if (currentY == y + 1) {
                return Arrays.asList(ActionConstants.MOVE_DOWN);
            }
        }
        
        return Arrays.asList(ActionConstants.DO_NOTHING);
    }

    private class Coordinate {
        int x;
        int y;
        int distance;
        Coordinate(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }

    private int getDestroyableNeighbourEntities(int x, int y) {
        int destroyableEntities = 0;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < dx.length; i++) {
            Entity entity = board.getEntityManager().getEntityAtExcluding(x + dx[i], y + dy[i], character);
            if (entity == null) continue;
            if (entity instanceof Tile && ((Tile) entity).isDestroyable()) {
                destroyableEntities += 1;
            }
            if (entity instanceof Character && !((Character) entity).isPlayer()) {
                destroyableEntities += 1;
            }
        }
        return destroyableEntities;
    }

    private int getNeighbourBombs(int x, int y) {
        int bombs = 0;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < dx.length; i++) {
            Entity entity = board.getEntityManager().getEntityAtExcluding(x + dx[i], y + dy[i], character);
            if (entity == null) continue;
            if (entity instanceof Bomb) bombs += 1;
        }
        return bombs;
    }
    
}
