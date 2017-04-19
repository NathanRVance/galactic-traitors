package net.traitors;

import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Actor;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;

public interface Layer extends Disposable {

    void act(float delta);

    void draw();

    void draw(BetterCamera camera);

    void addActor(Actor actor);

    void removeActor(Actor actor);

    boolean hasActor(Actor actor);

    void resize(int width, int height);

    BetterCamera getDefaultCamera();

    Point getBotCorner();

    float getWidth();

    float getHeight();

    Point screenToLayerCoords(Point screenPoint);

}
