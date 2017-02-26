package net.traitors.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;

import net.traitors.GameScreen;
import net.traitors.ui.TouchControls;
import net.traitors.util.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Controls {

    private static Map<Key, Integer> keymap = new HashMap<>();
    private static TouchControls touchControls;

    static {
        keymap.put(Key.UP, Input.Keys.COMMA);
        keymap.put(Key.DOWN, Input.Keys.O);
        keymap.put(Key.LEFT, Input.Keys.A);
        keymap.put(Key.RIGHT, Input.Keys.E);
        keymap.put(Key.SPRINT, Input.Keys.SHIFT_LEFT);
    }

    private static boolean isKeyPressed(Key key) {
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

    private static List<Point> getWorldTouches(Camera camera) {
        List<Point> points = new ArrayList<>(TouchControls.maxFingers);
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

    public static UserInput getUserInput() {
        UserInput ret = new UserInput();
        ret.pointsTouched = getWorldTouches(GameScreen.getStuff().getCamera());
        ret.keysPressed = new HashSet<>();
        for (Key key : Key.values()) {
            if (isKeyPressed(key)) {
                ret.keysPressed.add(key);
            }
        }
        return ret;
    }

    public enum Key {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SPRINT,
    }

    public static class UserInput implements Serializable {

        private static final long serialVersionUID = 4990555252952478767L;
        public List<Point> pointsTouched = new ArrayList<>();
        public Set<Key> keysPressed = new HashSet<>();

        @Override
        public boolean equals(Object other) {
            if(this == other) return true;
            if(other instanceof UserInput) {
                UserInput o = (UserInput) other;
                return pointsTouched.equals(o.pointsTouched) && keysPressed.equals(o.keysPressed);
            }
            return false;
        }

    }

}
