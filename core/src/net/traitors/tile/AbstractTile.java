package net.traitors.tile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.AbstractDrawable;
import net.traitors.util.TextureCreator;

public class AbstractTile extends AbstractDrawable implements Tile {

    private Texture texture;

    public AbstractTile() {
        super(1, 1);
        texture = TextureCreator.getTileTexture();
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(texture, getPoint().x, getPoint().y, 1, 1);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
