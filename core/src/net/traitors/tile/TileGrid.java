package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.Point;
import net.traitors.util.Thing;

public class TileGrid implements Platform {

    private final float width;
    private final float height;
    private Tile[][] grid;
    private float rotation = 0f;
    private Platform platform = new DelegatablePlatform();

    public TileGrid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[width][];
        for (int column = 0; column < width; column++) {
            grid[column] = new Tile[height];
            for (int row = 0; row < height; row++) {
                grid[column][row] = new Tile();
                grid[column][row].setPoint(new Point(platform.getPoint().x + column + .5f, platform.getPoint().y + row + .5f));
            }
        }
    }

    @Override
    public Point getPoint() {
        return platform.getPoint();
    }

    @Override
    public void setPoint(Point point) {
        platform.setPoint(point);
    }

    @Override
    public float getRotation() {
        return platform.getRotation();
    }

    @Override
    public void setRotation(float rotation) {
        platform.setRotation(rotation);
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
    public void draw(Batch batch) {
        for (Tile[] column : grid) {
            for (Tile tile : column) {
                tile.draw(batch);
            }
        }
    }

    @Override
    public void setTranslationalVelocity(Point velocity) {
        platform.setTranslationalVelocity(velocity);
    }

    @Override
    public Point getTranslationalVelocity() {
        return platform.getTranslationalVelocity();
    }

    @Override
    public void setRotationalVelocity(float velocity) {

    }

    @Override
    public float getRotationalVelocity() {
        return 0;
    }

    @Override
    public void addThing(Thing thing) {

    }

    @Override
    public void removeThing(Thing thing) {

    }

    @Override
    public void move(float delta) {

    }
}
