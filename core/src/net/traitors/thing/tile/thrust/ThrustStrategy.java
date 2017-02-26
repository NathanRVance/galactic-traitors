package net.traitors.thing.tile.thrust;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Thing;
import net.traitors.util.save.Savable;

public interface ThrustStrategy extends Disposable, Savable {

    void setBase(Thing base);

    void applyThrust(Thing user);

    void draw(Batch batch);

    void updateCooldown(float delta);

}
