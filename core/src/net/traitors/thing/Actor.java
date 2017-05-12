package net.traitors.thing;

import net.traitors.Layer;
import net.traitors.util.save.Savable;

public interface Actor extends Savable {

    /**
     * Do whatever this thing does
     *
     * @param delta time since last call to act
     */
    void act(float delta);

    Layer getLayer();

    /**
     * Sets this actor's ID
     *
     * @param ID the ID for this actor
     */
    void setID(long ID);

    /**
     * Get the actor's ID
     *
     * @return the ID set in setID, or throw a runtime exception if it wasn't set
     */
    long getID();

}
