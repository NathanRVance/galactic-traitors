package net.traitors.thing;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.util.Point;

public abstract class AbstractThing implements Thing {

    private final float width;
    private final float height;
    private Point point = new Point();
    private Point lastWorldPoint = new Point();
    private Point velocity = new Point();
    private float rotation = 0;
    private Platform platform;
    private Platform nullPlatform = new NullPlatform();

    public AbstractThing(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Point getPoint() {
        return point.duplicate();
    }

    @Override
    public void setPoint(Point point) {
        this.point = point.duplicate();
    }

    @Override
    public Point getWorldPoint() {
        return getPlatform().convertToWorldCoordinates(getPoint());
    }

    @Override
    public void setWorldPoint(Point point) {
        this.point = getPlatform().convertToPlatformCoordinates(point).duplicate();
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
        return getPlatform().convertToWorldRotation(getRotation());
    }

    @Override
    public void setWorldRotation(float rotation) {
        setRotation(getPlatform().convertToPlatformRotation(rotation));
    }

    @Override
    public Point getWorldVelocity() {
        return velocity;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public Platform getPlatform() {
        return (platform == null) ? nullPlatform : platform;
    }

    @Override
    public void setPlatform(Platform platform) {
        setPoint(getWorldPoint());
        setRotation(getWorldRotation());
        if (platform != null) {
            setPoint(platform.convertToPlatformCoordinates(getPoint()).duplicate());
            setRotation(platform.convertToPlatformRotation(rotation));
        }
        this.platform = platform;
    }

    @Override
    public abstract void draw(Batch batch);

    @Override
    public void act(float delta) {
        velocity = getWorldPoint().subtract(lastWorldPoint).scale(1 / delta);
        lastWorldPoint = getWorldPoint();
    }

    @Override
    public boolean contains(Point point) {
        point.subtract(getWorldPoint()).rotate(-1 * getWorldRotation());
        return Math.abs(point.x) <= getWidth() / 2 && Math.abs(point.y) <= getHeight() / 2;
    }
}
