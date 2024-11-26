package uet.oop.bomberman.entities.character.action;

public class ActionPlaceBomb extends Action {

    protected ActionPlaceBomb() {}

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
    
}
