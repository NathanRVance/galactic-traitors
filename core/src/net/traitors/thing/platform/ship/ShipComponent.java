package net.traitors.thing.platform.ship;

import net.traitors.thing.tile.Tile;
import net.traitors.thing.usable.Usable;

public interface ShipComponent extends Usable, Tile {

    void setUseCallback(ShipComputer computer);

}
