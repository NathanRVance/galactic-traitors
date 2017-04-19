package net.traitors.thing.platform;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.Layer;

public class UniverseTile extends AbstractPlatform {

    private static final float TILE_DIM = 1000;

    public UniverseTile(Layer layer) {
        super(layer, TILE_DIM, TILE_DIM);
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
