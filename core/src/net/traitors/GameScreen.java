package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.traitors.thing.Stuff;
import net.traitors.thing.platform.Platform;
import net.traitors.thing.platform.Ship;
import net.traitors.thing.platform.ShipFactory;
import net.traitors.thing.platform.TileGrid;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.player.Player;
import net.traitors.ui.TextView;
import net.traitors.ui.TouchControls;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;

public class GameScreen implements Screen {
    private static Stuff stuff;
    private GalacticTraitors game;
    private Stage uiControls;
    private TextView textView;

    GameScreen(GalacticTraitors game) {
        this.game = game;
        BetterCamera camera = new BetterCamera();
        Player player = new Player(Color.GREEN, new Color(0xdd8f4fff), Color.BROWN, Color.BLUE, Color.BLACK);
        stuff = new Stuff(camera, player);

        Ship ship = new ShipFactory().buildStandardShip();
        ship.setPoint(new Point(-5, 10));
        stuff.addActor(ship);

        Platform world = new UniverseTile();
        world.setPoint(new Point(0, 0));
        stuff.addActor(world);

        TileGrid t = new TileGrid(50, 2);
        t.setPoint(new Point(0, -20));
        t.setRotationalVelocity(-.3f);
        stuff.addActor(t);

        TileGrid tiles = new TileGrid(4, 3);
        tiles.setPoint(new Point(1, 2));
        tiles.setRotationalVelocity(-1);
        stuff.addActor(tiles);

        TileGrid moreTiles = new TileGrid(3, 3);
        moreTiles.setPoint(new Point(1, 1));
        moreTiles.setRotationalVelocity(-1);
        stuff.addActor(moreTiles);

        tiles = new TileGrid(1, 1);
        tiles.setPoint(new Point(1, 2));
        tiles.setRotationalVelocity(1);
        stuff.addActor(tiles);

        player.setPoint(new Point(-2, 2));
        player.setPlatform(moreTiles);
        stuff.addActor(camera); //must happen before player
        stuff.addActor(player);

        uiControls = new TouchControls(player, camera);
        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new net.traitors.controls.Input(camera)));
    }

    public static Stuff getStuff() {
        return stuff;
    }

    @Override
    public void render(float delta) {
        doMoves(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stuff.getCamera().combined);
        game.batch.begin();
        //textView.drawStringInWorld(camera, game.font, "Num Taps: " + numTaps, new Point(1, 1), TextView.Align.left, 1);
        stuff.drawStuff(game.batch);
        game.batch.end();

        textView.draw();
        uiControls.draw();
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
        uiControls = new TouchControls(stuff.getPlayer(), stuff.getCamera());
        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new net.traitors.controls.Input(stuff.getCamera())));

        textView = new TextView();
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
