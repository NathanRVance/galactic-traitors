package net.traitors;

import net.traitors.thing.platform.Platform;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.platform.ship.ShipFactory;
import net.traitors.util.Point;

public class NewGame {

    public static Layer getWorldLayer() {
        Layer worldLayer = new LayerLayer(GalacticTraitors.getCamera());
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

        //worldLayer.getPlayer().setPoint(new Point(-4, 10));
        worldLayer.addActor(GalacticTraitors.getCamera());
        return worldLayer;
    }

    public static ScreenLayer getScreenLayer() {
        return new ScreenLayer();
    }

}
