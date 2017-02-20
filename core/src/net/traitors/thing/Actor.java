package net.traitors.thing;

import java.io.Serializable;

public interface Actor extends Serializable {

    /**
     * Do whatever this thing does
     *
     * @param delta time since last call to act
     */
    void act(float delta);

}
