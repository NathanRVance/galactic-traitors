package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.traitors.util.AbstractDrawable;

public class TileGrid extends AbstractDrawable implements Drawable {

    private Tile[][] grid;

    public TileGrid(int width, int height) {
        super(width, height);
        grid = new Tile[width][];
        for (int column = 0; column < width; column++) {
            grid[column] = new Tile[height];
            for (int row = 0; row < height; row++) {
                grid[column][row] = new AbstractTile();
            }
        }
    }

    @Override
    public void draw(Batch batch) {
        for (int column = 0; column < grid.length; column++) {
            for (int row = 0; row < grid[column].length; row++) {
                batch.draw(grid[column][row].getTexture(), getPoint().x + column, getPoint().y + row, 1, 1);
            }
        }
    }

    @Override
    public void dispose() {
        for (Tile[] column : grid) {
            for (Tile tile : column) {
                tile.dispose();
            }
        }
    }
}
