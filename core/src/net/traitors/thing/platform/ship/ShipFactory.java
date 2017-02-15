package net.traitors.thing.platform.ship;

import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.GunTile;
import net.traitors.thing.tile.thrust.MainThrusterStrategy;
import net.traitors.thing.tile.OverviewScreen;
import net.traitors.thing.tile.ThrusterTile;
import net.traitors.thing.tile.thrust.RotationalThrusterStrategy;

public class ShipFactory {

    public ShipFactory() {

    }

    public Ship buildStandardShip() {
        Ship.ShipBuilder shipBuilder = new Ship.ShipBuilder(4, 5);
        shipBuilder.addTile(new OverviewScreen(2, 3), 1, 1);
        shipBuilder.addTile(new GunTile(1, 1, 0, new FloorTile()), 3, 2);
        shipBuilder.addTile(new GunTile(1, 1, (float) Math.PI, new FloorTile()), 0, 2);

        ShipComputer computer = new ShipComputer();
        shipBuilder.setComputer(computer);

        ThrusterTile t1 = new ThrusterTile(1, 1, (float) Math.PI * 3 / 2,
                new MainThrusterStrategy(new FloorTile(), 50000));
        ThrusterTile t2 = new ThrusterTile(1, 1, (float) Math.PI * 3 / 2,
                new MainThrusterStrategy(new FloorTile(), 50000));
        computer.syncUsages(t1, t2);
        shipBuilder.addTile(t1, 1, 0);
        shipBuilder.addTile(t2, 2, 0);


        shipBuilder.addTile(new ThrusterTile(1, 1, 0,
                new RotationalThrusterStrategy(new FloorTile(), 5000)), 3, 4);
        shipBuilder.addTile(new ThrusterTile(1, 1, (float) Math.PI / 2,
                new RotationalThrusterStrategy(new FloorTile(), 5000)), 0, 4);
        shipBuilder.addTile(new ThrusterTile(1, 1, (float) Math.PI,
                new RotationalThrusterStrategy(new FloorTile(), 5000)), 0, 0);
        shipBuilder.addTile(new ThrusterTile(1, 1, (float) Math.PI * 3 / 2,
                new RotationalThrusterStrategy(new FloorTile(), 5000)), 3, 0);

        return shipBuilder.getShip();
    }

    public Ship buildTileGrid(int width, int height) {
        return new Ship.ShipBuilder(width, height).getShip();
    }

}
