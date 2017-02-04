package net.traitors.thing.platform;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.Tile;
import net.traitors.util.Point;

public class TileGrid extends AbstractPlatform {

    private Tile[][] grid;

    public TileGrid(int width, int height) {
        super(width, height);
        grid = new Tile[width][height];
        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                grid[column][row] = new FloorTile();
                grid[column][row].setPlatform(this);
                grid[column][row].setPoint(new Point(column - (width - 1f) / 2, row - (height - 1f) / 2));
            }
        }
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
