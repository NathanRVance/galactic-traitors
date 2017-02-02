package net.traitors.ui.touchable;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.traitors.thing.item.Item;
import net.traitors.thing.player.Player;
import net.traitors.ui.TouchControls;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Widget implements Touchable {

    private List<InventorySlot> slots;
    private Player player;

    public Inventory(TouchControls stage, int numSlots, float x, float y, float width, float height, Player player) {
        this.player = player;
        player.setInventory(this);
        slots = new ArrayList<InventorySlot>(numSlots);
        float itemHeight = height / numSlots;
        for (int s = 0; s < numSlots; s++) {
            InventorySlot slot = new InventorySlot(this);
            slot.setBounds(x, y + itemHeight * s, width, itemHeight);
            stage.addTouchable(slot);
            slots.add(slot);
        }
    }

    void slotTapped(InventorySlot slot, boolean selected) {
        for (InventorySlot s : slots) {
            s.unselect();
        }
        if (selected) {
            slot.select();
            player.setHolding(slot.getItem());
        } else {
            slot.unselect();
            player.setHolding(null);
        }
    }

    public void addItem(Item item) {
        for (InventorySlot s : slots) {
            if (s.getItem() == null) {
                s.setItem(item);
                if(s.isSelected()) player.setHolding(item);
                break;
            }
        }
    }

    Player getPlayer() {
        return player;
    }

    @Override
    public boolean isTouched() {
        for (InventorySlot slot : slots) {
            if (slot.isTouched()) return true;
        }
        return false;
    }
}
