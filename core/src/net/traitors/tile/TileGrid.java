package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.Point;

public class TileGrid extends AbstractPlatform {

    private final float width;
    private final float height;
    private Tile[][] grid;

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
}
