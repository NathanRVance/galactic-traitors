package net.traitors.thing;

import net.traitors.controls.Controls;
import net.traitors.thing.item.Item;
import net.traitors.thing.platform.Platform;
import net.traitors.thing.player.Player;
import net.traitors.util.BetterCamera;
import net.traitors.util.Overlapper;
import net.traitors.util.Point;
import net.traitors.util.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Stuff {

    private List<Actor> actors = new ArrayList<Actor>();
    private List<Thing> stuff = new ArrayList<Thing>();
    private BetterCamera camera;
    private Player player;
    private Map<Platform, Set<TreeNode>> stuffOnPlatforms = new HashMap<Platform, Set<TreeNode>>();

    public Stuff(BetterCamera camera, Player player) {
        this.camera = camera;
        this.player = player;
    }

    public void addActor(Actor actor) {
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

    public void removeActor(Actor actor) {
        actors.remove(actor);
        if (actor instanceof Thing) {
            stuff.remove(actor);
        }
    }

    public List<Thing> getStuff() {
        return stuff;
    }

    public BetterCamera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public void doStuff(float delta) {
        for (Actor actor : actors) {
            actor.act(delta);
        }

        stuffOnPlatforms.clear();
        for (TreeNode tree : Overlapper.getOverlaps(stuff)) {
            placeThings(null, tree);
        }

        Point playerWorldPoint = player.getWorldPoint();
        camera.translate(playerWorldPoint.x - camera.position.x, playerWorldPoint.y - camera.position.y);
        camera.rotateWith(player.getPlatform());
        camera.update();

        List<Point> touchesInWorld = Controls.getWorldTouches(camera);
        if (touchesInWorld.size() == 1) {
            player.worldTouched(player.convertToPlatformCoordinates(touchesInWorld.get(0)));
        }
    }

    public Item getItemAt(Point point) {
        for (Set<TreeNode> platformContents : stuffOnPlatforms.values()) {
            for (TreeNode node : platformContents) {
                if (node.getThing() instanceof Item && node.getRotRet().contains(point)) {
                    return (Item) node.getThing();
                }
            }
        }
        return null;
    }

    private void placeThings(Platform parent, TreeNode child) {
        child.getThing().setPlatform(parent);
        if (parent != null) {
            stuffOnPlatforms.get(parent).add(child);
        }
        if (!child.getChildren().isEmpty()) {
            stuffOnPlatforms.put((Platform) child.getThing(), new HashSet<TreeNode>());
        }
        for (TreeNode treeNode : child.getChildren()) {
            placeThings((Platform) child.getThing(), treeNode);
        }
    }

}
