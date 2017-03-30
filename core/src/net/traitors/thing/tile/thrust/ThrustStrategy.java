package net.traitors.thing.tile.thrust;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Thing;
import net.traitors.util.save.Savable;

public interface ThrustStrategy extends Disposable, Savable {

    void setBase(Thing base);

    /**
     * Applies a thrust to the base
     *
     * @param user   the thing that is using this thruster
     * @param extent percent of max thrust to apply
     */
    void applyThrust(Thing user, float extent);

    void draw(Batch batch);

    void updateCooldown(float delta);

}
