package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.Point;
import net.traitors.util.Thing;

public class TileGrid implements Thing {

    private final float width;
    private final float height;
    private Tile[][] grid;
    private Point point = new Point();
    private float rotation = 0f;

    public TileGrid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[width][];
        for (int column = 0; column < width; column++) {
            grid[column] = new Tile[height];
            for (int row = 0; row < height; row++) {
                grid[column][row] = new AbstractTile();
            }
        }
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
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void draw(Batch batch) {
        for (int column = 0; column < grid.length; column++) {
            for (int row = 0; row < grid[column].length; row++) {
                batch.draw(grid[column][row].getTexture(), getPoint().x + column, getPoint().y + row, 1, 1);
            }
        }
    }
}
