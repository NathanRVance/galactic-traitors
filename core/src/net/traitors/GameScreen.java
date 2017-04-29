package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.controls.Controls;
import net.traitors.controls.InputProcessor;
import net.traitors.thing.Actor;
import net.traitors.thing.platform.NullPlatform;
import net.traitors.thing.player.Player;
import net.traitors.ui.ScreenElements.InventoryBar;
import net.traitors.util.Point;
import net.traitors.util.net.MultiplayerConnect;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private Layer worldLayer;
    private ScreenLayer uiControls;
    private static List<Layer> layers = new ArrayList<>();
    private static List<Controls.UserInput> inputs = new ArrayList<>();
    private static List<Player> players = new ArrayList<>();
    private static int playerID = 0;
    private int playersToAdd = 1; //Start by adding a player

    public GameScreen() {
        worldLayer = NewGame.getWorldLayer();
        layers.add(worldLayer);
        uiControls = NewGame.getScreenLayer();
        layers.add(uiControls);

        GalacticTraitors.getInputProcessor().addProcessor(new InputProcessor() {
            @Override
            public boolean scrolled(int amount) {
                GalacticTraitors.getCamera().zoom *= 1 + amount * .1;
                return false;
            }
        });

        MultiplayerConnect.start(this);
    }

    public static void removeActor(Actor actor) {
        for (Layer layer : layers) {
            layer.removeActor(actor);
        }
        if (actor instanceof Player && players.contains(actor)) {
            players.remove(actor);
        }
    }

    public void addPlayer() {
        playersToAdd++;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public static Player getPlayer() {
        int pid = playerID;
        return players.size() < pid ? players.get(pid) : players.get(players.size() - 1);
    }

    public void updateInputs(List<Controls.UserInput> in) {
        for (int i = 0; i < in.size(); i++) {
            inputs.set(i, in.get(i));
        }
    }

    @Override
    public synchronized void render(float delta) {
        doMoves(delta);

        GalacticTraitors.getInputProcessor().bump();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GalacticTraitors.getBatch().begin();
        for(Layer layer : layers) {
            GalacticTraitors.getBatch().setProjectionMatrix(layer.getDefaultCamera().combined);
            layer.draw();
        }
        GalacticTraitors.getBatch().end();

        GalacticTraitors.getTextView().draw();
        MultiplayerConnect.tick(delta);
    }

    private void doMoves(float delta) {
        while (playersToAdd-- > 0) {
            System.out.println("Adding player");
            InventoryBar bar = (players.size() == 0)? uiControls.getInventoryBar() : null;
            Player p = new Player(worldLayer, Color.GREEN, new Color(0xdd8f4fff), Color.BROWN, Color.BLUE, Color.BLACK, bar);
            worldLayer.addActor(p);
            players.add(p);
            inputs.add(new Controls.UserInput());
            p.setPoint(new Point(-4, 10));
        }

        for(Layer layer : layers) {
            layer.act(delta);
        }

        inputs.set(playerID, Controls.getUserInput());
        for (int i = 0; i < players.size(); i++) {
            players.get(i).move(delta, inputs.get(i));
        }

        Point playerWorldPoint = getPlayer().getWorldPoint();
        GalacticTraitors.getCamera().translate(playerWorldPoint.x - GalacticTraitors.getCamera().position.x,
                playerWorldPoint.y - GalacticTraitors.getCamera().position.y);
        if (GalacticTraitors.getCamera().getRotatingWith() instanceof NullPlatform)
            GalacticTraitors.getCamera().setRotateDepth(1);
        GalacticTraitors.getCamera().update();

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
    public static boolean isClean() {
        return false; // FIXME: 4/27/17
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
        GalacticTraitors.resize();
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
