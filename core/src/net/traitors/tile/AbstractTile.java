package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.traitors.util.AbstractDrawable;
import net.traitors.util.TextureCreator;

public class AbstractTile extends AbstractDrawable implements Tile {

    private TextureRegion texture;

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
        texture.getTexture().dispose();
    }

    @Override
    public TextureRegion getTexture() {
        return texture;
    }
}
