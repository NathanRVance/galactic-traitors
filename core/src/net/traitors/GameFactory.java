package net.traitors;

import com.badlogic.gdx.graphics.Color;

import net.traitors.thing.platform.Platform;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.platform.ship.ShipFactory;
import net.traitors.thing.player.Player;
import net.traitors.ui.ScreenElements.CompassBar;
import net.traitors.ui.ScreenElements.InventoryBar;
import net.traitors.ui.ScreenElements.Touchpad;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

public class GameFactory implements Savable {

    private static InventoryBar inventoryBar;
    private Layer worldLayer;
    private static Layer screenLayer;
    private GameScreen gameScreen;
    private long ID; //Save ID

    public GameFactory() {
        //Screen layer
        screenLayer = new LayerLayer(new BetterCamera());
        screenLayer.getDefaultCamera().setToOrtho(false, 5, 5);
        inventoryBar = new InventoryBar(screenLayer, 5, screenLayer.getHeight());
        //Point is set in InventoryBar draw method
        screenLayer.addActor(inventoryBar);
        Touchpad touchpad = new Touchpad(screenLayer, screenLayer.getHeight() / 5);
        touchpad.setPoint(screenLayer.getBotCorner().add(new Point(touchpad.getWidth() / 2, touchpad.getHeight() / 2)));
        screenLayer.addActor(touchpad);
        CompassBar compassBar = new CompassBar(screenLayer, touchpad.getHeight() * 2 / 3);
        compassBar.setPoint(screenLayer.getBotCorner().add(new Point(compassBar.getWidth() / 2, screenLayer.getHeight() - compassBar.getHeight() / 2)));
        screenLayer.addActor(compassBar);

        //World layer
        worldLayer = new LayerLayer(GalacticTraitors.getCamera());
        Ship ship = new ShipFactory().buildStandardShip(worldLayer);
        ship.setPoint(new Point(-5, 10));
        worldLayer.addActor(ship);

        Platform world = new UniverseTile(worldLayer);
        world.setPoint(new Point(0, 0));
        worldLayer.addActor(world);

        ship = new ShipFactory().buildTileGrid(worldLayer, 50, 2);
        ship.setPoint(new Point(0, -20));
        //ship.setRotationalVelocity(-.3f);
        worldLayer.addActor(ship);

        ship = new ShipFactory().buildTileGrid(worldLayer, 4, 3);
        ship.setPoint(new Point(1, 2));
        ship.setRotationalVelocity(-1);
        worldLayer.addActor(ship);

        ship = new ShipFactory().buildTileGrid(worldLayer, 3, 3);
        ship.setPoint(new Point(1, 1));
        ship.setRotationalVelocity(-1);
        worldLayer.addActor(ship);

        ship = new ShipFactory().buildTileGrid(worldLayer, 1, 1);
        ship.setPoint(new Point(1, 2));
        ship.setRotationalVelocity(1);
        worldLayer.addActor(ship);

        //Game screen
        gameScreen = new GameScreen(this);
        gameScreen.addLayer(worldLayer);
        gameScreen.addLayer(screenLayer);
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeLong(ID++);
        sd.writeSaveData(worldLayer.getSaveData());
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        if (saveData == null) return;
        try {
            long id = saveData.readLong();
            if (id <= ID) return; //Sometimes updates arrive out of order.
            ID = id;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        worldLayer.loadSaveData(saveData.readSaveData());
    }

    public static InventoryBar getInventoryBar() {
        return inventoryBar;
    }

    public static Layer getScreenLayer() {
        return screenLayer;
    }

    Player makePlayer(boolean isMainPlayer) {
        InventoryBar bar = (isMainPlayer) ? inventoryBar : null;
        Player p = new Player(worldLayer, Color.GREEN, new Color(0xdd8f4fff), Color.BROWN, Color.BLUE, Color.BLACK, bar);
        worldLayer.addActor(p);
        return p;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
