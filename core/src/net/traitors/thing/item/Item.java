package net.traitors.thing.item;

import com.badlogic.gdx.graphics.Texture;

import net.traitors.thing.Thing;

public interface Item extends Thing {

    Texture getInventoryImage();

    Texture getHandImage();

    void use();

    /**
     * Acting for items progresses the cooldown.
     *
     * @param delta time since last call to act
     */
    void act(float delta);

    /**
     * Gets the progress this item has to cooling off, where 1 means it's ready to fire.
     * @return cooldown percent, ranging from 0 to 1
     */
    float getCooldownPercent();

}
