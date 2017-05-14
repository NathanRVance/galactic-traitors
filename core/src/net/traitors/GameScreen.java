package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.controls.Controls;
import net.traitors.controls.InputProcessor;
import net.traitors.thing.player.Player;
import net.traitors.util.Point;
import net.traitors.util.net.MultiplayerConnect;
import net.traitors.util.net.MultiplayerSocket;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private GameFactory gameFactory;
    private final List<Layer> layers = new ArrayList<>();
    private MultiplayerSocket socket;

    GameScreen(GameFactory gameFactory) {
        this.gameFactory = gameFactory;

        GalacticTraitors.getInputProcessor().addProcessor(new InputProcessor() {
            @Override
            public boolean scrolled(int amount) {
                GalacticTraitors.getCamera().zoom *= 1 + amount * .1;
                return false;
            }
        });

        socket = MultiplayerConnect.start(this, gameFactory);

        addPlayer(true);
    }

    void addLayer(Layer layer) {
        layers.add(layer);
    }

    public long addPlayer(boolean isMain) {
        Player p;
        synchronized (layers) {
            p = gameFactory.makePlayer(isMain);
        }
        if (isMain) {
            Controls.setPlayerID(p.getID());
        }
        p.setPoint(new Point(-4, 10));
        return p.getID();
    }

    @Override
    public synchronized void render(float delta) {
        synchronized (layers) {
            doMoves(delta);
        }

        GalacticTraitors.getInputProcessor().bump();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GalacticTraitors.getBatch().begin();
        synchronized (layers) {
            for (Layer layer : layers) {
                GalacticTraitors.getBatch().setProjectionMatrix(layer.getDefaultCamera().combined);
                layer.draw();
            }
        }
        GalacticTraitors.getBatch().end();
        GalacticTraitors.getTextView().draw();

        socket.send();
        socket.receive();
    }

    private void doMoves(float delta) {
        Controls.update();

        for (Layer layer : layers) {
            layer.act(delta);
        }

        GalacticTraitors.getCamera().act(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        GalacticTraitors.resize();
        for (Layer layer : layers) {
            layer.resize(width, height);
        }
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
