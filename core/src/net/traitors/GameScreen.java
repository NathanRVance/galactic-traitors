package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.controls.Controls;
import net.traitors.controls.InputProcessor;
import net.traitors.thing.player.Player;
import net.traitors.util.Point;
import net.traitors.util.net.MultiplayerConnect;
import net.traitors.util.save.SaveData;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private GameFactory gameFactory;
    private List<Layer> layers = new ArrayList<>();
    private boolean mainPlayerNotAdded = true;
    private int playersToAdd = 1; //Start by adding a player

    GameScreen(GameFactory gameFactory) {
        this.gameFactory = gameFactory;

        GalacticTraitors.getInputProcessor().addProcessor(new InputProcessor() {
            @Override
            public boolean scrolled(int amount) {
                GalacticTraitors.getCamera().zoom *= 1 + amount * .1;
                return false;
            }
        });

        MultiplayerConnect.start(this);
    }

    void addLayer(Layer layer) {
        layers.add(layer);
    }

    public void addPlayer() {
        playersToAdd++;
    }

    public void setPlayerID(long playerID) {
        Controls.setID(playerID);
    }

    @Override
    public synchronized void render(float delta) {
        doMoves(delta);

        GalacticTraitors.getInputProcessor().bump();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GalacticTraitors.getBatch().begin();
        for (Layer layer : layers) {
            GalacticTraitors.getBatch().setProjectionMatrix(layer.getDefaultCamera().combined);
            layer.draw();
        }
        GalacticTraitors.getBatch().end();

        GalacticTraitors.getTextView().draw();
        MultiplayerConnect.tick(delta);

        //Test stuff
        SaveData testSaveData = layers.get(0).getSaveData();
        layers.get(0).loadSaveData(testSaveData);
    }

    private void doMoves(float delta) {
        while (playersToAdd-- > 0) {
            System.out.println("Adding player");
            Player p = gameFactory.makePlayer(mainPlayerNotAdded);
            if (mainPlayerNotAdded) {
                setPlayerID(p.getID());
                mainPlayerNotAdded = false;
            }
            p.setPoint(new Point(-4, 10));
        }

        Controls.update();

        for (Layer layer : layers) {
            layer.act(delta);
        }

        GalacticTraitors.getCamera().act(delta);

        //TODO: Implement me
        /*if (MultiplayerConnect.isServer())
            updateSaveData();
        if (MultiplayerConnect.isClient())
            resolveSavedData();*/
    }

    /**
     * Checks if we can send data to multiplayer clients
     *
     * @return true if sendable, false otherwise
     */
    public boolean isClean() {
        return false; // FIXME: 4/27/17
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
