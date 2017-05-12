package net.traitors.thing;

import net.traitors.Layer;
import net.traitors.util.save.SaveData;

public abstract class AbstractActor implements Actor {

    private Layer layer;
    private long ID = -1;

    public AbstractActor(Layer layer) {
        this.layer = layer;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeLong(ID);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        ID = saveData.readLong();
    }

    @Override
    public Layer getLayer() {
        return layer;
    }

    @Override
    public void setID(long ID) {
        this.ID = ID;
    }

    @Override
    public long getID() {
        if(ID == -1) {
            throw new RuntimeException("ID wasn't set!");
        }
        return ID;
    }
}
