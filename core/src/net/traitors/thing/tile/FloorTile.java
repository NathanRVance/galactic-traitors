package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;

public class FloorTile extends AbstractThing implements Tile {

    public FloorTile() {
        super(1, 1);
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = getWorldPoint();
        float worldRotation = getWorldRotation();
        batch.draw(TextureCreator.getTileTexture(), worldPoint.x - getWidth() / 2, worldPoint.y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, worldRotation * MathUtils.radiansToDegrees);
    }

    @Override
    public void dispose() {
        //Do nothing since this texture comes from the TextureCreator, which reuses it.
    }
}
