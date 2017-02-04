package net.traitors.thing.platform;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.Tile;
import net.traitors.util.Point;

public class Ship extends AbstractPlatform {

    private Tile[][] grid;

    private Ship(int width, int height) {
        super(width, height);
    }

    @Override
    public void draw(Batch batch) {
        for (Tile[] column : grid) {
            for (Tile tile : column) {
                if(tile != null) {
                    tile.draw(batch);
                }
            }
        }
    }


    static class ShipBuilder {

        private Ship ship;
        private boolean[][] occupied;

        ShipBuilder(int width, int height) {
            ship = new Ship(width, height);
            ship.grid = new Tile[width][height];
            occupied = new boolean[width][height];
        }

        /**
         * Add a tile to the ship, which replaces floor tiles
         * @param tile tile to be added
         * @param x x position for the lower left corner of this tile
         * @param y y position for the lower left corner of this tile
         */
        void addTile(Tile tile, int x, int y) {
            ship.grid[x][y] = tile;
            prepTile(tile, x, y);
            for(int ox = x; ox < x + tile.getWidth(); ox++) {
                for (int oy = y; oy < y + tile.getHeight(); oy++) {
                    occupied[ox][oy] = true;
                }
            }
        }

        Ship getShip() {
            for(int x = 0; x < ship.grid.length; x++) {
                for(int y = 0; y < ship.grid[x].length; y++) {
                    if(! occupied[x][y]) {
                        ship.grid[x][y] = new FloorTile();
                        prepTile(ship.grid[x][y], x, y);
                    }
                }
            }
            return ship;
        }

        private void prepTile(Tile tile, int x, int y) {
            tile.setPlatform(ship);
            tile.setPoint(new Point(x - (ship.getWidth() - 1f) / 2 + (tile.getWidth() - 1) / 2,
                    y - (ship.getHeight() - 1f) / 2 + (tile.getHeight() - 1) / 2));
        }

    }
}
