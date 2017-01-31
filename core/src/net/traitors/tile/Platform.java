package net.traitors.tile;

import net.traitors.util.Point;
import net.traitors.util.Thing;

public interface Platform extends Thing {

    void setTranslationalVelocity(Point velocity);

    Point getTranslationalVelocity();

    void setRotationalVelocity(float velocity);

    float getRotationalVelocity();

    Point convertToWorldCoordinates(Point point);

    Point convertToPlatformCoordinates(Point point);

    float convertToWorldRotation(float rotation);

}
