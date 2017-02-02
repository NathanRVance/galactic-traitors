package net.traitors.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.traitors.thing.item.Gun;
import net.traitors.thing.player.Player;
import net.traitors.ui.touchable.Inventory;
import net.traitors.ui.touchable.SyncCameraButton;
import net.traitors.ui.touchable.Touchable;
import net.traitors.ui.touchable.TouchableTouchpad;
import net.traitors.controls.Controls;
import net.traitors.util.BetterCamera;
import net.traitors.util.TextureCreator;

import java.util.HashSet;
import java.util.Set;

public class TouchControls extends Stage implements Touchable {

    public static final int maxFingers = 10;
    //Has to be reasonably big, but I have no clue why. Maybe roundoff error?
    private static final float arbitraryNumber = 100;
    private boolean[] wasTouched = new boolean[maxFingers];
    //Finger touching the widget last render cycle. Index is finger, value is widget touched.
    private Touchable[] touched = new Touchable[maxFingers];

    private Set<Touchable> touchables = new HashSet<Touchable>();

    private TouchableTouchpad touchpad;

    public TouchControls(Player player, BetterCamera camera) {
        super(new ExtendViewport(arbitraryNumber, arbitraryNumber));
        float height = getHeight();
        float width = getWidth();

        float touchpadWidth = height / 5;
        float touchpadKnobWidth = touchpadWidth / 3;
        float touchpadDeadRadius = touchpadKnobWidth / 4;
        touchpad = new TouchableTouchpad(touchpadDeadRadius, TextureCreator.getTouchpadStyle(touchpadKnobWidth));
        touchpad.setBounds(0, 0, touchpadWidth, touchpadWidth);
        addTouchable(touchpad);

        float slotWidth = height / 5;
        Inventory inventory = new Inventory(this, 5, width - slotWidth, height - slotWidth * 4, slotWidth, slotWidth * 4, player);
        addTouchable(inventory);
        inventory.addItem(new Gun(.1f, .1f));

        float buttonDim = height / 6;
        SyncCameraButton button = new SyncCameraButton(camera);
        button.setBounds(0, height - buttonDim, buttonDim, buttonDim);
        addTouchable(button);

        Controls.registerTouchControls(this);
    }

    public <A extends Actor & Touchable> void addTouchable(A touchable) {
        addActor(touchable);
        touchables.add(touchable);
    }

    public float getTouchpadPercentX() {
        return touchpad.getKnobPercentX();
    }

    public float getTouchpadPercentY() {
        return touchpad.getKnobPercentY();
    }

    @Override
    public void act() {
        super.act();
        //Update touches
        for (int touch = 0; touch < touched.length; touch++) {
            if (!Gdx.input.isTouched(touch)) {
                touched[touch] = null;
                wasTouched[touch] = false;
            } else if (!wasTouched[touch]) { //Just touched
                wasTouched[touch] = true;
                for (Touchable touchable : touchables) {
                    if (touchable.isTouched() && !wasTouchedLastCycle(touchable)) { //And this was just touched, too!
                        touched[touch] = touchable;
                    }
                }
            }
        }
    }

    private boolean wasTouchedLastCycle(Touchable touchable) {
        for (Touchable t : touched) {
            if (t == touchable) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTouched() {
        for (Touchable t : touchables) {
            if (t.isTouched()) return true;
        }
        return false;
    }

    public boolean isTouched(int finger) {
        return touched[finger] != null;
    }
}
