package net.traitors.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import net.traitors.GalacticTraitors;
import net.traitors.ui.TouchControls;
import net.traitors.util.Point;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

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

    public static void registerTouchControls(TouchControls touchControls) {
        Controls.touchControls = touchControls;
    }

    public static UserInput getUserInput() {
        UserInput ret = new UserInput();
        ret.pointsTouched = GalacticTraitors.getInputProcessor().getWorldTouches();
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

    public static class UserInput implements Savable {

        public List<Point> pointsTouched = new ArrayList<>();
        public Set<Key> keysPressed = new HashSet<>();

        @Override
        public SaveData getSaveData() {
            SaveData sd = new SaveData();
            sd.writeInt(pointsTouched.size());
            for (Point p : pointsTouched) {
                sd.writeFloat(p.x);
                sd.writeFloat(p.y);
            }
            sd.writeInt(keysPressed.size());
            for (Key key : keysPressed) {
                sd.writeString(key.name());
            }
            return sd;
        }

        @Override
        public void loadSaveData(SaveData saveData) {
            int numPoints = saveData.readInt();
            pointsTouched = new ArrayList<>(numPoints);
            for (int i = 0; i < numPoints; i++) {
                pointsTouched.add(new Point(saveData.readFloat(), saveData.readFloat()));
            }
            int numKeys = saveData.readInt();
            keysPressed = new HashSet<>(numKeys);
            for (int i = 0; i < numKeys; i++) {
                keysPressed.add(Key.valueOf(saveData.readString()));
            }
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other instanceof UserInput) {
                UserInput o = (UserInput) other;
                return pointsTouched.equals(o.pointsTouched) && keysPressed.equals(o.keysPressed);
            }
            return false;
        }
    }

}
