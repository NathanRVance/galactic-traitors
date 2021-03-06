package net.traitors.thing;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.Layer;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public abstract class AbstractThing extends AbstractActor implements Thing {

    private float width;
    private float height;
    private float mass;
    private Point point = new Point();
    private Point lastWorldPoint = null;
    private Point translationalVelocity = new Point(); //TODO: Make it work across platforms
    private Point translationalAcceleration = new Point();
    private float rotation = 0;
    private Platform platform;
    private Platform nullPlatform = new NullPlatform();

    public AbstractThing(Layer layer, float width, float height, float mass) {
        super(layer);
        this.width = width;
        this.height = height;
        this.mass = mass;
    }

    public AbstractThing(Layer layer, float width, float height) {
        super(layer);
        this.width = width;
        this.height = height;
        this.mass = width * height;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        sd.writeFloat(width);
        sd.writeFloat(height);
        sd.writeFloat(mass);
        sd.writePoint(getWorldPoint()); //This thing will not have a platform immediately after loadSaveData is called
        sd.writePoint(lastWorldPoint);
        sd.writePoint(getWorldVelocity());
        sd.writeFloat(getWorldRotation());
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        width = saveData.readFloat();
        height = saveData.readFloat();
        mass = saveData.readFloat();
        point = saveData.readPoint();
        lastWorldPoint = saveData.readPoint();
        translationalVelocity = saveData.readPoint();
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
        return translationalVelocity.rotate(getPlatform().getRotation()).add(getPlatform().getWorldVelocity());
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
        if(platform != getPlatform()) {
            //Rebase in world coordinates
            setPoint(getWorldPoint());
            setRotation(getWorldRotation());
            setTranslationalVelocity(getWorldVelocity());
            if (platform != null) {
                //Convert to platform coordinates
                setPoint(platform.convertToPlatformCoordinates(getPoint()));
                setRotation(platform.convertToPlatformRotation(rotation));
                setTranslationalVelocity(getTranslationalVelocity().subtract(platform.getWorldVelocity()).rotate(platform.getRotation() * -1));
            }
            this.platform = platform;
        }
    }

    @Override
    public void draw(Batch batch, BetterCamera camera) {
        if (camera.isWatching(this, Math.max(getWidth(), getHeight()))) {
            draw(batch);
        }
    }

    @Override
    public void act(float delta) {
        setTranslationalVelocity(getTranslationalVelocity().add(translationalAcceleration.scale(delta)));
        translationalAcceleration = new Point();
        setPoint(getPoint().add(getTranslationalVelocity().scale(delta)));
        setTranslationalVelocity(getPlatform().applyFriction(getTranslationalVelocity(), delta));
    }

    @Override
    public Layer getLayer() {
        return super.getLayer();
    }

    @Override
    public boolean contains(Point point) {
        return contains(point, 0);
    }

    @Override
    public boolean contains(Point point, float margin) {
        point = point.subtract(getWorldPoint()).rotate(-1 * getWorldRotation());
        return Math.abs(point.x) <= width / 2 + margin && Math.abs(point.y) <= height / 2 + margin;
    }

    @Override
    public void applyForce(Point force) {
        translationalAcceleration = translationalAcceleration.add(force.transAccel(this));
    }
}
