package net.traitors.ui.touchable;

import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class TouchableTouchpad extends Touchpad implements Touchable {

    public TouchableTouchpad(float deadzoneRadius, TouchpadStyle style) {
        super(deadzoneRadius, style);
    }
}
