package net.traitors.controls;

import net.traitors.thing.Thing;
import net.traitors.util.Point;

public interface MouseoverCallback extends Thing {

    void mouseEnter();

    void mouseExit();

    /**
     * The mouse has just been clicked while over this thing
     * @param touchLoc location, in layer coordinates, that the mouse went down in
     * @return true to consume event, otherwise false
     */
    boolean mouseDown(Point touchLoc);

    /**
     * Called if this callback previously returned true for mouseDown and mouseUp hasn't happened yet
     * @param touchLoc location, in layer coordinates, that the mouse moved to
     * @return true to consume event, otherwise false
     */
    boolean mouseDragged(Point touchLoc);

    /**
     * The mouse has just been released while over this thing or mouseDown returned true
     * @return true to consume event, otherwise false
     */
    boolean mouseUp();

}
