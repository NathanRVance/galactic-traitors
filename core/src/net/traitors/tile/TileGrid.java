package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.Point;

public class TileGrid implements Platform {

    private final float width;
    private final float height;
    private Tile[][] grid;
    private Platform platform = new DelegatablePlatform();

    public TileGrid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[width][];
        for (int column = 0; column < width; column++) {
            grid[column] = new Tile[height];
            for (int row = 0; row < height; row++) {
                grid[column][row] = new Tile();
                grid[column][row].setPlatform(this);
                grid[column][row].setPoint(new Point(column - (width - 1f) / 2, row - (height - 1f) / 2));
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
    public Point getWorldPoint() {
        return platform.getWorldPoint();
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
    public float getWorldRotation() {
        return platform.getWorldRotation();
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
        this.platform.setPlatform(platform);
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
    public Point getTranslationalVelocity() {
        return platform.getTranslationalVelocity();
    }

    @Override
    public void setTranslationalVelocity(Point velocity) {
        platform.setTranslationalVelocity(velocity);
    }

    @Override
    public float getRotationalVelocity() {
        return platform.getRotationalVelocity();
    }

    @Override
    public void setRotationalVelocity(float velocity) {
        platform.setRotationalVelocity(velocity);
    }

    @Override
    public void move(float delta) {
        platform.move(delta);
    }

    @Override
    public Point convertToWorldCoordinates(Point point) {
        return platform.convertToWorldCoordinates(point);
    }

    @Override
    public float convertToWorldRotation(float rotation) {
        return platform.convertToWorldRotation(rotation);
    }
}
