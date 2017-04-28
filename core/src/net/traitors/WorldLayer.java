package net.traitors;

import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.thing.item.Item;
import net.traitors.thing.platform.Platform;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorldLayer extends LayerLayer {

    public WorldLayer() {
        super(GalacticTraitors.getCamera());
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof Thing) {
            Collections.sort(stuff, new Comparator<Thing>() {
                @Override
                public int compare(Thing thing1, Thing thing2) {
                    float surfaceArea1 = thing1.getHeight() * thing1.getWidth();
                    float surfaceArea2 = thing2.getHeight() * thing2.getWidth();
                    if (surfaceArea1 > surfaceArea2) return 1;
                    if (surfaceArea1 < surfaceArea2) return -1;
                    return 0;
                }
            });
        }
    }

    @Override
    public void draw() {
        draw(getDefaultCamera());
    }

    @Override
    public void draw(BetterCamera camera) {
        for (int i = stuff.size() - 1; i >= 0; i--) {
            stuff.get(i).draw(GalacticTraitors.getBatch(), camera);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        placeStuff(stuff);
    }

    public Item getItemAt(Point point) {
        for (Thing thing : stuff) {
            if (thing instanceof Item && thing.contains(point, .5f)) {
                return (Item) thing;
            }
        }
        return null;
    }

    /**
     * Puts stuff on stuff
     *
     * @param stuff all the things we're dealing with, sorted by stacking
     *              precedence (earlier in the list ends up on top of stacks)
     */
    private void placeStuff(List<Thing> stuff) {
        //Stuff toward the start of the list end up on top
        for (int i = 0; i < stuff.size(); i++) {
            Thing thing = stuff.get(i);
            boolean foundPlatform = false;
            for (int j = i + 1; j < stuff.size(); j++) {
                if (stuff.get(j) instanceof Platform
                        && stuff.get(j).contains(thing.getWorldPoint())) {
                    thing.setPlatform((Platform) stuff.get(j));
                    foundPlatform = true;
                    break;
                }
            }
            if (!foundPlatform) {
                thing.setPlatform(null);
            }
        }
    }
}
