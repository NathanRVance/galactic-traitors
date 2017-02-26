package net.traitors.thing;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public abstract class AbstractThing implements Thing {

    private float width;
    private float height;
    private float mass;
    private Point point = new Point();
    private Point lastWorldPoint = new Point();
    private Point velocity = new Point();
    private float rotation = 0;
    private Platform platform;
    private Platform nullPlatform = new NullPlatform();

    public AbstractThing(float width, float height, float mass) {
        this.width = width;
        this.height = height;
        this.mass = mass;
    }

    public AbstractThing(float width, float height) {
        this.width = width;
        this.height = height;
        this.mass = width * height;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeFloat(width);
        sd.writeFloat(height);
        sd.writeFloat(mass);
        sd.writeFloat(point.x);
        sd.writeFloat(point.y);
        sd.writeFloat(lastWorldPoint.x);
        sd.writeFloat(lastWorldPoint.y);
        sd.writeFloat(velocity.x);
        sd.writeFloat(velocity.y);
        sd.writeFloat(rotation);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        width = saveData.readFloat();
        height = saveData.readFloat();
        mass = saveData.readFloat();
        point = new Point(saveData.readFloat(), saveData.readFloat());
        lastWorldPoint = new Point(saveData.readFloat(), saveData.readFloat());
        velocity = new Point(saveData.readFloat(), saveData.readFloat());
        rotation = saveData.readFloat();
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
        return getPlatform().convertToWorldCoordinates(getPoint());
    }

    @Override
    public void setWorldPoint(Point point) {
        setPoint(getPlatform().convertToPlatformCoordinates(point));
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
    public float getMass() {
        return mass;
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
    public void draw(Batch batch, BetterCamera camera) {
        if(camera.isWatching(this, getWidth())) {
            draw(batch);
        }
    }

    @Override
    public void act(float delta) {
        velocity = getWorldPoint().subtract(lastWorldPoint).scale(1 / delta);
        lastWorldPoint = getWorldPoint();
    }

    @Override
    public boolean contains(Point point) {
        point = point.subtract(getWorldPoint()).rotate(-1 * getWorldRotation());
        return Math.abs(point.x) <= getWidth() / 2 && Math.abs(point.y) <= getHeight() / 2;
    }
}
