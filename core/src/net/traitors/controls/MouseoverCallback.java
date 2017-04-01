package net.traitors.controls;

import net.traitors.thing.Thing;

public interface MouseoverCallback extends Thing {

    void mouseEnter();

    void mouseExit();

    void mouseDown();

    void mouseUp();

}
