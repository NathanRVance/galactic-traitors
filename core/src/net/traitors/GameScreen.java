package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.controls.InputProcessor;
import net.traitors.thing.Actor;
import net.traitors.thing.player.Player;
import net.traitors.util.net.MultiplayerConnect;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private static WorldLayer worldLayer;
    private static ScreenLayer uiControls;

    private static List<Layer> layers = new ArrayList<>();

    public GameScreen() {
        uiControls = NewGame.getScreenLayer();
        layers.add(uiControls);
        worldLayer = NewGame.getWorldLayer();
        layers.add(worldLayer);

        GalacticTraitors.getInputProcessor().addProcessor(new InputProcessor() {
            @Override
            public boolean scrolled(int amount) {
                GalacticTraitors.getCamera().zoom *= 1 + amount * .1;
                return false;
            }
        });

        MultiplayerConnect.start();
    }

    public static void removeActor(Actor actor) {
        for (Layer layer : layers) {
            layer.removeActor(actor);
        }
    }

    public static WorldLayer getWorldLayer() {
        return worldLayer;
    }

    public static ScreenLayer getTouchControls() {
        return uiControls;
    }

    public static Player getPlayer() {
        return getWorldLayer().getPlayer();
    }

    @Override
    public synchronized void render(float delta) {
        doMoves(delta);

        GalacticTraitors.getInputProcessor().bump();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GalacticTraitors.getBatch().begin();
        GalacticTraitors.getBatch().setProjectionMatrix(worldLayer.getDefaultCamera().combined);
        worldLayer.draw();
        GalacticTraitors.getBatch().setProjectionMatrix(uiControls.getDefaultCamera().combined);
        uiControls.draw();
        GalacticTraitors.getBatch().end();

        GalacticTraitors.getTextView().draw();
        MultiplayerConnect.tick(delta);
    }

    private void doMoves(float delta) {
        uiControls.act(delta);
        worldLayer.act(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        GalacticTraitors.getCamera().setToOrtho(false, 5 * aspectRatio, 5);
        for (Layer layer : layers) {
            layer.resize(width, height);
        }
        worldLayer.getPlayer().getInventory().update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        for (Layer layer : layers) {
            layer.dispose();
        }
    }
}
