package net.traitors.controls;

import net.traitors.thing.Thing;

public interface MouseoverCallback extends Thing {

    void mouseEnter();

    void mouseExit();

    /**
     * The mouse has just been clicked while over this thing
     * @return true to consume event, otherwise false
     */
    boolean mouseDown();

    /**
     * The mouse has just been released while over this thing
     * @return true to consume event, otherwise false
     */
    boolean mouseUp();

}
