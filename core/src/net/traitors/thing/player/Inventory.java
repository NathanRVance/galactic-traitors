package net.traitors.thing.player;

import net.traitors.thing.item.Item;
import net.traitors.ui.ScreenElements.InventoryBar;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Inventory implements Savable {

    private List<Item> inventory = new ArrayList<>();
    private int held = -1;
    private InventoryBar bar;

    Inventory(InventoryBar bar) {
        this.bar = bar;
        int capacity = 5;
        if(bar != null)  capacity = bar.getCapacity();
        for(int i = 0; i < capacity; i++) {
            inventory.add(null);
        }
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeInt(held);
        sd.writeList(inventory);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        held = saveData.readInt();
        List<Item> items = (List<Item>) saveData.readList();
        //System.out.println("\nStuff:");
        for(int i = 0; i < items.size(); i++) {
            inventory.set(i, items.get(i));
            //System.out.printf("%d: %s\n", i, items.get(i));
        }
        update();
    }

    Item getHeld() {
        if(held == -1) return null;
        return inventory.get(held);
    }

    Item get(int index) {
        return inventory.get(index);
    }

    void setHeld(int index) {
        held = index;
    }

    void addItem(Item item) {
        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i) == null) {
                inventory.set(i, item);
                break;
            }
        }
        update();
    }

    void remove(int index) {
        inventory.set(index, null);
        update();
    }

    void swapItems(int item1, int item2) {
        Collections.swap(inventory, item1, item2);
        update();
    }

    void updateCooldowns(float delta) {
        for (Item item : inventory) {
            if(item != null)
                item.act(delta);
        }
    }

    private void update() {
        if (bar == null) return;
        for (int i = 0; i < inventory.size(); i++) {
            bar.setItemAt(inventory.get(i), i);
        }
        if (held != -1) {
            bar.setTapped(held);
        }
    }

}
