package net.traitors;

import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.save.Savable;

public interface Layer extends Disposable, Savable {

    void act(float delta);

    void draw();

    void draw(BetterCamera camera);

    void addActor(Actor actor);

    void removeActor(Actor actor);

    void resize(int width, int height);

    BetterCamera getDefaultCamera();

    Point getBotCorner();

    float getWidth();

    float getHeight();

    Point screenToLayerCoords(Point screenPoint);

    Thing getThingAt(Point point);

}
