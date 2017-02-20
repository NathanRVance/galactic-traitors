package net.traitors.ui.touchable;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.traitors.GameScreen;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.ui.TouchControls;

public class CompassBar extends Widget implements Touchable {

    private TouchControls stage;
    private SelectableSwitch<Compass> selectableSwitch = new SelectableSwitch<>();
    private float x;
    private float y;
    private float compassDim;

    public CompassBar(TouchControls stage, float x, float y, float compassDim) {
        this.stage = stage;
        this.x = x;
        this.y = y;
        this.compassDim = compassDim;
    }

    public void updateCompasses() {
        Platform p = GameScreen.getStuff().getPlayer().getPlatform();
        int compassIndex = 0;
        while (!(p instanceof NullPlatform)) {
            if (selectableSwitch.getSelectables().size() <= compassIndex) {
                Compass compass = new Compass(selectableSwitch, GameScreen.getStuff().getCamera());
                compass.setBounds(x + compassDim * compassIndex, y, compassDim, compassDim);
                selectableSwitch.addSelectable(compass);
                stage.addTouchable(compass);
            }
            selectableSwitch.getSelectables().get(compassIndex).setTrackThing(p);
            p = p.getPlatform();
            compassIndex++;
        }
        while (selectableSwitch.getSelectables().size() > compassIndex) {
            stage.removeTouchable(selectableSwitch.getSelectables().remove(compassIndex));
        }

        boolean selected = false;
        for (Compass compass : selectableSwitch.getSelectables()) {
            if (compass.isSelected()) {
                if (selected) {
                    //There can only be one.
                    compass.unselect();
                } else {
                    selected = true;
                }
            }
        }
        if (!selected && !selectableSwitch.getSelectables().isEmpty()) {
            selectableSwitch.getSelectables().get(0).select();
        }
    }

    @Override
    public boolean isTouched() {
        return selectableSwitch.isTouched();
    }
}
