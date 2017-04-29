package net.traitors.thing.player;

import net.traitors.GameScreen;
import net.traitors.thing.item.Item;
import net.traitors.ui.ScreenElements.InventoryBar;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory implements Savable {

    private List<Item> inventory = new ArrayList<>();
    private Item held;
    private InventoryBar bar;

    public Inventory(InventoryBar bar) {
        this.bar = bar;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeList(inventory, held);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        for (Item item : saveData.readList(inventory, Item.class)) {
            inventory.add(item);
        }
        held = (Item) saveData.getFlaggedSavable();
    }

    public Item getHeld() {
        return held;
    }

    public void setHeld(Item item) {
        if (item != null && !inventory.contains(item)) {
            addItem(item);
        }
        held = item;
    }

    public void addItem(Item item) {
        inventory.add(item);
        update();
    }

    public void removeItem(Item item) {
        inventory.remove(item);
        update();
    }

    public void swapItems(Item item1, Item item2) {
        Collections.swap(inventory, inventory.indexOf(item1), inventory.indexOf(item2));
        update();
    }

    public void updateCooldowns(float delta) {
        for (Item item : inventory) {
            item.act(delta);
        }
    }

    private void update() {
        if (bar == null) return;
        for (int i = 0; i < inventory.size() && i < bar.getCapacity(); i++) {
            bar.setItemAt(inventory.get(i), i);
        }
        for (int i = inventory.size(); i < bar.getCapacity(); i++) {
            bar.setItemAt(null, i);
        }
        if (held != null) {
            bar.setTapped(held);
        }
    }

}
