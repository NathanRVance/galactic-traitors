package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.traitors.util.Point;
import net.traitors.util.TextureCreator;
import net.traitors.util.Thing;

public class Tile implements Thing {

    private TextureRegion texture;

    private Point point = new Point();
    private float rotation = 0f;

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
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
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
    public void draw(Batch batch) {
        batch.draw(texture, getPoint().x - getWidth() / 2, getPoint().y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, rotation);
    }

}
