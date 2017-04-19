package net.traitors.thing.platform.ship;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.Layer;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.thing.tile.ThrusterTile;
import net.traitors.thing.tile.thrust.RotationalThrusterStrategy;
import net.traitors.util.Point;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShipComputer implements Savable, Actor {

    private Map<Point, ThrusterTile> thrusters = new HashMap<>();
    private Set<ShipComponent> components = new HashSet<>();
    private Set<Set<ShipComponent>> syncedComponents = new HashSet<>();
    private Ship ship;

    private boolean autostop = false;

    public ShipComputer() {
    }

    ShipComputer(Set<ShipComponent> components) {
        for (ShipComponent component : components) {
            components.add(component);
        }
    }

    void setShip(Ship ship) {
        this.ship = ship;
    }

    void addComponent(ShipComponent component) {
        components.add(component);
        if (component instanceof ThrusterTile &&
                ((ThrusterTile) component).getThrustStrategy() instanceof RotationalThrusterStrategy) {
            ThrusterTile tile = (ThrusterTile) component;
            for (Point p : ((RotationalThrusterStrategy) tile.getThrustStrategy()).getThrusters()) {
                thrusters.put(p, tile);
            }
        }
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeInt(syncedComponents.size());
        for (Set<ShipComponent> components : syncedComponents) {
            sd.writeInt(components.size());
            for (ShipComponent component : components) {
                Point p = component.getPoint();
                sd.writeFloat(p.x);
                sd.writeFloat(p.y);
            }
        }
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        syncedComponents = new HashSet<>();
        int len = saveData.readInt();
        for (int i = 0; i < len; i++) {
            int len2 = saveData.readInt();
            Set<ShipComponent> components = new HashSet<>();
            for (int comp = 0; comp < len2; comp++) {
                Point p = new Point(saveData.readFloat(), saveData.readFloat());
                for (ShipComponent sp : this.components) {
                    if (p.equals(sp.getPoint())) {
                        components.add(sp);
                        break;
                    }
                }
            }
            syncedComponents.add(components);
        }
    }

    /**
     * Causes both components to be used when one is used. If the components aren't already
     * monitored, they will be.
     *
     * @param components the components to be synced
     */
    void syncUsages(ShipComponent... components) {
        Set<ShipComponent> synced = new HashSet<>();
        for (ShipComponent component : components) {
            this.components.add(component);
            synced.add(component);
        }
        syncedComponents.add(synced);
    }

    /**
     * Causes use(Thing user) on all synced components to be called with the same user. This method is called
     * by Ship and not by the components themselves, otherwise we could get into an infinite loop!
     *
     * @param component the component that was just used
     * @param user      the user that used this component
     */
    void componentUsed(ShipComponent component, Thing user, Point touchPoint) {
        for (Set<ShipComponent> comps : syncedComponents) {
            if (comps.contains(component)) {
                for (ShipComponent c : comps) {
                    if (c != component) {
                        c.use(user, touchPoint);
                    }
                }
            }
        }
    }

    public void toggleAutostop() {
        autostop = !autostop;
    }

    public boolean isAutostop() {
        return autostop;
    }

    private void autostop(float delta) {
        float rotVel = ship.getRotationalVelocity();
        Point transVel = ship.getTranslationalVelocity();
        //Goal: Slow down both rotVel and transVel by firing the correct thrusters

        for (Point force : thrusters.keySet()) {
            Point radius = thrusters.get(force).getPoint();
            float dAngVel = force.angAccel(radius, ship) * delta;
            Point dTransVel = force.transAccel(ship).scale(delta);

            //Prepare to use thruster
            ThrusterTile tt = thrusters.get(force);
            VirtualUser user = new VirtualUser(getLayer(), tt.getPoint(), force.angle() + (float) Math.PI);
            if (transVel.distanceFromZero() > .001 && transVel.distanceFromZero() > transVel.add(dTransVel).distanceFromZero()) {
                //Point slows down translation
                tt.getThrustStrategy().applyThrust(user, 1);
            } else if (Math.abs(rotVel) > .001 && (rotVel < 0) != (dAngVel < 0)) {
                //dAngVel * x = - rotVel
                float percentUse = rotVel / dAngVel * -1;
                if (percentUse > 1) percentUse = 1;
                tt.getThrustStrategy().applyThrust(user, percentUse);
            }
        }

    }

    @Override
    public void act(float delta) {
        if (isAutostop()) {
            autostop(delta);
        }
    }

    @Override
    public Layer getLayer() {
        return ship.getLayer();
    }

    private static class VirtualUser extends AbstractThing {

        private VirtualUser(Layer layer, Point point, float rotation) {
            super(layer);
            setPoint(point);
            setRotation(rotation);
        }

        @Override
        public void draw(Batch batch) {
            //Do nothing
        }

        @Override
        public void dispose() {
            //Do nothing
        }
    }
}
