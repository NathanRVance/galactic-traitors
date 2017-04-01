package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.ship.ShipComponent;
import net.traitors.thing.tile.thrust.ThrustStrategy;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public class ThrusterTile extends AbstractThing implements ShipComponent {

    private ThrustStrategy thrustStrategy;

    public ThrusterTile(float width, float height, float rotation, ThrustStrategy thrustStrategy) {
        super(width, height);
        setRotation(rotation);
        this.thrustStrategy = thrustStrategy;
        thrustStrategy.setBase(this);
    }

    public ThrusterTile() {

    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        sd.writeSavable(thrustStrategy);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        thrustStrategy = (ThrustStrategy) saveData.readSavable(thrustStrategy);
        thrustStrategy.setBase(this);
    }

    @Override
    public void draw(Batch batch) {
        thrustStrategy.draw(batch);
    }

    @Override
    public void use(Thing user, Point touchPoint) {
        float extent = user.getPoint().distance(touchPoint) / 2;
        thrustStrategy.applyThrust(user, extent > 1 ? 1 : extent);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        thrustStrategy.updateCooldown(delta);
    }

    @Override
    public float getCooldownPercent() {
        return 1;
    }

    @Override
    public void dispose() {
        thrustStrategy.dispose();
    }
}
