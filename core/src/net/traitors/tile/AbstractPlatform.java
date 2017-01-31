package net.traitors.tile;

import net.traitors.util.Point;

public abstract class AbstractPlatform implements Platform {

    private Point translationalVelocity = new Point();
    private float rotationalVelocity = 0f;

    private Point point = new Point();
    private float rotation = 0f;

    private Platform platform = null;

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
        setPoint(new Point(getPoint().x + translationalVelocity.x * delta, getPoint().y + translationalVelocity.y * delta));
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
        if (platform != null) {
            return platform.convertToWorldCoordinates(point);
        } else {
            return point;
        }
    }

    @Override
    public Point convertToPlatformCoordinates(Point point) {
        //First, convert from world coordinates to the platform one level up (if there is one)
        if (platform != null) {
            point = platform.convertToPlatformCoordinates(point);
        }

        //Then, convert to our coordinates
        //Resolve translation
        point = point.subtract(getPoint());
        //And rotation
        if (!point.isZero()) {
            point = point.rotate(-getRotation());
        }

        return point;
    }

    @Override
    public float convertToWorldRotation(float rotation) {
        rotation = (rotation + getRotation()) % (float) (Math.PI * 2);
        if (platform != null) {
            return platform.convertToWorldRotation(rotation);
        } else {
            return rotation;
        }
    }

    @Override
    public float convertToPlatformRotation(float rotation) {
        if (platform != null) {
            rotation = platform.convertToPlatformRotation(rotation);
        }
        return (rotation - getRotation()) % (float) (Math.PI * 2);
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public Point getWorldPoint() {
        return (platform == null) ? getPoint() : platform.convertToWorldCoordinates(getPoint());
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public float getWorldRotation() {
        return (platform == null) ? getRotation() : platform.convertToWorldRotation(getRotation());
    }

    @Override
    public void setPlatform(Platform platform) {
        setPoint(getWorldPoint());
        setRotation(getWorldRotation());
        if (platform != null) {
            setPoint(platform.convertToPlatformCoordinates(getPoint()));
            setRotation(platform.convertToPlatformRotation(rotation));
        }
        this.platform = platform;
    }
}
