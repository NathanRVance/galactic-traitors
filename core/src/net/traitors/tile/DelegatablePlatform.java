package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.Point;
import net.traitors.util.Thing;

import java.util.HashSet;
import java.util.Set;

public class DelegatablePlatform implements Platform {

    private Point translationalVelocity = new Point();
    private float rotationalVelocity = 0f;

    private Set<Thing> stuff = new HashSet<Thing>();

    private Point position = new Point();
    private float rotation = 0f;

    @Override
    public Point getTranslationalVelocity() {
        return translationalVelocity;
    }

    @Override
    public void setTranslationalVelocity(Point velocity) {
        translationalVelocity = velocity;
    }

    @Override
    public float getRotationalVelocity() {
        return rotationalVelocity;
    }

    @Override
    public void setRotationalVelocity(float velocity) {
        rotationalVelocity = velocity;
    }

    @Override
    public void addThing(Thing thing) {
        stuff.add(thing);
    }

    @Override
    public void removeThing(Thing thing) {
        stuff.remove(thing);
    }

    @Override
    public void move(float delta) {
        //These methods handle moving the things in stuff
        setPoint(new Point(position.x + translationalVelocity.x * delta, position.y + translationalVelocity.y * delta));
        setRotation((rotation + rotationalVelocity * delta) % (float) (Math.PI * 2));
    }

    @Override
    public Point getPoint() {
        return position;
    }

    @Override
    public void setPoint(Point point) {
        Point change = new Point(point.x - position.x, point.y - position.y);
        this.position = point;
        for (Thing thing : stuff) {
            thing.setPoint(new Point(thing.getPoint().x + change.x, thing.getPoint().y + change.y));
        }
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        float change = rotation - this.rotation;
        this.rotation = rotation;
        for (Thing thing : stuff) {
            Point thingp = thing.getPoint();
            if(thingp.equals(position)) continue;
            float d = thingp.distance(position);
            float angle = (float) Math.asin((thingp.y - position.y) / d);
            if(thingp.x < position.x) angle = (float) Math.PI - angle;
            angle += change;
            thingp = new Point((float) Math.cos(angle) * d + position.x, (float) Math.sin(angle) * d + position.y);

            thing.setPoint(thingp);
            thing.setRotation(thing.getRotation() + change);
        }
    }

    //We don't care about methods below here. The class that delegates to this class should handle them.

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void draw(Batch batch) {

    }
}
