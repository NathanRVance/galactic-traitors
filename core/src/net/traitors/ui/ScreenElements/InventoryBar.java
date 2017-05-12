package net.traitors.ui.ScreenElements;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.GalacticTraitors;
import net.traitors.Layer;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.item.Item;
import net.traitors.thing.player.Player;
import net.traitors.util.Point;

import java.util.List;

public class InventoryBar extends AbstractThing {

    private SelectableSwitch<InventorySlot> selectableSwitch = new SelectableSwitch<>();

    public InventoryBar(Layer layer, int numSlots, float height) {
        super(layer, height / numSlots, height);
        for (int s = 0; s < numSlots; s++) {
            InventorySlot slot = new InventorySlot(layer, selectableSwitch, getWidth(), getWidth());
            selectableSwitch.addSelectable(slot);
            GalacticTraitors.getInputProcessor().addCallback(slot);
        }
    }

    public void setPlayer(Player player) {
        for(InventorySlot slot : selectableSwitch.getSelectables()) {
            slot.setPlayer(player);
        }
    }

    public void setTapped(Item item) {
        for(InventorySlot slot : selectableSwitch.getSelectables()) {
            if(slot.getItem() == item) {
                selectableSwitch.selectableTapped(slot, true);
                break;
            }
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
    public void setPoint(Point point) {
        super.setPoint(point);
        Point p = getPoint().add(new Point(0, getHeight() / 2 - getWidth() / 2));
        for(InventorySlot slot : selectableSwitch.getSelectables()) {
            slot.setPoint(p);
            p = p.subtract(new Point(0, getWidth()));
        }
    }

    @Override
    public void draw(Batch batch) {
        //Forever, update ourselves to be to the right side of the layer
        setPoint(getLayer().getBotCorner().add(new Point(getLayer().getWidth() - getWidth() / 2, getHeight() / 2)));
        //Actually draw
        for(InventorySlot slot : selectableSwitch.getSelectables()) {
            slot.draw(batch);
        }
    }

    @Override
    public void dispose() {
        for(InventorySlot slot : selectableSwitch.getSelectables()) {
            GalacticTraitors.getInputProcessor().removeCallback(slot);
            slot.dispose();
        }
    }
}
