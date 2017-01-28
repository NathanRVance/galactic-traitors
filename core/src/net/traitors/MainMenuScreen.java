package net.traitors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.traitors.ui.TextView;
import net.traitors.util.Point;


class MainMenuScreen implements Screen {

    private final GalacticTraitors game;
    private OrthographicCamera camera;
    private TextView textView;

    MainMenuScreen(GalacticTraitors game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.end();

        textView.drawStringOnScreen(game.font, "Welcome to Galactic Traitors!", new Point(.5f, .5f), TextView.Align.center, .5f);
        textView.draw();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        camera.setToOrtho(false, 5 * aspectRatio, 5);
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

    }
}
