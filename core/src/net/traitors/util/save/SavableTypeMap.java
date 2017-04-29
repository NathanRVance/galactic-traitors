package net.traitors.util.save;

import net.traitors.controls.Controls;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.item.Gun;
import net.traitors.thing.platform.AbstractPlatform;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.platform.ship.ShipComputer;
import net.traitors.thing.player.Inventory;
import net.traitors.thing.player.Player;
import net.traitors.thing.projectile.Projectile;
import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.GunTile;
import net.traitors.thing.tile.OverviewScreen;
import net.traitors.thing.tile.ThrusterTile;
import net.traitors.thing.tile.thrust.MainThrusterStrategy;
import net.traitors.thing.tile.thrust.RotationalThrusterStrategy;
import net.traitors.util.BetterCamera;
import net.traitors.util.BiMap;

public class SavableTypeMap {

    private static BiMap<String, Class<? extends Savable>> classBiMap;

    static {
        classBiMap = new BiMap<>();
        classBiMap.put("AbstractPlatform", AbstractPlatform.class);
        classBiMap.put("AbstractThing", AbstractThing.class);
        classBiMap.put("BetterCamera", BetterCamera.class);
        classBiMap.put("FloorTile", FloorTile.class);
        classBiMap.put("Gun", Gun.class);
        classBiMap.put("GunTile", GunTile.class);
        classBiMap.put("Inventory", Inventory.class);
        classBiMap.put("MainThrusterStrategy", MainThrusterStrategy.class);
        classBiMap.put("NullPlatform", NullPlatform.class);
        classBiMap.put("OverviewScreen", OverviewScreen.class);
        classBiMap.put("Player", Player.class);
        classBiMap.put("Projectile", Projectile.class);
        classBiMap.put("RotationalThrusterStrategy", RotationalThrusterStrategy.class);
        classBiMap.put("Ship", Ship.class);
        classBiMap.put("ShipComputer", ShipComputer.class);
        classBiMap.put("ThrusterTile", ThrusterTile.class);
        classBiMap.put("UniverseTile", UniverseTile.class);
        classBiMap.put("UserInput", Controls.UserInput.class);
    }


    public static String getStringType(Savable savable) {
        return classBiMap.getReverse(savable.getClass());
    }

    public static Class<? extends Savable> getClass(String type) {
        return classBiMap.get(type);
    }

}
