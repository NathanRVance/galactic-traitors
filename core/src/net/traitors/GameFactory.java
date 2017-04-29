package net.traitors;

import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

public class GameFactory implements Savable {

    private GameScreen gameScreen;

    @Override
    public SaveData getSaveData() {
        return null;
    }

    @Override
    public void loadSaveData(SaveData saveData) {

    }

    public GameScreen createGameScreen() {
        gameScreen = new GameScreen();
        return gameScreen;
    }
}
