package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.controls.InputProcessor;
import net.traitors.thing.Stuff;
import net.traitors.thing.platform.Platform;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.platform.ship.ShipFactory;
import net.traitors.ui.TouchControls;
import net.traitors.util.Point;
import net.traitors.util.net.MultiplayerConnect;

public class GameScreen implements Screen {
    private static Stuff stuff;
    private static TouchControls uiControls;
    //private TextView textView;

    public GameScreen() {
        uiControls = new TouchControls();
        stuff = new Stuff();

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
        stuff.addActor(GalacticTraitors.getCamera());

        GalacticTraitors.getInputProcessor().addProcessor(new InputProcessor() {
            @Override
            public boolean scrolled(int amount) {
                GalacticTraitors.getCamera().zoom *= 1 + amount * .1;
                return false;
            }
        });
        GalacticTraitors.getInputProcessor().addProcessor(uiControls);

        MultiplayerConnect.start();
    }

    public static Stuff getStuff() {
        return stuff;
    }

    public static TouchControls getTouchControls() {
        return uiControls;
    }

    @Override
    public synchronized void render(float delta) {
        doMoves(delta);

        GalacticTraitors.getInputProcessor().bump();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GalacticTraitors.getBatch().setProjectionMatrix(GalacticTraitors.getCamera().combined);
        GalacticTraitors.getBatch().begin();
        //textView.drawStringInWorld(camera, game.font, "Num Taps: " + numTaps, new Point(1, 1), TextView.Align.left, 1);
        stuff.drawStuff(GalacticTraitors.getBatch(), GalacticTraitors.getCamera());
        GalacticTraitors.getBatch().end();

        GalacticTraitors.getTextView().draw();
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
        GalacticTraitors.getCamera().setToOrtho(false, 5 * aspectRatio, 5);
        uiControls = new TouchControls();
        //Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new InputProcessor(stuff.getCamera())));
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
