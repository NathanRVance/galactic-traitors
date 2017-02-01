package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.traitors.controls.Controls;
import net.traitors.thing.Actor;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.Platform;
import net.traitors.thing.platform.TileGrid;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.player.Player;
import net.traitors.ui.TextView;
import net.traitors.ui.TouchControls;
import net.traitors.util.BetterCamera;
import net.traitors.util.Overlapper;
import net.traitors.util.Point;
import net.traitors.util.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameScreen implements Screen {
    private Player player;
    private BetterCamera camera;
    private GalacticTraitors game;
    private int numTaps = 0;
    private Stage uiControls;
    private TextView textView;
    private List<Actor> actors = new ArrayList<Actor>();
    private List<Thing> stuff = new ArrayList<Thing>();

    GameScreen(GalacticTraitors game) {
        this.game = game;

        Platform world = new UniverseTile();
        world.setPoint(new Point(0, 0));
        stuff.add(world);

        TileGrid t = new TileGrid(50, 2);
        t.setPoint(new Point(0, -20));
        t.setRotationalVelocity(-.3f);
        stuff.add(t);

        TileGrid tiles = new TileGrid(4, 3);
        tiles.setPoint(new Point(1, 2));
        tiles.setRotationalVelocity(-1);
        stuff.add(tiles);

        TileGrid moreTiles = new TileGrid(3, 3);
        moreTiles.setPoint(new Point(1, 1));
        moreTiles.setRotationalVelocity(-1);
        stuff.add(moreTiles);

        tiles = new TileGrid(1, 1);
        tiles.setPoint(new Point(1, 2));
        tiles.setRotationalVelocity(1);
        stuff.add(tiles);

        player = new Player(Color.GREEN, new Color(0xdd8f4fff), Color.BROWN, Color.BLUE, Color.BLACK);
        player.setPoint(new Point(-2, 2));
        player.setPlatform(moreTiles);
        stuff.add(player);

        Collections.sort(stuff, new Comparator<Thing>() {
            @Override
            public int compare(Thing thing1, Thing thing2) {
                float surfaceArea1 = thing1.getHeight() * thing1.getWidth();
                float surfaceArea2 = thing2.getHeight() * thing2.getWidth();
                if (surfaceArea1 > surfaceArea2) return -1;
                if (surfaceArea1 < surfaceArea2) return 1;
                return 0;
            }
        });

        camera = new BetterCamera();
        player.setCamera(camera);
        uiControls = new TouchControls(player, camera);
        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new net.traitors.controls.Input(camera)));

        for(Thing thing : stuff) {
            actors.add(thing);
        }
        actors.remove(player);
        actors.add(camera);
        actors.add(player);
    }

    @Override
    public void render(float delta) {
        doMoves(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        //textView.drawStringInWorld(camera, game.font, "Num Taps: " + numTaps, new Point(1, 1), TextView.Align.left, 1);
        for (Thing thing : stuff) {
            thing.draw(game.batch);
        }
        game.batch.end();

        textView.draw();
        uiControls.draw();
    }

    private void doMoves(float delta) {
        uiControls.act();

        for (Actor actor : actors) {
            actor.act(delta);
        }

        for (TreeNode tree : Overlapper.getOverlaps(stuff)) {
            placeThings(null, tree);
        }

        List<Point> touchesInWorld = Controls.getWorldTouches(camera);
        if (!touchesInWorld.isEmpty()) {
            player.rotateToFace(player.convertToPlatformCoordinates(touchesInWorld.get(0)));
        }
        if (Gdx.input.justTouched()) {
            numTaps++;
        }

        Point playerWorldPoint = player.getWorldPoint();
        camera.translate(playerWorldPoint.x - camera.position.x, playerWorldPoint.y - camera.position.y);
        camera.rotateWith(player.getPlatform());
        //camera.act(delta);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
    }

    private void placeThings(Platform parent, TreeNode child) {
        child.getThing().setPlatform(parent);
        for (TreeNode treeNode : child.getChildren()) {
            placeThings((Platform) child.getThing(), treeNode);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        camera.setToOrtho(false, 5 * aspectRatio, 5);
        uiControls = new TouchControls(player, camera);
        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new net.traitors.controls.Input(camera)));

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
