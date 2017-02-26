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
import java.util.List;

public class Stuff implements Savable {

    private List<Actor> actors = new ArrayList<>();
    private List<Thing> stuff = new ArrayList<>();
    private List<Actor> removeBuffer = new ArrayList<>();
    private List<Actor> addBuffer = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private int playersToAdd = 0;
    private BetterCamera camera;
    private float delta;

    public Stuff(BetterCamera camera) {
        this.camera = camera;
        addPlayer();
        resolveBuffers();
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeList(actors, null);
        sd.writeSaveData(camera.getSaveData());
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        for(Actor actor : saveData.readList(actors, Actor.class)) {
            addActor(actor);
        }
        camera.loadSaveData(saveData.readSaveData());
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

        while (playersToAdd > 0) {
            addPlayer();
            playersToAdd--;
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
    }

    public boolean clean() {
        return playersToAdd == 0;
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

    public synchronized void doStuff(float delta) {
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

        getPlayer().move(delta, Controls.getUserInput());
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
