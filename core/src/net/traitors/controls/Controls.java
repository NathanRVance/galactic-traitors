package net.traitors.controls;

import com.badlogic.gdx.Input;

import net.traitors.GalacticTraitors;
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

    private static Map<Integer, Key> keymap = new HashMap<>();
    private static Set<Key> pressed = new HashSet<>();

    static {
        keymap.put(Input.Keys.COMMA, Key.UP);
        keymap.put(Input.Keys.O, Key.DOWN);
        keymap.put(Input.Keys.A, Key.LEFT);
        keymap.put(Input.Keys.E, Key.RIGHT);
        keymap.put(Input.Keys.SHIFT_LEFT, Key.SPRINT);
    }

    public static void keyPressed(Key key) {
        pressed.add(key);
    }

    static void keyPressed(int keycode) {
        pressed.add(keymap.get(keycode));
    }

    public static void keyReleased(Key key) {
        pressed.remove(key);
    }

    static void keyReleased(int keycode) {
        pressed.remove(keymap.get(keycode));
    }

    public static UserInput getUserInput() {
        UserInput ret = new UserInput();
        ret.pointsTouched = GalacticTraitors.getInputProcessor().getWorldTouches();
        ret.keysPressed = new HashSet<>(pressed);
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
