package net.traitors.thing.platform;

import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;

abstract class AbstractPlatform extends AbstractThing implements Platform {

    private Point translationalVelocity = new Point();
    private float rotationalVelocity = 0f;

    AbstractPlatform() {
        super(0, 0);
    }

    AbstractPlatform(int width, int height) {
        super(width, height);
    }

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
    public void act(float delta) {
        super.act(delta);
        setPoint(getPoint().add(translationalVelocity.scale(delta)));
        setRotation((getRotation() + rotationalVelocity * delta) % (float) (Math.PI * 2));
    }

    @Override
    public Point convertToWorldCoordinates(Point point) {
        //First convert to the coordinates of the platform one level up (if there is one)
        //Resolve rotation induced differences
        if (!point.isZero()) {
            point = point.rotate(getRotation());
        }
        //And translation
        point = point.add(getPoint());

        //Then, convert from that platform's coordinates
        return getPlatform().convertToWorldCoordinates(point);
    }

    @Override
    public Point convertToPlatformCoordinates(Point point) {
        //First, convert from world coordinates to the platform one level up
        point = getPlatform().convertToPlatformCoordinates(point);

        //Then, convert to our coordinates
        //Resolve translation
        point = point.subtract(getPoint());
        //And rotation
        if (!point.isZero()) {
            point = point.rotate(-1 * getRotation());
        }

        return point;
    }

    @Override
    public float convertToWorldRotation(float rotation) {
        rotation = (float) ((rotation + getRotation() + Math.PI * 2) % (Math.PI * 2));
        return getPlatform().convertToWorldRotation(rotation);
    }

    @Override
    public float convertToPlatformRotation(float rotation) {
        rotation = getPlatform().convertToPlatformRotation(rotation);
        return (float) ((rotation - getRotation() + Math.PI * 2) % (float) (Math.PI * 2));
    }
}