package net.traitors.controls;

import com.badlogic.gdx.Input;

import net.traitors.GalacticTraitors;
import net.traitors.util.Point;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Controls {

    /**
     * All high level operations must go through this class. This includes:
     * - Player movement
     * - Clicks in world
     * - Menu operations
     * - Inventory operations
     */

    private static Map<Integer, Key> keymap = new HashMap<>();
    private static Set<Key> pressed = new HashSet<>();
    private static Queue<OperationStruct> operations = new LinkedList<>();
    private static Queue<OperationStruct> sendOpts = new LinkedList<>();
    private static Map<Long, UserInput> inputs = new HashMap<>();
    public static long ID = -1;

    static {
        keymap.put(Input.Keys.COMMA, Key.UP);
        keymap.put(Input.Keys.O, Key.DOWN);
        keymap.put(Input.Keys.A, Key.LEFT);
        keymap.put(Input.Keys.E, Key.RIGHT);
        keymap.put(Input.Keys.SHIFT_LEFT, Key.SPRINT);
    }

    public static void setPlayerID(long ID) {
        Controls.ID = ID;
    }

    public static void keyPressed(Key key) {
        pressed.add(key);
    }

    static void keyPressed(int keycode) {
        if (keymap.containsKey(keycode))
            pressed.add(keymap.get(keycode));
    }

    public static void keyReleased(Key key) {
        pressed.remove(key);
    }

    static void keyReleased(int keycode) {
        pressed.remove(keymap.get(keycode));
    }

    public static void operationPerformed(Operation opt, SaveData data) {
        operations.add(new OperationStruct(opt, data));
        sendOpts.add(new OperationStruct(opt, data));
        if(sendOpts.size() > 100)
            sendOpts.remove();
    }

    /*
    Operations are perfromed locally and sent.
    Each operation is performed exactly once locally, and zero or one times sent.
    Keep operations in a queue:
     - deliver once on update()
     - deliver once on getInputToSend()
     - enforce max queue length (push stale ops off)
     */

    public static void setInput(long userID, UserInput input) {
        if (inputs.containsKey(userID) && inputs.get(userID).operations != input.operations) {
            Queue<OperationStruct> newOpts = input.operations;
            Queue<OperationStruct> oldOpts = inputs.get(userID).operations;
            while (!newOpts.isEmpty()) {
                oldOpts.add(newOpts.remove());
            }
            input.operations = oldOpts;
        }
        inputs.put(userID, input);
    }

    public static UserInput getInput(long ID) {
        if (inputs.containsKey(ID)) {
            return inputs.get(ID);
        } else {
            return new UserInput(ID);
        }
    }

    private static UserInput getBaseInput() {
        UserInput input = new UserInput(ID);
        input.pointsTouched = GalacticTraitors.getInputProcessor().getWorldTouches();
        input.keysPressed = new HashSet<>(pressed);
        return input;
    }

    //Obtains input for sending
    public static UserInput getInputToSend() {
        UserInput input = getBaseInput();
        input.operations = sendOpts;
        sendOpts = new LinkedList<>();
        return input;
    }

    public static void update() {
        UserInput input = getBaseInput();
        input.operations = operations; //same instance
        setInput(ID, input);
    }

    public enum Key {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SPRINT,
    }

    public enum Operation {
        SWAP,
        DROP,
        HOLD,
        AUTOSTOP
    }

    public static class OperationStruct implements Savable {

        private Operation operation;
        private SaveData data;

        OperationStruct(Operation operation, SaveData data) {
            this.operation = operation;
            this.data = data;
        }

        public Operation getOperation() {
            return operation;
        }

        public SaveData getData() {
            return new SaveData(data.toString());
        }

        @Override
        public SaveData getSaveData() {
            SaveData sd = new SaveData();
            sd.writeString(operation.name());
            sd.writeSaveData(data);
            return sd;
        }

        @Override
        public void loadSaveData(SaveData saveData) {
            operation = Operation.valueOf(saveData.readString());
            data = saveData.readSaveData();
        }
    }

    public static class UserInput implements Savable {

        public List<Point> pointsTouched = new ArrayList<>();
        public Set<Key> keysPressed = new HashSet<>();
        public Queue<OperationStruct> operations = new LinkedList<>();
        public long ID; //ID of actor this input is for

        public UserInput(long ID) {
            this.ID = ID;
        }

        @Override
        public SaveData getSaveData() {
            SaveData sd = new SaveData();
            sd.writeLong(ID);
            sd.writeInt(pointsTouched.size());
            for (Point p : pointsTouched) {
                sd.writeFloat(p.x);
                sd.writeFloat(p.y);
            }
            sd.writeInt(keysPressed.size());
            for (Key key : keysPressed) {
                sd.writeString(key.name());
            }
            sd.writeInt(operations.size());
            while (!operations.isEmpty()) {
                sd.writeSaveData(operations.remove().getSaveData());
            }
            return sd;
        }

        @Override
        public void loadSaveData(SaveData saveData) {
            ID = saveData.readLong();
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
            int numOpts = saveData.readInt();
            operations = new LinkedList<>();
            for (int i = 0; i < numOpts; i++) {
                OperationStruct opt = new OperationStruct(null, null);
                opt.loadSaveData(saveData.readSaveData());
                operations.add(opt);
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
