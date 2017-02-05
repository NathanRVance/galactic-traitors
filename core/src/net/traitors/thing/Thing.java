package net.traitors.thing;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.platform.Platform;
import net.traitors.util.Point;

public interface Thing extends Actor {

    /**
     * Get the center of mass of this thing relative to the platform it is on
     *
     * @return center of mass
     */
    Point getPoint();

    /**
     * Set the center of mass of this thing relative to the platform it is on
     *
     * @param point center of mass
     */
    void setPoint(Point point);

    /**
     * Get the center of mass of this thing relative to world coordinates
     *
     * @return center of mass
     */
    Point getWorldPoint();

    /**
     * Set the center of mass of this thing relative to world coordinates
     *
     * @param point center of mass
     */
    void setWorldPoint(Point point);

    /**
     * Get the current rotation of this thing relative to the platform it is on
     *
     * @return rotation in radians
     */
    float getRotation();

    /**
     * Set the current rotation of this thing relative to the platform it is on
     *
     * @param rotation rotation in radians
     */
    void setRotation(float rotation);

    /**
     * Get the current rotation of this thing relative to the world
     *
     * @return rotation in radians
     */
    float getWorldRotation();

    /**
     * Set the current rotation of this thing relative to the world
     *
     * @param rotation rotation in radians
     */
    void setWorldRotation(float rotation);

    /**
     * Gets the current world velocity
     *
     * @return the velocity we are moving at in the world
     */
    Point getWorldVelocity();

    float getWidth();

    float getHeight();

    Platform getPlatform();

    /**
     * Register this thing with a platform that will allow keeping track of points and rotations
     * relative to the platform
     *
     * @param platform the platform this thing is on
     */
    void setPlatform(Platform platform);

    /**
     * Draw this thing using its point, rotation, width, height, and platform
     *
     * @param batch batch used to draw this thing
     */
    void draw(Batch batch);

    /**
     * Checks if the point is contained within this thing
     *
     * @param point point to consider, in world coordinates
     * @return whether or not the point falls within this thing
     */
    boolean contains(Point point);

}
