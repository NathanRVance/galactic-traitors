package net.traitors.thing;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.platform.Platform;
import net.traitors.util.Point;

public abstract class AbstractThing implements Thing {

    private Point point = new Point();
    private float rotation = 0;
    private Platform platform;
    private final float width;
    private final float height;

    public AbstractThing(float width, float height) {
        this.width = width;
        this.height = height;
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
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
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

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public abstract void draw(Batch batch);

    @Override
    public abstract void act(float delta);
}
