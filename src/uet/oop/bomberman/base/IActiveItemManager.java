package uet.oop.bomberman.base;

import java.util.List;

import uet.oop.bomberman.entities.tile.item.Item;

public interface IActiveItemManager {

    public List<Item> getActiveItems();
    public void addActiveItem(Item item);

}