package net.traitors.thing.platform.ship;

import net.traitors.thing.Thing;
import net.traitors.util.Point;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

import java.util.HashSet;
import java.util.Set;

public class ShipComputer implements Savable {

    private Set<ShipComponent> components = new HashSet<>();
    private Set<Set<ShipComponent>> syncedComponents = new HashSet<>();

    public ShipComputer() {
    }

    ShipComputer(Set<ShipComponent> components) {
        for (ShipComponent component : components) {
            components.add(component);
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
     * Causes use(Thing user) on all synced components to be called with the same user.
     *
     * @param component the component that was just used
     * @param user      the user that used this component
     */
    public void componentUsed(ShipComponent component, Thing user, Point touchPoint) {
        //So that we don't get an infinite loop of components syncing with each other, we keep track
        //of the ones that have already entered the call stack.
        if (components.contains(component)) {
            for (Set<ShipComponent> comps : syncedComponents) {
                if (comps.contains(component)) {
                    for (ShipComponent c : comps) {
                        if (c != component) {
                            components.remove(c);
                            c.use(user, touchPoint);
                            components.add(c);
                        }
                    }
                }
            }
        }
    }
}
