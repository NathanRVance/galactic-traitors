package net.traitors.util.save;

public interface Savable {

    SaveData getSaveData();

    void loadSaveData(SaveData saveData);

}
