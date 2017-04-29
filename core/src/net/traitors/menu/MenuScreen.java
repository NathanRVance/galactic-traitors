package net.traitors.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import net.traitors.GalacticTraitors;
import net.traitors.GameScreen;
import net.traitors.Layer;
import net.traitors.LayerLayer;
import net.traitors.util.Point;

public class MenuScreen implements Screen {

    private final GalacticTraitors game;
    private Layer menuLayer;

    public MenuScreen(GalacticTraitors game) {
        this.game = game;
        GalacticTraitors.getCamera().setToOrtho(false);
        menuLayer = new LayerLayer(GalacticTraitors.getCamera());
    }

    @Override
    public void show() {
        Menu menu = new Menu.MenuBuilder(3f).addButton("Start Game", new Runnable() {
            @Override
            public void run() {
                game.setScreen(new GameScreen());
            }
        }).build(menuLayer, "Galactic Traitors");
        menu.setPoint(new Point(2, 2));
        menuLayer.addActor(menu);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GalacticTraitors.getCamera().update();
        GalacticTraitors.getBatch().setProjectionMatrix(menuLayer.getDefaultCamera().combined);

        GalacticTraitors.getBatch().begin();
        menuLayer.draw();
        GalacticTraitors.getBatch().end();

        GalacticTraitors.getTextView().draw();
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
