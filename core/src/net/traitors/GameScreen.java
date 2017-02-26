package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.controls.InputProcessor;
import net.traitors.thing.Stuff;
import net.traitors.thing.platform.Platform;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.platform.ship.ShipFactory;
import net.traitors.ui.TouchControls;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.net.MultiplayerConnect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameScreen implements Screen {
    private static Stuff stuff;
    private static byte[] serializedStuff;
    private static TouchControls uiControls;
    private GalacticTraitors game;
    //private TextView textView;

    GameScreen(GalacticTraitors game) {
        this.game = game;
        BetterCamera camera = new BetterCamera();
        uiControls = new TouchControls();
        stuff = new Stuff(camera);

        Ship ship = new ShipFactory().buildStandardShip();
        ship.setPoint(new Point(-5, 10));
        stuff.addActor(ship);

        Platform world = new UniverseTile();
        world.setPoint(new Point(0, 0));
        stuff.addActor(world);

        ship = new ShipFactory().buildTileGrid(50, 2);
        ship.setPoint(new Point(0, -20));
        //ship.setRotationalVelocity(-.3f);
        stuff.addActor(ship);

        ship = new ShipFactory().buildTileGrid(4, 3);
        ship.setPoint(new Point(1, 2));
        ship.setRotationalVelocity(-1);
        stuff.addActor(ship);

        ship = new ShipFactory().buildTileGrid(3, 3);
        ship.setPoint(new Point(1, 1));
        ship.setRotationalVelocity(-1);
        stuff.addActor(ship);

        ship = new ShipFactory().buildTileGrid(1, 1);
        ship.setPoint(new Point(1, 2));
        ship.setRotationalVelocity(1);
        stuff.addActor(ship);

        stuff.getPlayer().setPoint(new Point(-4, 10));
        stuff.addActor(camera);

        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new InputProcessor(camera)));

        //MultiplayerConnect.makeServer();
        //MultiplayerConnect.connectToServer("209.140.230.243");
        //MultiplayerConnect.start();
    }

    public static Stuff getStuff() {
        return stuff;
    }

    public static synchronized byte[] serializeStuff() {
        return serializedStuff;
    }

    public static synchronized void deserializeStuff(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream si = new ObjectInputStream(bi);
        GameScreen.stuff = (Stuff) si.readObject();
    }

    public static TouchControls getTouchControls() {
        return uiControls;
    }

    private void setSerializedStuff() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(stuff);
            so.flush();
            serializedStuff = bo.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void render(float delta) {
        doMoves(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stuff.getCamera().combined);
        game.batch.begin();
        //textView.drawStringInWorld(camera, game.font, "Num Taps: " + numTaps, new Point(1, 1), TextView.Align.left, 1);
        stuff.drawStuff(game.batch, stuff.getCamera());
        game.batch.end();

        //textView.draw();
        uiControls.draw();
        //setSerializedStuff();
        MultiplayerConnect.tick(delta);
    }

    private void doMoves(float delta) {
        uiControls.act();
        stuff.doStuff(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        stuff.getCamera().setToOrtho(false, 5 * aspectRatio, 5);
        uiControls = new TouchControls();
        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new InputProcessor(stuff.getCamera())));
        stuff.getPlayer().getInventory().update();
        //textView = new TextView();
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
        uiControls.dispose();
    }
}
