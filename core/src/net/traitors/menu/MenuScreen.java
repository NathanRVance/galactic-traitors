package net.traitors.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.GalacticTraitors;
import net.traitors.GameScreen;
import net.traitors.ui.TextView;
import net.traitors.util.Point;

public class MenuScreen implements Screen {

    private final GalacticTraitors game;

    public MenuScreen(GalacticTraitors game) {
        this.game = game;
        GalacticTraitors.getCamera().setToOrtho(false);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GalacticTraitors.getCamera().update();
        GalacticTraitors.getBatch().setProjectionMatrix(GalacticTraitors.getCamera().combined);

        GalacticTraitors.getBatch().begin();
        GalacticTraitors.getBatch().end();

        GalacticTraitors.getTextView().drawStringOnScreen("Welcome to Galactic Traitors!", new Point(.5f, .5f),
                TextView.Align.center, .5f, .1f, Color.WHITE);
        GalacticTraitors.getTextView().draw();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen());
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        GalacticTraitors.getCamera().setToOrtho(false, 5 * aspectRatio, 5);
        game.resize();
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
