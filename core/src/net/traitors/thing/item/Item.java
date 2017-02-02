package net.traitors.thing.item;

import com.badlogic.gdx.graphics.Texture;

import net.traitors.thing.Thing;

public interface Item extends Thing {

    Texture getInventoryImage();

    Texture getHandImage();

}
