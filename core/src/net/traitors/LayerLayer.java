package net.traitors;

import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

import java.util.ArrayList;
import java.util.List;

public class LayerLayer implements Savable, Layer {

    protected List<Actor> actors = new ArrayList<>();
    protected List<Thing> stuff = new ArrayList<>();
    private BetterCamera camera;

    private SaveData cachedSaveData = null;
    private SaveData dataToLoad = null;
    private long ID = 0;

    public LayerLayer(BetterCamera camera) {
        this.camera = camera;
    }

    @Override
    public SaveData getSaveData() {
        return cachedSaveData;
    }

    private void updateSaveData() {
        SaveData sd = new SaveData();
        sd.writeLong(ID++);
        sd.writeList(actors, null);
        sd.writeSaveData(getDefaultCamera().getSaveData());
        cachedSaveData = sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        dataToLoad = saveData;
    }

    private void resolveSavedData() {
        if (dataToLoad != null) {
            //Make a new temp variable so that dataToLoad can be replaced in another thread.
            SaveData saveData = dataToLoad;
            long id = saveData.readLong();
            if (id <= ID) return; //Sometimes updates arrive out of order.
            ID = id;
            //Wipe out everything
            actors.clear();
            stuff.clear();

            for (Actor actor : saveData.readList(actors, Actor.class)) {
                if (!actors.contains(actor)) { //This instance could be recycled from in there
                    addActor(actor);
                }
            }
            getDefaultCamera().loadSaveData(saveData.readSaveData());
        }
    }

    @Override
    public void act(float delta) {
        for(Actor actor : new ArrayList<>(actors)) { //Avoid modification issues
            actor.act(delta);
        }
    }

    @Override
    public void draw() {
        draw(getDefaultCamera());
    }

    @Override
    public void draw(BetterCamera camera) {
        for(Thing thing : new ArrayList<>(stuff)) { //Avoid modification issues
            thing.draw(GalacticTraitors.getBatch(), camera);
        }
    }

    @Override
    public void addActor(Actor actor) {
        actors.add(actor);
        if(actor instanceof Thing) {
            stuff.add((Thing) actor);
        }
    }

    @Override
    public void removeActor(Actor actor) {
        actors.remove(actor);
        if(actor instanceof Thing) {
            stuff.remove(actor);
        }
    }

    @Override
    public boolean hasActor(Actor actor) {
        return actors.contains(actor);
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
    public void dispose() {
        for (Actor actor : actors) {
            if (actor instanceof Disposable) {
                ((Disposable) actor).dispose();
            }
        }
    }
}
