package net.traitors.thing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.controls.Controls;
import net.traitors.thing.item.Item;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.thing.player.Player;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.net.MultiplayerConnect;
import net.traitors.util.save.Savable;
import net.traitors.util.save.SaveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stuff implements Savable {

    private final Map<Player, Controls.UserInput> inputs = new HashMap<>();
    private List<Actor> actors = new ArrayList<>();
    private List<Thing> stuff = new ArrayList<>();
    private List<Actor> removeBuffer = new ArrayList<>();
    private List<Actor> addBuffer = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private int playersToAdd = 0;
    private BetterCamera camera;
    private float delta;

    private SaveData cachedSaveData = null;
    private SaveData dataToLoad = null;
    private long ID = 0;

    public Stuff(BetterCamera camera) {
        this.camera = camera;
        addPlayer();
        resolveBuffers();
    }

    @Override
    public SaveData getSaveData() {
        return cachedSaveData;
    }

    private void updateSaveData() {
        SaveData sd = new SaveData();
        sd.writeLong(ID++);
        sd.writeList(actors, null);
        sd.writeSaveData(camera.getSaveData());
        sd.writeFloat(delta);
        sd.writeInt(players.size());
        for (Player player : players) {
            sd.writeSavable(inputs.get(player));
        }
        cachedSaveData = sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        dataToLoad = saveData;
    }

    private void resolveSavedData() {
        if(dataToLoad != null) {
            //Make a new temp variable so that dataToLoad can be replaced by another thread.
            SaveData saveData = dataToLoad;
            //And set dataToLoad to be null. This is not atomic and may result in missing an update
            //cycle, but at the worst case we only miss every other one.
            dataToLoad = null;
            long id = saveData.readLong();
            if(id <= ID) return; //Sometimes updates come out of order.
            ID = id;
            for (Actor actor : saveData.readList(actors, Actor.class)) {
                addActor(actor);
            }
            camera.loadSaveData(saveData.readSaveData());
            delta = saveData.readFloat();
            resolveBuffers();
            int numInputs = saveData.readInt();
            for (int i = 0; i < numInputs; i++) {
                inputs.put(players.get(i), (Controls.UserInput) saveData.readSavable(inputs.get(players.get(i))));
            }
        }
    }

    public void updateInputs(List<Controls.UserInput> in) {
        System.out.println("Updating inputs");
        for (int i = 0; i < players.size(); i++) {
            if (in.size() > i) {
                inputs.put(players.get(i), in.get(i));
            }

        }
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
            if (actor instanceof Player) {
                players.remove(actor);
            }
            if (actor instanceof Disposable) {
                ((Disposable) actor).dispose();
            }
        }
        boolean addedThing = false;
        while (playersToAdd > 0) {
            addPlayer();
            playersToAdd--;
            addedThing = true;
        }
        for (Actor actor : addBuffer) {
            actors.add(actor);
            if (actor instanceof Thing) {
                stuff.add((Thing) actor);
                addedThing = true;
            }
            if (actor instanceof Player) {
                players.add((Player) actor);
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
        return players.get(MultiplayerConnect.getPlayerID());
    }

    private void addPlayer() {
        System.out.println("Adding player");
        Player p = new Player(Color.GREEN, new Color(0xdd8f4fff), Color.BROWN, Color.BLUE, Color.BLACK, players.size());
        addActor(p);
        inputs.put(p, new Controls.UserInput());
    }

    public boolean clean() {
        return playersToAdd == 0 && cachedSaveData != null;
    }

    public void addPlayerAsync() {
        playersToAdd++;
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
        System.out.println("Doing stuff");
        resolveBuffers();
        this.delta = delta;
        placeStuff(stuff);

        for (Actor actor : actors) {
            actor.act(delta);
        }

        for (Player player : players) {
            player.act(delta);
        }

        Point playerWorldPoint = getPlayer().getWorldPoint();
        camera.translate(playerWorldPoint.x - camera.position.x, playerWorldPoint.y - camera.position.y);
        if (camera.getRotatingWith() instanceof NullPlatform)
            camera.setRotateDepth(1);
        camera.update();

        inputs.put(getPlayer(), Controls.getUserInput());
        for (Player player : players) {
            player.move(delta, inputs.get(player));
        }


        resolveBuffers();
        updateSaveData(); //TODO: Add method to check if this is a server, and only do this if so.
        resolveSavedData(); //TODO: Same, but for client.
        System.out.println("Done doing stuff");
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
