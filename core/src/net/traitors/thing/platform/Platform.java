package net.traitors.thing.platform;

import net.traitors.thing.Thing;
import net.traitors.util.Point;

public interface Platform extends Thing {

    Point getTranslationalVelocity();

    void setTranslationalVelocity(Point velocity);

    float getRotationalVelocity();

    void setRotationalVelocity(float velocity);

    Point convertToWorldCoordinates(Point point);

    Point convertToPlatformCoordinates(Point point);

    float convertToWorldRotation(float rotation);

    float convertToPlatformRotation(float rotation);

    /**
     * Applies a force to this platform, which will affect its translational and rotational
     * velocities.
     *
     * @param force    the force vector applied, in Newtons, in platform coordinates
     * @param position position on this platform that the force is applied, in platform coordinates
     * @param delta    time for which this force is applied
     */
    void applyForce(Point force, Point position, float delta);

}
