package net.traitors.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.Actor;
import net.traitors.thing.Thing;

public class BetterCamera extends OrthographicCamera implements Actor {

    private Thing rotateWith = null;
    private float offset = 0;

    /**
     * Rotate the camera to face a given direction
     *
     * @param direction angle in radians
     */
    private void rotateTo(float direction) {
        rotate((getCameraAngle() - direction) * MathUtils.radiansToDegrees);
    }

    /**
     * The direction the camera is currently facing
     *
     * @return angle in radians
     */
    public float getCameraAngle() {
        return -1 * (float) (Math.atan2(up.x, up.y));
    }

    public void rotateWith(Thing thing) {
        if (thing != rotateWith) {
            //We're rotating with a new thing
            rotateWith = thing;
            offset = getCameraAngle() - thing.getWorldRotation();
        }
    }

    public Thing getRotatingWith() {
        return rotateWith;
    }

    /**
     * Gets the offset from the "north" of the thing this platform is rotating with
     *
     * @return offset in radians
     */
    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    /**
     * Face the "north" direction of the thing we're rotating with
     */
    public void syncRotations() {
        offset = 0;
        zoom = 1;
    }

    /**
     * Rotate with the thing set to rotate with. This should be called after the act call to thing
     * so that the camera doesn't lag behind.
     *
     * @param delta time in seconds since last call to act
     */
    @Override
    public void act(float delta) {
        if (rotateWith != null) {
            rotateTo(rotateWith.getWorldRotation() + offset);
        }
    }

}
