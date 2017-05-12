package net.traitors.util.save;

import net.traitors.Layer;
import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.List;

public class SaveData {

    private StringBuilder stringBuilder = new StringBuilder();
    private Layer layer;

    private static final String delimiter = ":";

    public SaveData() {
    }

    public SaveData(String saveData) {
        stringBuilder = new StringBuilder(saveData);
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    private String getToDelimiter() {
        int del = stringBuilder.indexOf(delimiter);
        String ret = stringBuilder.substring(0, del);
        stringBuilder.delete(0, del + 1);
        return ret;
    }

    public void writeBoolean(boolean b) {
        stringBuilder.append(b).append(delimiter);
    }

    public boolean readBoolean() {
        return Boolean.parseBoolean(getToDelimiter());
    }

    public void writeInt(int i) {
        stringBuilder.append(i).append(delimiter);
    }

    public int readInt() {
        return Integer.parseInt(getToDelimiter());
    }

    public void writeLong(long l) {
        stringBuilder.append(l).append(delimiter);
    }

    public long readLong() {
        return Long.parseLong(getToDelimiter());
    }

    public void writeFloat(float f) {
        stringBuilder.append(f).append(delimiter);
    }

    public float readFloat() {
        return Float.parseFloat(getToDelimiter());
    }

    public void writePoint(Point p) {
        if (p == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeFloat(p.x);
            writeFloat(p.y);
        }
    }

    public Point readPoint() {
        if (readBoolean()) {
            return new Point(readFloat(), readFloat());
        } else {
            return null;
        }
    }

    public void writeString(String s) {
        stringBuilder.append(s).append(delimiter);
    }

    public String readString() {
        return getToDelimiter();
    }

    public void writeSavable(Savable savable) {
        if (savable == null) {
            writeString("null");
        } else {
            writeString(Instantiator.getStringType(savable));
            writeSaveData(savable.getSaveData());
        }
    }

    public Savable readSavable() {
        String classname = readString();
        if (classname.equals("null")) return null;
        try {
            Savable s = Instantiator.getInstance(classname, layer);
            s.loadSaveData(readSaveData());
            return s;
        } catch (Exception e) {
            System.err.println("Error dealing with class " + classname);
            e.printStackTrace();
        }
        return null;
    }

    public <T extends Savable> void writeList(List<T> savables) {
        writeInt(savables.size());
        for (Savable savable : savables) {
            writeSavable(savable);
        }
    }

    public List<? extends Savable> readList() {
        int len = readInt();
        List<Savable> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            ret.add(readSavable());
        }
        return ret;
    }

    public void writeSaveData(SaveData sd) {
        stringBuilder.append("(").append(sd.toString()).append(")");
    }

    public SaveData readSaveData() {
        int unmatched = 0;
        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '(') unmatched++;
            if (stringBuilder.charAt(i) == ')') unmatched--;
            if (unmatched == 0) {
                SaveData ret = new SaveData(stringBuilder.substring(1, i));
                stringBuilder.delete(0, i + 1);
                return ret;
            }
        }
        return new SaveData();
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

}
