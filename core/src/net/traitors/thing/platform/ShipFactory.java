package net.traitors.thing.platform;

import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.GunTile;
import net.traitors.thing.tile.OverviewScreen;

public class ShipFactory {

    public ShipFactory() {

    }

    public Ship buildStandardShip() {
        Ship.ShipBuilder shipBuilder = new Ship.ShipBuilder(4, 8);
        shipBuilder.addTile(new OverviewScreen(2, 2), 1, 1);
        shipBuilder.addTile(new GunTile(1, 1, 0, new FloorTile()), 3, 2);
        return shipBuilder.getShip();
    }

}
