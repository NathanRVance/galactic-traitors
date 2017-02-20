package net.traitors.ui.touchable;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.traitors.thing.item.Item;
import net.traitors.ui.TouchControls;

import java.util.List;

public class InventoryBar extends Widget implements Touchable {

    private SelectableSwitch<InventorySlot> selectableSwitch = new SelectableSwitch<>();

    public InventoryBar(TouchControls stage, int numSlots, float x, float y, float width, float height) {
        float itemHeight = height / numSlots;
        for (int s = 0; s < numSlots; s++) {
            InventorySlot slot = new InventorySlot(selectableSwitch);
            slot.setBounds(x, y + itemHeight * s, width, itemHeight);
            stage.addTouchable(slot);
            selectableSwitch.addSelectable(slot);
        }
    }

    /**
     * Sets an item at the specified index
     *
     * @param item  item to be added
     * @param index location to added
     * @return the displaced item or null
     */
    public Item setItemAt(Item item, int index) {
        List<InventorySlot> slots = selectableSwitch.getSelectables();
        if (index >= getCapacity()) throw new IllegalArgumentException("Bad index!");
        Item ret = slots.get(index).getItem();
        slots.get(index).setItem(item);
        slots.get(index).unselect();
        return ret;
    }

    public int getCapacity() {
        return selectableSwitch.getSelectables().size();
    }

    @Override
    public boolean isTouched() {
        return selectableSwitch.isTouched();
    }
}
