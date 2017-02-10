package net.traitors.thing.platform;

import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.GunTile;
import net.traitors.thing.tile.OverviewScreen;
import net.traitors.thing.tile.MainThrusterTile;
import net.traitors.thing.tile.RotationalThrusterTile;

public class ShipFactory {

    public ShipFactory() {

    }

    public Ship buildStandardShip() {
        Ship.ShipBuilder shipBuilder = new Ship.ShipBuilder(4, 5);
        shipBuilder.addTile(new OverviewScreen(2, 3), 1, 1);
        shipBuilder.addTile(new GunTile(1, 1, 0, new FloorTile()), 3, 2);
        shipBuilder.addTile(new GunTile(1, 1, (float) Math.PI, new FloorTile()), 0, 2);
        MainThrusterTile t1 = new MainThrusterTile(1, 1, (float) Math.PI * 3 / 2, new FloorTile());
        MainThrusterTile t2 = new MainThrusterTile(1, 1, (float) Math.PI * 3 / 2, new FloorTile());
        t1.lockUseWith(t2);
        shipBuilder.addTile(t1, 1, 0);
        shipBuilder.addTile(t2, 2, 0);

        RotationalThrusterTile rtt = new RotationalThrusterTile(0, new FloorTile());
        shipBuilder.addTile(rtt, 3, 4);
        RotationalThrusterTile rtt2 = new RotationalThrusterTile((float) Math.PI / 2, new FloorTile());
        shipBuilder.addTile(rtt2, 0, 4);
        rtt.secretSpecialTestStuff(rtt2);
        shipBuilder.addTile(new RotationalThrusterTile((float) Math.PI, new FloorTile()), 0, 0);
        shipBuilder.addTile(new RotationalThrusterTile((float) Math.PI * 3 / 2, new FloorTile()), 3, 0);

        return shipBuilder.getShip();
    }

    public Ship buildTileGrid(int width, int height) {
        return new Ship.ShipBuilder(width, height).getShip();
    }

}
