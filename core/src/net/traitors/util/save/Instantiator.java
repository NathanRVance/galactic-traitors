package net.traitors.util.save;

import net.traitors.Layer;
import net.traitors.controls.Controls;
import net.traitors.thing.item.Gun;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.player.Player;
import net.traitors.thing.projectile.Projectile;
import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.GunTile;
import net.traitors.thing.tile.OverviewScreen;
import net.traitors.thing.tile.ThrusterTile;
import net.traitors.thing.tile.thrust.MainThrusterStrategy;
import net.traitors.thing.tile.thrust.RotationalThrusterStrategy;

import java.util.HashMap;
import java.util.Map;

class Instantiator {

    private static Map<Class<? extends Savable>, SavableType> classMap;

    private enum SavableType {
        FloorTile,
        Gun,
        GunTile,
        MainThrusterStrategy,
        NullPlatform,
        OverviewScreen,
        Player,
        Projectile,
        RotationalThrusterStrategy,
        Ship,
        ThrusterTile,
        UniverseTile,
        UserInput
    }

    static {
        classMap = new HashMap<>();
        classMap.put(FloorTile.class, SavableType.FloorTile);
        classMap.put(Gun.class, SavableType.Gun);
        classMap.put(GunTile.class, SavableType.GunTile);
        classMap.put(MainThrusterStrategy.class, SavableType.MainThrusterStrategy);
        classMap.put(NullPlatform.class, SavableType.NullPlatform);
        classMap.put(OverviewScreen.class, SavableType.OverviewScreen);
        classMap.put(Player.class, SavableType.Player);
        classMap.put(Projectile.class, SavableType.Projectile);
        classMap.put(RotationalThrusterStrategy.class, SavableType.RotationalThrusterStrategy);
        classMap.put(Ship.class, SavableType.Ship);
        classMap.put(ThrusterTile.class, SavableType.ThrusterTile);
        classMap.put(UniverseTile.class, SavableType.UniverseTile);
        classMap.put(Controls.UserInput.class, SavableType.UserInput);
    }


    static String getStringType(Savable savable) {
        return classMap.get(savable.getClass()).name();
    }

    static Savable getInstance(String type, Layer layer) {
        switch (SavableType.valueOf(type)) {
            case FloorTile:
                return new FloorTile(layer);
            case Gun:
                return new Gun(layer, 0, 0);
            case GunTile:
                return new GunTile(0, 0, 0, layer, null);
            case MainThrusterStrategy:
                return new MainThrusterStrategy(null, 0);
            case NullPlatform:
                return new NullPlatform();
            case OverviewScreen:
                return new OverviewScreen(layer, 0, 0);
            case Player:
                return new Player(layer);
            case Projectile:
                return new Projectile(layer, 0, 0, null, null, null, 0);
            case RotationalThrusterStrategy:
                return new RotationalThrusterStrategy(null, 0);
            case Ship:
                return new Ship(layer, 0, 0);
            case ThrusterTile:
                return new ThrusterTile(layer, 0, 0, 0, null);
            case UniverseTile:
                return new UniverseTile(layer);
            case UserInput:
                return new Controls.UserInput(-1);
            default:
                throw new RuntimeException("Punch the programmer. He done messed up.");
        }
    }

}
