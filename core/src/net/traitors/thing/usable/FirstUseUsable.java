package net.traitors.thing.usable;

import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public abstract class FirstUseUsable implements Usable, Actor {

    private boolean used = false;
    private boolean repeatUse = false;

    @Override
    public void use(Thing user, Point touchPoint) {
        repeatUse = true;
        if (!used) {
            firstUse(user, touchPoint);
        }
    }

    @Override
    public void act(float delta) {
        if (!repeatUse)
            used = false;
        repeatUse = false;
    }

    /**
     * Works like use method, but only called after use hadn't been called for at least one act cycle.
     * Requires use and act to be called.
     *
     * @param user       the user that was passed into use
     * @param touchPoint the point that was passed into use
     */
    protected abstract void firstUse(Thing user, Point touchPoint);

    @Override
    public float getCooldownPercent() {
        return 0;
    }

    @Override
    public SaveData getSaveData() {
        return null;
    }

    @Override
    public void loadSaveData(SaveData saveData) {

    }
}
