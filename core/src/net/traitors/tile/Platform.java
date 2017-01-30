package net.traitors.tile;

import net.traitors.util.Point;
import net.traitors.util.Thing;

public interface Platform extends Thing {

    void setTranslationalVelocity(Point velocity);

    Point getTranslationalVelocity();

    void setRotationalVelocity(float velocity);

    float getRotationalVelocity();

    void addThing(Thing thing);

    void removeThing(Thing thing);

    void move(float delta);

}
