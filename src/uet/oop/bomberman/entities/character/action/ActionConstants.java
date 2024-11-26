package uet.oop.bomberman.entities.character.action;

import java.util.Arrays;
import java.util.List;

public interface ActionConstants {

    public static final ActionMove MOVE_UP = new ActionMove(0, -1);
    public static final ActionMove MOVE_DOWN = new ActionMove(0, +1);
    public static final ActionMove MOVE_LEFT = new ActionMove(-1, 0);
    public static final ActionMove MOVE_RIGHT = new ActionMove(+1, 0);

    public static final ActionMove MOVE_UP_LEFT = new ActionMove(-1, -1);
    public static final ActionMove MOVE_UP_RIGHT = new ActionMove(+1, -1);
    public static final ActionMove MOVE_DOWN_LEFT = new ActionMove(-1, +1);
    public static final ActionMove MOVE_DOWN_RIGHT = new ActionMove(+1, +1);

    public static final List<ActionMove> LIST_ACTION_MOVE = Arrays.asList(new ActionMove[] {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        MOVE_UP_LEFT,
        MOVE_UP_RIGHT,
        MOVE_DOWN_LEFT,
        MOVE_DOWN_RIGHT,
    });

    public static final ActionPlaceBomb PLACE_BOMB = new ActionPlaceBomb();
    public static final Action DO_NOTHING = new ActionNoop();

}
