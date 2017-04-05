package net.traitors.thing.platform;

import com.badlogic.gdx.graphics.g2d.Batch;

public class UniverseTile extends AbstractPlatform {

    private static final float TILE_DIM = 1000;

    @Override
    public float getWidth() {
        return TILE_DIM;
    }

    @Override
    public float getHeight() {
        return TILE_DIM;
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void dispose() {

    }
}
