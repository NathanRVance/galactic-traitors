package net.traitors.thing.platform;

import net.traitors.thing.Thing;
import net.traitors.util.Point;

public interface Platform extends Thing {


    float getRotationalVelocity();

    void setRotationalVelocity(float velocity);

    Point convertToWorldCoordinates(Point point);

    Point convertToPlatformCoordinates(Point point);

    float convertToWorldRotation(float rotation);

    float convertToPlatformRotation(float rotation);

    /**
     * Applies a force to this platform, which will affect its translational and rotational
     * velocities. Note: forces expire after next call to act!
     *
     * @param force    the force vector applied, in Newtons, in platform coordinates
     * @param position position on this platform that the force is applied, in platform coordinates
     */
    void applyPointForce(Point force, Point position);

}
