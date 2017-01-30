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
    public void setTranslationalVelocity(Point velocity) {
        translationalVelocity = velocity;
    }

    @Override
    public Point getTranslationalVelocity() {
        return translationalVelocity;
    }

    @Override
    public void setRotationalVelocity(float velocity) {
        rotationalVelocity = velocity;
    }

    @Override
    public float getRotationalVelocity() {
        return rotationalVelocity;
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
        position = new Point(position.x + translationalVelocity.x * delta, position.y + translationalVelocity.y * delta);
        rotation = (rotation + rotationalVelocity * delta) % (float) (Math.PI * 2);

        //Move stuff
        for (Thing thing : stuff) {
            Point thingp = thing.getPoint();
            //Translation
            thingp = new Point(thingp.x + translationalVelocity.x * delta,
                    thingp.y + translationalVelocity.y * delta);
            //Rotation
            float d = thingp.distance(position);
            float angle = (float) Math.asin(thingp.y / d);
            angle += rotationalVelocity * delta;
            thingp = new Point((float) Math.cos(angle) * d, (float) Math.sin(angle) * d);

            thing.setPoint(thingp);
        }
    }

    @Override
    public Point getPoint() {
        return position;
    }

    @Override
    public void setPoint(Point point) {
        this.position = point;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
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
