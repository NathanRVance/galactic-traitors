package net.traitors.thing.usable;

import net.traitors.thing.Thing;
import net.traitors.util.Point;

public interface Usable {

    /**
     * Cause this usable to do whatever it does
     *
     * @param user  the thing that is using this usable
     */
    void use(Thing user, Point touchPoint);

    /**
     * Gets the progress this item has to cooling off, where 1 means it's ready to fire.
     *
     * @return cooldown percent, ranging from 0 to 1
     */
    float getCooldownPercent();

}
