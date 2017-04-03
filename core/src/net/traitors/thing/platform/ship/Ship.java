package net.traitors.thing.platform.ship;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.Thing;
import net.traitors.thing.platform.AbstractPlatform;
import net.traitors.thing.tile.FloorTile;
import net.traitors.thing.tile.Tile;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

import java.util.HashSet;
import java.util.Set;

public class Ship extends AbstractPlatform {

    private Tile[][] grid;
    private Set<ShipComponent> components = new HashSet<>();
    private ShipComputer computer = new ShipComputer();

    private Ship(int width, int height) {
        super(width, height);
    }

    public Ship() {

    }

    public ShipComputer getComputer() {
        return computer;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        sd.writeInt(grid.length);
        for (Tile[] column : grid) {
            sd.writeInt(column.length);
            for (Tile tile : column) {
                sd.writeSavable(tile);
            }
        }
        sd.writeSaveData(computer.getSaveData());
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        components = new HashSet<>();
        grid = new Tile[saveData.readInt()][];
        for (int col = 0; col < grid.length; col++) {
            grid[col] = new Tile[saveData.readInt()];
            for (int tile = 0; tile < grid[col].length; tile++) {
                grid[col][tile] = (Tile) saveData.readSavable(
                        (this.grid != null && this.grid.length >= col && this.grid[col].length >= tile) ?
                                this.grid[col][tile] : null
                );
                if (grid[col][tile] instanceof ShipComponent) {
                    components.add((ShipComponent) grid[col][tile]);
                }
            }
        }
        computer = new ShipComputer(components);
        computer.loadSaveData(saveData.readSaveData());
    }

    @Override
    public void draw(Batch batch) {
        for (Tile[] column : grid) {
            for (Tile tile : column) {
                if (tile != null) {
                    tile.draw(batch);
                }
            }
        }
    }

    /**
     * Get a usable that can be used from point
     *
     * @param user       The user that's using the usable
     * @param point      point to get usable at, in world coordinates
     * @param touchPoint point the user touched
     */
    public void useUsableAt(Thing user, Point point, Point touchPoint) {
        for (ShipComponent component : components) {
            if (component.contains(point)) {
                component.use(user, touchPoint);
                computer.componentUsed(component, user, touchPoint);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (ShipComponent component : components) {
            component.act(delta); //Updates cooldowns
        }
        computer.act(delta);
    }

    @Override
    public void dispose() {
        for (Tile[] column : grid) {
            for (Tile tile : column) {
                tile.dispose();
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
         *
         * @param tile tile to be added
         * @param x    x position for the lower left corner of this tile
         * @param y    y position for the lower left corner of this tile
         */
        void addTile(Tile tile, int x, int y) {
            ship.grid[x][y] = tile;
            prepTile(tile, x, y);
            for (int ox = x; ox < x + tile.getWidth(); ox++) {
                for (int oy = y; oy < y + tile.getHeight(); oy++) {
                    occupied[ox][oy] = true;
                }
            }
            if (tile instanceof ShipComponent) {
                ship.components.add((ShipComponent) tile);
            }
        }

        void setComputer(ShipComputer computer) {
            ship.computer = computer;
        }

        Ship getShip() {
            for (int x = 0; x < ship.grid.length; x++) {
                for (int y = 0; y < ship.grid[x].length; y++) {
                    if (!occupied[x][y]) {
                        ship.grid[x][y] = new FloorTile();
                        prepTile(ship.grid[x][y], x, y);
                    }
                }
            }

            for(ShipComponent component : ship.components) {
                component.setShip(ship);
                ship.computer.addComponent(component);
            }
            ship.computer.setShip(ship);

            return ship;
        }

        private void prepTile(Tile tile, int x, int y) {
            tile.setPlatform(ship);
            tile.setPoint(new Point(x - (ship.getWidth() - 1f) / 2 + (tile.getWidth() - 1) / 2,
                    y - (ship.getHeight() - 1f) / 2 + (tile.getHeight() - 1) / 2));
        }

    }
}
