package net.traitors.util.save;

import java.util.ArrayList;
import java.util.List;

public class SaveData {

    private StringBuilder stringBuilder = new StringBuilder();
    private Savable flagged = null;

    private static final String delimiter = ":";

    public SaveData() {

    }

    public SaveData(String saveData) {
        stringBuilder = new StringBuilder(saveData);
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
            writeString(SavableTypeMap.getStringType(savable));
            writeSaveData(savable.getSaveData());
        }
    }

    public Savable readSavable(Savable cachedSavable) {
        String classname = readString();
        if (classname.equals("null")) return null;
        try {
            Class<? extends Savable> c = SavableTypeMap.getClass(classname);
            Savable s = (c.isInstance(cachedSavable)) ? cachedSavable : c.newInstance();
            s.loadSaveData(readSaveData());
            return s;
        } catch (Exception e) {
            System.err.println("Error dealing with class " + classname);
            e.printStackTrace();
        }
        return null;
    }

    public Savable getFlaggedSavable() {
        return flagged;
    }

    public <T extends Savable> void writeList(List<T> savables, T flag) {
        writeInt(savables.size());
        for (Savable savable : savables) {
            writeSavable(savable);
            writeBoolean(savable == flag); //Same instance
        }
    }

    /**
     * Performs an in-place restoration on cachedSavables, loading data into cache hits and
     * removing elements that don't belong. Any entries that are missing are returned. If an
     * element is flagged, it will be available at the next call to getFlaggedSavable
     *
     * @param cachedSavables list of Savables that can be restored without extra instansiation.
     * @return a list of Savables that were missing from cachedSavables
     */
    public <T extends Savable> List<? extends T> readList(List<T> cachedSavables, Class<T> type) {
        int len = readInt();
        List<T> ret = new ArrayList<>(len);
        int cacheIndex = 0;
        for (int i = 0; i < len; i++) {
            try {
                Class<? extends T> c = SavableTypeMap.getClass(readString()).asSubclass(type);
                while (cacheIndex < cachedSavables.size() && !c.isInstance(cachedSavables.get(cacheIndex))) {
                    cachedSavables.remove(cacheIndex);
                }
                T s;
                if (cacheIndex < cachedSavables.size()) {
                    //Got a hit!
                    s = cachedSavables.get(cacheIndex);
                    cachedSavables.remove(cacheIndex);
                } else {
                    //Out of cached savables :(
                    s = c.newInstance();
                    ret.add(s);
                }
                s.loadSaveData(readSaveData());
                if (readBoolean()) flagged = s;
            } catch (Exception e) {
                e.printStackTrace();
            }
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
