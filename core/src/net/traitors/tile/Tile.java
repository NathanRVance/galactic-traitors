package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;

public interface Tile extends Drawable, Disposable {

    TextureRegion getTexture();

}
