package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;

public class Tile extends AbstractThing {

    private TextureRegion texture;

    public Tile() {
        super(1, 1);
        texture = TextureCreator.getTileTexture();
    }

    @Override
    public void act(float delta) {
        //Do nothing
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = getWorldPoint();
        float worldRotation = getWorldRotation();
        batch.draw(texture, worldPoint.x - getWidth() / 2, worldPoint.y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, worldRotation * MathUtils.radiansToDegrees);
    }

}
