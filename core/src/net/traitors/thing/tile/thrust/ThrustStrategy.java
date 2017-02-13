package net.traitors.thing.tile.thrust;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Thing;

public interface ThrustStrategy extends Disposable {

    void setBase(Thing base);

    void applyThrust(Thing user);

    void draw(Batch batch);

    void updateCooldown(float delta);

}
