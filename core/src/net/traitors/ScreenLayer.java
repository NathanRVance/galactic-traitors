package net.traitors;

import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.ui.ScreenElements.CompassBar;
import net.traitors.ui.ScreenElements.InventoryBar;
import net.traitors.ui.ScreenElements.Touchpad;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.List;

public class ScreenLayer implements Layer {

    //TODO: Merge this with WorldLayer

    private List<Actor> actors = new ArrayList<>();
    private BetterCamera camera = new BetterCamera();
    private Touchpad touchpad;
    private InventoryBar inventoryBar;
    private CompassBar compassBar;

    ScreenLayer() {
        getDefaultCamera().setToOrtho(false, 5, 5);
        //Need to add a touchpad, inventory, and compass bar.
        //Since there is a finite number of things, we'll keep track of them explicitly
        touchpad = new Touchpad(this, getHeight() / 5);
        touchpad.setPoint(getBotCorner().add(new Point(touchpad.getWidth() / 2, touchpad.getHeight() / 2)));
        addActor(touchpad);

        inventoryBar = new InventoryBar(this, 5, getHeight());
        //Since it is to the right, set point on resize
        addActor(inventoryBar);

        compassBar = new CompassBar(this, touchpad.getHeight() * 2 / 3);
        compassBar.setPoint(getBotCorner().add(new Point(compassBar.getWidth() / 2, getHeight() - compassBar.getHeight() / 2)));
        addActor(compassBar);
    }

    @Override
    public void act(float delta) {
        for (Actor actor : actors) {
            actor.act(delta);
        }
    }

    @Override
    public void draw() {
        draw(getDefaultCamera());
    }

    @Override
    public void draw(BetterCamera camera) {
        for (Actor actor : actors) {
            if (actor instanceof Thing) {
                ((Thing) actor).draw(GalacticTraitors.getBatch());
            }
        }
    }

    @Override
    public void addActor(Actor actor) {
        actors.add(actor);
    }

    @Override
    public void removeActor(Actor actor) {
        actors.remove(actor);
    }

    @Override
    public boolean hasActor(Actor actor) {
        return actors.contains(actor);
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        getDefaultCamera().setToOrtho(false, 5 * aspectRatio, 5);
        inventoryBar.setPoint(getBotCorner().add(new Point(getWidth() - inventoryBar.getWidth() / 2, inventoryBar.getHeight() / 2)));
    }

    @Override
    public BetterCamera getDefaultCamera() {
        return camera;
    }

    @Override
    public Point getBotCorner() {
        return new Point(camera.position.x - getWidth() / 2, camera.position.y - getHeight() / 2);
    }

    @Override
    public float getWidth() {
        return camera.viewportWidth;
    }

    @Override
    public float getHeight() {
        return camera.viewportHeight;
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

    public InventoryBar getInventoryBar() {
        return inventoryBar;
    }
}
