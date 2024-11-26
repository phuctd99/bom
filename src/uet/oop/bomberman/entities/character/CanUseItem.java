package uet.oop.bomberman.entities.character;

import java.util.stream.Stream;

import uet.oop.bomberman.entities.tile.item.Item;

public interface CanUseItem {

    public Stream<Item> getActiveItems();
    public void addActiveItem(Item item);

}