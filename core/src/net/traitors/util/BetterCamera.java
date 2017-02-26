package net.traitors.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.util.save.SaveData;

public class BetterCamera extends OrthographicCamera implements Actor {

    private int rotateDepth;
    private float offset = 0;
    private Thing cachedThing = null;
    private Platform nullPlatform = new NullPlatform();

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeInt(rotateDepth);
        sd.writeFloat(offset);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        rotateDepth = saveData.readInt();
        offset = saveData.readFloat();
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

    public void setRotateDepth(int depth) {
        rotateDepth = depth;
    }

    public int getRotateDepth() {
        return rotateDepth;
    }

    public Thing getRotatingWith() {
        return getThingAtDepth(rotateDepth);
    }

    public Thing getThingAtDepth(int depth) {
        Thing thing = GameScreen.getStuff().getPlayer();
        for(int i = 0; i < depth; i++) {
            thing = thing.getPlatform();
            if(thing == null) return nullPlatform;
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
        if (cachedThing != getRotatingWith()) {
            //We're rotating with a new thing
            cachedThing = getRotatingWith();
            offset = getCameraAngle() - getRotatingWith().getWorldRotation();
        }
        rotateTo(getRotatingWith().getWorldRotation() + offset);
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
