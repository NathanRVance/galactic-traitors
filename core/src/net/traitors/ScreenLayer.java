package net.traitors;

import net.traitors.ui.ScreenElements.CompassBar;
import net.traitors.ui.ScreenElements.InventoryBar;
import net.traitors.ui.ScreenElements.Touchpad;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;

public class ScreenLayer extends LayerLayer {

    private Touchpad touchpad;
    private InventoryBar inventoryBar;
    private CompassBar compassBar;

    ScreenLayer() {
        super(new BetterCamera());
        getDefaultCamera().setToOrtho(false, 5, 5);
        //Need to add a touchpad, inventory, and compass bar.
        //Since there is a finite number of things, we'll keep track of them explicitly
        touchpad = new Touchpad(this, getHeight() / 5);
        touchpad.setPoint(getBotCorner().add(new Point(touchpad.getWidth() / 2, touchpad.getHeight() / 2)));
        addActor(touchpad);

        inventoryBar = new InventoryBar(this, 5, getHeight());
        //Since it is to the right, set point on resize
        addActor(inventoryBar);

        compassBar = new CompassBar(this, touchpad.getHeight() * 2 / 3);
        compassBar.setPoint(getBotCorner().add(new Point(compassBar.getWidth() / 2, getHeight() - compassBar.getHeight() / 2)));
        addActor(compassBar);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        inventoryBar.setPoint(getBotCorner().add(new Point(getWidth() - inventoryBar.getWidth() / 2, inventoryBar.getHeight() / 2)));
    }

    public InventoryBar getInventoryBar() {
        return inventoryBar;
    }
}
