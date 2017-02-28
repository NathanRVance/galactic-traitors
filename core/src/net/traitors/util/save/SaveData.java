package net.traitors.util.save;

import java.util.ArrayList;
import java.util.List;

public class SaveData {

    private StringBuilder stringBuilder = new StringBuilder();

    public SaveData() {

    }

    public SaveData(String saveData) {
        loadData(saveData);
    }

    public void loadData(String savedData) {
        stringBuilder = new StringBuilder(savedData);
    }

    public void writeBoolean(boolean b) {
        stringBuilder.append(b).append(":");
    }

    public boolean readBoolean() {
        int firstColon = stringBuilder.indexOf(":");
        boolean ret = Boolean.parseBoolean(stringBuilder.substring(0, firstColon));
        stringBuilder.delete(0, firstColon + 1);
        return ret;
    }

    public void writeInt(int i) {
        stringBuilder.append(i).append(":");
    }

    public int readInt() {
        int firstColon = stringBuilder.indexOf(":");
        int ret = Integer.parseInt(stringBuilder.substring(0, firstColon));
        stringBuilder.delete(0, firstColon + 1);
        return ret;
    }

    public void writeFloat(float f) {
        stringBuilder.append(f).append(":");
    }

    public float readFloat() {
        int firstColon = stringBuilder.indexOf(":");
        float ret = Float.parseFloat(stringBuilder.substring(0, firstColon));
        stringBuilder.delete(0, firstColon + 1);
        return ret;
    }

    public void writeString(String s) {
        stringBuilder.append(s).append(":");
    }

    public String readString() {
        int firstColon = stringBuilder.indexOf(":");
        String ret = stringBuilder.substring(0, firstColon);
        stringBuilder.delete(0, firstColon + 1);
        return ret;
    }

    public void writeSavable(Savable savable) {
        if(savable == null) {
            writeString("null");
        } else {
            writeString(savable.getClass().toString());
            writeSaveData(savable.getSaveData());
        }
    }

    public Savable readSavable(Savable cachedSavable) {
        String classname = readString();
        if(classname.equals("null")) return null;
        try {
            Class<? extends Savable> c = Class.forName(classname).asSubclass(Savable.class);
            Savable s = (c.isInstance(cachedSavable)) ? cachedSavable : c.newInstance();
            s.loadSaveData(readSaveData());
            return s;
        } catch (Exception e) {
            System.err.println("Error dealing with class " + classname);
            e.printStackTrace();
        }
        return null;
    }

    private Savable flagged = null;

    public Savable getFlaggedSavable() {
        return flagged;
    }

    public <T extends Savable> void writeList(List<T> savables, T flag) {
        writeInt(savables.size());
        for (Savable savable : savables) {
            writeString(savable.getClass().toString());
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
                Class<? extends T> c = Class.forName(readString()).asSubclass(type);
                while (cacheIndex < cachedSavables.size() && !c.isInstance(cachedSavables.get(cacheIndex))) {
                    cachedSavables.remove(cacheIndex);
                }
                T s;
                if (cacheIndex < cachedSavables.size()) {
                    //Got a hit!
                    s = cachedSavables.get(cacheIndex);
                } else {
                    //Out of cached savables :(
                    s = c.newInstance();
                    ret.add(s);
                }
                s.loadSaveData(readSaveData());
                if(readBoolean()) flagged = s;
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
        SaveData ret = new SaveData();
        int unmatched = 1;
        for (int i = 1; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '(') unmatched++;
            if (stringBuilder.charAt(i) == ')') unmatched--;
            if (unmatched == 0) {
                ret.loadData(stringBuilder.substring(1, i));
                stringBuilder.delete(0, i + 1);
                break;
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

}
