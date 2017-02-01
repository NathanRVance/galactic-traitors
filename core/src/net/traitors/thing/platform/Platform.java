package net.traitors.thing.platform;

import net.traitors.util.Point;
import net.traitors.thing.Thing;

public interface Platform extends Thing {

    void setTranslationalVelocity(Point velocity);

    Point getTranslationalVelocity();

    void setRotationalVelocity(float velocity);

    float getRotationalVelocity();

    Point convertToWorldCoordinates(Point point);

    Point convertToPlatformCoordinates(Point point);

    float convertToWorldRotation(float rotation);

    float convertToPlatformRotation(float rotation);

}
