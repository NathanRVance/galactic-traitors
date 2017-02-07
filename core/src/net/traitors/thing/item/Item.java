package net.traitors.thing.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.traitors.thing.Thing;
import net.traitors.thing.usable.Usable;

public interface Item extends Thing, Usable {

    Texture getInventoryImage();

    TextureRegion getHandImage();

    /**
     * Acting for items progresses the cooldown.
     *
     * @param delta time since last call to act
     */
    void act(float delta);

}
