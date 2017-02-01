package net.traitors.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;

import net.traitors.ui.TouchControls;
import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controls {

    private static Map<Key, Integer> keymap = new HashMap<Key, Integer>();
    private static TouchControls touchControls;

    static {
        keymap.put(Key.UP, Input.Keys.COMMA);
        keymap.put(Key.DOWN, Input.Keys.O);
        keymap.put(Key.LEFT, Input.Keys.A);
        keymap.put(Key.RIGHT, Input.Keys.E);
        keymap.put(Key.SPRINT, Input.Keys.SHIFT_LEFT);
    }

    public static boolean isKeyPressed(Key key) {
        return Gdx.input.isKeyPressed(keymap.get(key)) || isPressedByTouchpad(key);
    }

    private static boolean isPressedByTouchpad(Key key) {
        if (touchControls == null) return false;
        switch (key) {
            case SPRINT:
                return Math.sqrt(Math.pow(touchControls.getTouchpadPercentX(), 2) + Math.pow(touchControls.getTouchpadPercentY(), 2)) > .95;
            case UP:
                return touchControls.getTouchpadPercentY() > .25;
            case DOWN:
                return touchControls.getTouchpadPercentY() < -.25;
            case LEFT:
                return touchControls.getTouchpadPercentX() < -.25;
            case RIGHT:
                return touchControls.getTouchpadPercentX() > .25;
            default:
                return false;
        }
    }

    public static List<Point> getWorldTouches(Camera camera) {
        List<Point> points = new ArrayList<Point>(TouchControls.maxFingers);
        for (int i = 0; i < TouchControls.maxFingers; i++) {
            if (Gdx.input.isTouched(i) && (touchControls == null || !touchControls.isTouched(i))) {
                points.add(new Point(Gdx.input.getX(i), Gdx.input.getY(i)).unproject(camera));
            }
        }
        return points;
    }

    public static void registerTouchControls(TouchControls touchControls) {
        Controls.touchControls = touchControls;
    }

    public enum Key {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SPRINT,
    }

}
