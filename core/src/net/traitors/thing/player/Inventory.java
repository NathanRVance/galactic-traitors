package net.traitors.thing.player;

import net.traitors.GameScreen;
import net.traitors.thing.item.Gun;
import net.traitors.thing.item.Item;
import net.traitors.ui.touchable.InventoryBar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory implements Serializable {

    private static final long serialVersionUID = -3541440266159851697L;
    private List<Item> inventory;

    public Inventory() {
        inventory = new ArrayList<>();
        //Populate with default inventory
        inventory.add(new Gun(.1f, .1f));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        update();
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

    public void update() {
        InventoryBar inventoryBar = GameScreen.getTouchControls().getInventoryBar();
        for (int i = 0; i < inventory.size() && i < inventoryBar.getCapacity(); i++) {
            inventoryBar.setItemAt(inventory.get(i), i);
        }
        for (int i = inventory.size(); i < inventoryBar.getCapacity(); i++) {
            inventoryBar.setItemAt(null, i);
        }
    }


}
