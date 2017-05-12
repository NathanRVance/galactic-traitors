package net.traitors.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.Layer;
import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.util.save.SaveData;

public class BetterCamera extends OrthographicCamera implements Actor {

    private int rotateDepth;
    private float offset = 0;
    private Thing previousRotatingWith = null;
    private Thing tracking = new NullPlatform();
    private long ID = -1;

    @Override
    public SaveData getSaveData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the thing this camera follows. Must be done every time saved data is loaded.
     * @param tracking the thing to be tracked
     */
    public void setTracking(Thing tracking) {
        this.tracking = tracking;
    }

    public Thing getTracking() {
        return tracking;
    }

    /**
     * Rotate the camera to face a given direction
     *
     * @param direction angle in radians
     */
    public void rotateTo(float direction) {
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

    public int getRotateDepth() {
        return rotateDepth;
    }

    public void setRotateDepth(int depth) {
        rotateDepth = depth;
    }

    public Thing getRotatingWith() {
        return getThingAtDepth(rotateDepth);
    }

    public Thing getThingAtDepth(int depth) {
        Thing thing = tracking;
        for (int i = 0; i < depth; i++) {
            thing = thing.getPlatform();
        }
        return thing;
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
        setOffset(0);
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
        //Translate
        Point p = tracking.getWorldPoint();
        translate(p.x - position.x, p.y - position.y);
        if (getRotatingWith() instanceof NullPlatform) setRotateDepth(1);
        update();

        //Rotate
        if (previousRotatingWith != getRotatingWith()) {
            //We're rotating with a new thing
            previousRotatingWith = getRotatingWith();
            setOffset(getCameraAngle() - previousRotatingWith.getWorldRotation());
        }
        rotateTo(previousRotatingWith.getWorldRotation() + getOffset());
    }

    @Override
    public Layer getLayer() {
        return getRotatingWith().getLayer();
    }

    @Override
    public void setID(long ID) {
        this.ID = ID;
    }

    @Override
    public long getID() {
        if(ID == -1) {
            throw new RuntimeException("ID wasn't set!");
        }
        return ID;
    }


    /**
     * Determines if any portion of this thing falls withing the camera.
     *
     * @param thing   the thing this camera may be seeing
     * @param padding amount of wiggle room around the thing's bounds we allow while still "seeing" it
     * @return whether or not the thing is seen
     */
    public boolean isWatching(Thing thing, float padding) {
        float offset = thing.getWorldPoint().subtract(new Point(position.x, position.y)).distanceFromZero();
        float extent = new Point(thing.getWidth(), thing.getHeight()).distanceFromZero() + padding;
        float viewWidth = new Point(viewportWidth * zoom, viewportHeight * zoom).distanceFromZero() / 2;
        return offset - extent < viewWidth;
    }
}
