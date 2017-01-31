package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.util.Point;
import net.traitors.util.TextureCreator;
import net.traitors.util.Thing;

public class Tile implements Thing {

    private TextureRegion texture;

    private Point point = new Point();
    private float rotation = 0f;
    private Platform platform;

    public Tile() {
        texture = TextureCreator.getTileTexture();
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
        return 1f;
    }

    @Override
    public float getHeight() {
        return 1f;
    }

    @Override
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @Override
    public void act(float delta) {
        //Do nothing
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = getWorldPoint();
        float worldRotation = getWorldRotation();
        batch.draw(texture, worldPoint.x - getWidth() / 2, worldPoint.y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, worldRotation * MathUtils.radiansToDegrees);
    }

}
