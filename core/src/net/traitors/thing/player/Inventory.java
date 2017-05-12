package net.traitors.thing.player;

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

    Inventory(InventoryBar bar) {
        this.bar = bar;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeList(inventory);
        if(held != null) {
            sd.writeBoolean(true);
            sd.writeInt(inventory.indexOf(held));
        } else {
            sd.writeBoolean(false);
        }
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        for (Item item : (List<Item>) saveData.readList()) {
            inventory.add(item);
        }
        if(saveData.readBoolean()) {
            held = inventory.get(saveData.readInt());
        }
    }

    Item getHeld() {
        return held;
    }

    void setHeld(Item item) {
        if (item != null && !inventory.contains(item)) {
            addItem(item);
        }
        held = item;
    }

    void addItem(Item item) {
        inventory.add(item);
        update();
    }

    void removeItem(Item item) {
        inventory.remove(item);
        update();
    }

    void swapItems(Item item1, Item item2) {
        Collections.swap(inventory, inventory.indexOf(item1), inventory.indexOf(item2));
        update();
    }

    void updateCooldowns(float delta) {
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
