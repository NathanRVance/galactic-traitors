package net.traitors.thing.platform;

import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;

public abstract class AbstractPlatform extends AbstractThing implements Platform {

    private static final long serialVersionUID = 1867652038890912824L;
    private Point translationalVelocity = new Point();
    private float rotationalVelocity = 0f;

    AbstractPlatform() {
        super(0, 0, Float.MAX_VALUE);
    }

    public AbstractPlatform(float width, float height) {
        //By default, assume 1 meter thick and a mass of 1000 kg / m^3
        super(width, height, width * height * 1000);
    }

    public AbstractPlatform(float width, float height, float mass) {
        super(width, height, mass);
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
        setPoint(getPoint().add(getTranslationalVelocity().scale(delta)));
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

    @Override
    public void applyForce(Point force, Point radius, float delta) {
        // radius X force = torque
        //   [x1,        y1,        z1]
        // X [x2,        y2,        z2]
        // = [y1z2-z1y2, z1x2-x1z2, x1y2-y1x2]
        // We only care about the z component of the cross product for torque
        float torque = radius.x * force.y - radius.y * force.x;
        // torque = moment of inertia * angular acceleration
        // moment of inertia for thin rectangular plate = m * (h^2 + w^2) / 12
        float I = getMass() * (getHeight() * getHeight() + getWidth() * getWidth()) / 12;
        float angAccel = torque / I;
        setRotationalVelocity(getRotationalVelocity() + angAccel * delta);

        // a = f / m
        Point a = force.scale(1 / getMass()).rotate(getWorldRotation());
        setTranslationalVelocity(getTranslationalVelocity().add(a.scale(delta)));
    }
}
