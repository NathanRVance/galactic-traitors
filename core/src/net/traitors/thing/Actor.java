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

}
