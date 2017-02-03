package net.traitors.thing;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.util.Point;

public abstract class AbstractThing implements Thing {

    private final float width;
    private final float height;
    private Point point = new Point();
    private float rotation = 0;
    private Platform platform;
    private Platform nullPlatform = new NullPlatform();

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
    public void setWorldPoint(Point point) {
        this.point = getPlatform().convertToPlatformCoordinates(point);
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
            setPoint(platform.convertToPlatformCoordinates(getPoint()));
            setRotation(platform.convertToPlatformRotation(rotation));
        }
        this.platform = platform;
    }

    @Override
    public abstract void draw(Batch batch);

    @Override
    public abstract void act(float delta);
}
