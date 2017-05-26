package net.traitors.thing.platform;

import net.traitors.Layer;
import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public abstract class AbstractPlatform extends AbstractThing implements Platform {

    private float rotationalAcceleration = 0f;
    private float rotationalVelocity = 0f;

    public AbstractPlatform(Layer layer, float width, float height) {
        //By default, assume 1 meter thick and a mass of 1000 kg / m^3
        super(layer, width, height, width * height * 1000);
    }

    public AbstractPlatform(Layer layer, float width, float height, float mass) {
        super(layer, width, height, mass);
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        //Save rotational velocity
        sd.writeFloat(rotationalVelocity);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        //Load rotational velocity
        rotationalVelocity = saveData.readFloat();
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
        setRotationalVelocity(getRotationalVelocity() + rotationalAcceleration * delta);
        rotationalAcceleration = 0f;
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
    public void applyPointForce(Point force, Point radius) {
        //Rotational component
        rotationalAcceleration += force.angAccel(radius, this);
        //Translational component
        applyForce(force);
    }
}
