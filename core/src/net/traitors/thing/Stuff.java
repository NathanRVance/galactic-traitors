package net.traitors.thing;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.controls.Controls;
import net.traitors.thing.item.Item;
import net.traitors.thing.platform.Platform;
import net.traitors.thing.player.Player;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Stuff {

    private List<Actor> actors = new ArrayList<>();
    private List<Thing> stuff = new ArrayList<>();
    private List<Actor> removeBuffer = new ArrayList<>();
    private List<Actor> addBuffer = new ArrayList<>();
    private BetterCamera camera;
    private Player player;
    private float delta;

    public Stuff(BetterCamera camera, Player player) {
        this.camera = camera;
        this.player = player;
    }

    public void addActor(Actor actor) {
        addBuffer.add(actor);
    }

    public void removeActor(Actor actor) {
        removeBuffer.add(actor);
    }

    private void resolveBuffers() {
        for (Actor actor : removeBuffer) {
            actors.remove(actor);
            if (actor instanceof Thing) {
                stuff.remove(actor);
            }
            if (actor instanceof Disposable) {
                ((Disposable) actor).dispose();
            }
        }
        boolean addedThing = false;
        for (Actor actor : addBuffer) {
            actors.add(actor);
            if (actor instanceof Thing) {
                stuff.add((Thing) actor);
                addedThing = true;
            }
        }
        if (addedThing) {
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

        addBuffer.clear();
        removeBuffer.clear();
    }

    public BetterCamera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public float getDelta() {
        return delta;
    }

    public void drawStuff(Batch batch, BetterCamera camera) {
        for (int i = stuff.size() - 1; i >= 0; i--) {
            stuff.get(i).draw(batch, camera);
        }
    }

    public void doStuff(float delta) {
        this.delta = delta;
        placeStuff(stuff);

        for (Actor actor : actors) {
            actor.act(delta);
        }

        Point playerWorldPoint = player.getWorldPoint();
        camera.translate(playerWorldPoint.x - camera.position.x, playerWorldPoint.y - camera.position.y);
        if (camera.getRotatingWith() == null)
            camera.rotateWith(player.getPlatform());
        camera.update();

        player.move(delta, Controls.getUserInput());

        resolveBuffers();
    }

    public Item getItemAt(Point point) {
        for (Thing thing : stuff) {
            if (thing instanceof Item && thing.contains(point)) {
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
            thing.setPlatform(null);
            for (int j = i + 1; j < stuff.size(); j++) {
                if (stuff.get(j) instanceof Platform
                        && stuff.get(j).contains(thing.getWorldPoint())) {
                    thing.setPlatform((Platform) stuff.get(j));
                    break;
                }
            }
        }
    }

}
