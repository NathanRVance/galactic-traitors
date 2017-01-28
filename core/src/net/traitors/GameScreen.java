package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.traitors.player.Player;
import net.traitors.tile.TileGrid;
import net.traitors.ui.TextView;
import net.traitors.ui.TouchControls;
import net.traitors.util.AbstractDrawable;
import net.traitors.util.Controls;
import net.traitors.util.Point;

import java.util.List;

public class GameScreen implements Screen {
    private static Player player;
    private AbstractDrawable tiles;
    private OrthographicCamera camera;
    private GalacticTraitors game;
    private int numTaps = 0;
    private Stage uiControls;
    private TextView textView;

    GameScreen(GalacticTraitors game) {
        this.game = game;
        player = new Player(Color.GREEN, new Color(0xdd8f4fff), Color.BROWN, Color.BLUE, Color.BLACK);
        player.getPoint().x = 2;
        player.getPoint().y = 2;
        tiles = new TileGrid(30, 20);
        tiles.getPoint().x = 1;
        tiles.getPoint().y = 1;
        camera = new OrthographicCamera();
        uiControls = new TouchControls();
        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new Input(camera)));
    }

    public static Player getPlayer() {
        return player;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Happens first to catch all input this render cycle
        uiControls.act();

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        textView.drawStringInWorld(camera, game.font, "Num Taps: " + numTaps, new Point(1, 1), TextView.Align.left, 1);
        tiles.draw(game.batch);
        player.move(delta);
        camera.translate(player.getPoint().x - camera.position.x, player.getPoint().y - camera.position.y);
        List<Point> touchesInWorld = Controls.getWorldTouches(camera);
        if (!touchesInWorld.isEmpty()) {
            player.rotateToFace(touchesInWorld.get(0));
        }
        if (Gdx.input.justTouched()) {
            numTaps++;
        }
        player.draw(game.batch);
        game.batch.end();
        textView.draw();
        uiControls.draw();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        camera.setToOrtho(false, 5 * aspectRatio, 5);
        uiControls = new TouchControls();
        Gdx.input.setInputProcessor(new InputMultiplexer(uiControls, new Input(camera)));

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
        tiles.dispose();
        player.dispose();
        uiControls.dispose();
    }
}
