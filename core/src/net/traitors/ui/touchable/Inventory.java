package net.traitors.ui.touchable;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.traitors.thing.item.Item;
import net.traitors.thing.player.Player;
import net.traitors.ui.TouchControls;

public class Inventory extends Widget implements Touchable {

    private SelectableSwitch<InventorySlot> selectableSwitch = new SelectableSwitch<InventorySlot>();
    private Player player;

    public Inventory(TouchControls stage, int numSlots, float x, float y, float width, float height, Player player) {
        this.player = player;
        player.setInventory(this);
        float itemHeight = height / numSlots;
        for (int s = 0; s < numSlots; s++) {
            InventorySlot slot = new InventorySlot(selectableSwitch, player);
            slot.setBounds(x, y + itemHeight * s, width, itemHeight);
            stage.addTouchable(slot);
            selectableSwitch.addSelectable(slot);
        }
    }

    public void addItem(Item item) {
        for (InventorySlot s : selectableSwitch.getSelectables()) {
            if (s.getItem() == null) {
                s.setItem(item);
                if (s.isSelected()) player.setHolding(item);
                break;
            }
        }
    }

    public void updateCooldowns(float delta) {
        Item holding = null;
        for (InventorySlot s : selectableSwitch.getSelectables()) {
            if (s.getItem() != null) {
                s.getItem().act(delta);
                if(s.isSelected()) holding = s.getItem();
            }
        }
        player.setHolding(holding);
    }

    @Override
    public boolean isTouched() {
        return selectableSwitch.isTouched();
    }
}
