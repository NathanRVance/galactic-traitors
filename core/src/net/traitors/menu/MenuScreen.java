package net.traitors.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.traitors.GalacticTraitors;
import net.traitors.GameScreen;
import net.traitors.ui.TextView;
import net.traitors.util.Point;

public class MenuScreen implements Screen {

    private final GalacticTraitors game;
    private OrthographicCamera camera;

    public MenuScreen(GalacticTraitors game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        game.getTextView().setCamera(camera);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        game.getBatch().end();

        game.getTextView().drawStringOnScreen("Welcome to Galactic Traitors!", new Point(.5f, .5f), TextView.Align.center, .5f);
        game.getTextView().draw();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        camera.setToOrtho(false, 5 * aspectRatio, 5);
        game.resize();
        game.getTextView().setCamera(camera);
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

    }
}
