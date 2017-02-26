package net.traitors.thing.platform.ship;

import net.traitors.thing.Thing;
import net.traitors.util.Point;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ShipComputer {

    private Set<ShipComponent> components = new HashSet<>();
    private Set<Set<ShipComponent>> shipComponents = new HashSet<>();

    public ShipComputer() {
    }

    public ShipComputer(Serializable savedComponents, Set<ShipComponent> components) {
        Set<Set<Point>> save = (Set<Set<Point>>) savedComponents;
        for(ShipComponent component : components) {
            addComponent(component);
        }
        for (Set<Point> points : save) {
            Set<ShipComponent> c = new HashSet<>();
            for (Point point : points) {
                for (ShipComponent component : components) {
                    if(point.equals(component.getPoint())) {
                        c.add(component);
                        break;
                    }
                }
            }
            shipComponents.add(c);
        }
    }

    public Serializable saveComponents() {
        //Declared by type not interface because it must be serializable
        HashSet<HashSet<Point>> save = new HashSet<>();
        for (Set<ShipComponent> components : shipComponents) {
            HashSet<Point> s = new HashSet<>();
            for (ShipComponent component : components) {
                s.add(component.getPoint());
            }
            save.add(s);
        }
        return save;
    }

    /**
     * Adds a component to be monitored by this computer.
     *
     * @param component the ShipComponent to be monitored.
     */
    public void addComponent(ShipComponent component) {
        components.add(component);
        component.setUseCallback(this);
    }

    /**
     * Causes both components to be used when one is used. If the components aren't already
     * monitored, they will be.
     *
     * @param components the components to be synced
     */
    public void syncUsages(ShipComponent... components) {
        Set<ShipComponent> synced = new HashSet<>();
        for (ShipComponent component : components) {
            addComponent(component);
            synced.add(component);
        }
        shipComponents.add(synced);
    }

    /**
     * Causes use(Thing user) on all synced components to be called with the same user.
     *
     * @param component the component that was just used
     * @param user      the user that used this component
     */
    public void componentUsed(ShipComponent component, Thing user) {
        //So that we don't get an infinite loop of components syncing with each other, we keep track
        //of the ones that have already entered the call stack.
        if (components.contains(component)) {
            for (Set<ShipComponent> comps : shipComponents) {
                if (comps.contains(component)) {
                    for (ShipComponent c : comps) {
                        if (c != component) {
                            components.remove(c);
                            c.use(user);
                            components.add(c);
                        }
                    }
                }
            }
        }
    }

}
