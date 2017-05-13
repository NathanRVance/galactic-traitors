package net.traitors;

import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.Platform;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LayerLayer implements Layer {

    private List<Actor> actors = new ArrayList<>();
    private List<Thing> stuff = new ArrayList<>();
    private BetterCamera camera;
    private long actorID = 0;

    public LayerLayer(BetterCamera camera) {
        this.camera = camera;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeList(actors);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        saveData.setLayer(this);
        //Wipe out everything
        actors.clear();
        stuff.clear();
        for (Actor actor : (List<Actor>) saveData.readList()) {
            addActor(actor);
        }
    }

    @Override
    public void act(float delta) {
        for (Actor actor : new ArrayList<>(actors)) { //Avoid modification issues
            actor.act(delta);
        }

        //Resolve platforms
        //Stuff toward the end of the list end up on top
        for (int i = stuff.size() - 1; i >= 0; i--) {
            Thing thing = stuff.get(i);
            boolean foundPlatform = false;
            for (int j = i - 1; j >= 0; j--) {
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

    @Override
    public void draw() {
        draw(getDefaultCamera());
    }

    @Override
    public void draw(BetterCamera camera) {
        for (Thing thing : new ArrayList<>(stuff)) { //Avoid modification issues
            thing.draw(GalacticTraitors.getBatch(), camera);
        }
    }

    @Override
    public void addActor(Actor actor) {
        actor.setID(actorID++);
        actors.add(actor);
        if (actor instanceof Thing) {
            stuff.add((Thing) actor);
            Collections.sort(stuff, new Comparator<Thing>() {
                @Override
                public int compare(Thing thing1, Thing thing2) {
                    float surfaceArea1 = thing1.getHeight() * thing1.getWidth();
                    float surfaceArea2 = thing2.getHeight() * thing2.getWidth();
                    if (surfaceArea1 > surfaceArea2) return -1;
                    if (surfaceArea1 < surfaceArea2) return 1;
                    return 0;
                }
            });
        }
    }

    @Override
    public void removeActor(Actor actor) {
        actors.remove(actor);
        if (actor instanceof Thing) {
            stuff.remove(actor);
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        getDefaultCamera().setToOrtho(false, 5 * aspectRatio, 5);
    }

    @Override
    public BetterCamera getDefaultCamera() {
        return camera;
    }

    @Override
    public Point getBotCorner() {
        return new Point(getDefaultCamera().position.x - getWidth() / 2, getDefaultCamera().position.y - getHeight() / 2);
    }

    @Override
    public float getWidth() {
        return getDefaultCamera().viewportWidth;
    }

    @Override
    public float getHeight() {
        return getDefaultCamera().viewportHeight;
    }

    @Override
    public Point screenToLayerCoords(Point screenPoint) {
        return screenPoint.unproject(getDefaultCamera());
    }

    @Override
    public Thing getThingAt(Point point) {
        for (int i = stuff.size() - 1; i >= 0; i--) {
            if (stuff.get(i).contains(point, .5f)) {
                return stuff.get(i);
            }
        }
        return null;
    }

    @Override
    public void dispose() {
        for (Actor actor : actors) {
            if (actor instanceof Disposable) {
                ((Disposable) actor).dispose();
            }
        }
    }
}
