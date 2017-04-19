package net.traitors.ui.ScreenElements;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.GalacticTraitors;
import net.traitors.GameScreen;
import net.traitors.Layer;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.util.Point;

public class CompassBar extends AbstractThing {

    private SelectableSwitch<Compass> selectableSwitch = new SelectableSwitch<>();

    public CompassBar(Layer layer, float height) {
        super(layer, height, height);
    }

    @Override
    public void act(float delta) {
        Platform p = GameScreen.getPlayer().getPlatform();
        int compassIndex = 0;
        while (!(p instanceof NullPlatform)) {
            if (selectableSwitch.getSelectables().size() <= compassIndex) {
                Compass compass = new Compass(getLayer(), selectableSwitch, getHeight());
                compass.setPoint(new Point(getPoint().x + getHeight() * compassIndex, getPoint().y));
                selectableSwitch.addSelectable(compass);
                GalacticTraitors.getInputProcessor().addCallback(compass);
            }
            selectableSwitch.getSelectables().get(compassIndex).setTrackDepth(compassIndex + 1);
            p = p.getPlatform();
            compassIndex++;
        }
        while (selectableSwitch.getSelectables().size() > compassIndex) {
            GalacticTraitors.getInputProcessor().removeCallback(selectableSwitch.getSelectables().remove(compassIndex));
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
    public void draw(Batch batch) {
        for(Compass compass : selectableSwitch.getSelectables()) {
            compass.draw(batch);
        }
    }

    @Override
    public void dispose() {
        for(Compass compass : selectableSwitch.getSelectables()) {
            compass.dispose();
        }
    }
}
