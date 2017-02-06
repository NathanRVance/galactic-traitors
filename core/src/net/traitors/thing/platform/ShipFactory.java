package net.traitors.thing.platform;

import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.GunTile;
import net.traitors.thing.tile.OverviewScreen;
import net.traitors.thing.tile.ThrusterTile;

public class ShipFactory {

    public ShipFactory() {

    }

    public Ship buildStandardShip() {
        Ship.ShipBuilder shipBuilder = new Ship.ShipBuilder(4, 5);
        shipBuilder.addTile(new OverviewScreen(2, 3), 1, 1);
        shipBuilder.addTile(new GunTile(1, 1, 0, new FloorTile()), 3, 2);
        shipBuilder.addTile(new GunTile(1, 1, (float) Math.PI, new FloorTile()), 0, 2);
        shipBuilder.addTile(new ThrusterTile(1, 1, (float) Math.PI * 3 / 2, new FloorTile()), 1, 0);
        return shipBuilder.getShip();
    }

}
