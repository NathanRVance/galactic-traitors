package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.ship.ShipComponent;
import net.traitors.thing.platform.ship.ShipComputer;
import net.traitors.thing.tile.thrust.ThrustStrategy;

public class ThrusterTile extends AbstractThing implements ShipComponent {

    private ThrustStrategy thrustStrategy;
    private transient ShipComputer computer;

    public ThrusterTile(float width, float height, float rotation, ThrustStrategy thrustStrategy) {
        super(width, height);
        setRotation(rotation);
        this.thrustStrategy = thrustStrategy;
        thrustStrategy.setBase(this);
    }

    @Override
    public void draw(Batch batch) {
        thrustStrategy.draw(batch);
    }

    @Override
    public void use(Thing user) {
        thrustStrategy.applyThrust(user);
        if (computer != null) computer.componentUsed(this, user);
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

    @Override
    public void setUseCallback(ShipComputer computer) {
        this.computer = computer;
    }
}
