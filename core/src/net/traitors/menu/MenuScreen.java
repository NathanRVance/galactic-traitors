package net.traitors.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.StringBuilder;

import net.traitors.GalacticTraitors;
import net.traitors.GameFactory;
import net.traitors.Layer;
import net.traitors.LayerLayer;
import net.traitors.controls.InputProcessor;
import net.traitors.thing.usable.StringStrategy;
import net.traitors.util.Point;
import net.traitors.util.net.MultiplayerConnect;

public class MenuScreen implements Screen {

    private final GalacticTraitors game;
    private Layer menuLayer;
    private EditText et;

    public MenuScreen(GalacticTraitors game) {
        this.game = game;
        GalacticTraitors.getCamera().setToOrtho(false);
        menuLayer = new LayerLayer(GalacticTraitors.getCamera());
    }

    @Override
    public void show() {

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
        GalacticTraitors.resize();
        menuLayer.resize(width, height);
        et = new EditText("Connect to IP");

        Menu menu = new Menu.MenuBuilder(3f)
                .addButton("Host Game", new Runnable() {
                    @Override
                    public void run() {
                        MultiplayerConnect.makeServer();
                        game.setScreen(new GameFactory().getGameScreen());
                    }
                })
                .addButton(et, new Runnable() {
                    @Override
                    public void run() {
                        et.setText("");
                        GalacticTraitors.getInputProcessor().addProcessor(new InputProcessor() {
                            @Override
                            public boolean keyTyped(char character) {
                                if (character == '\b') et.delete();
                                else if (character == '\r') {
                                    MultiplayerConnect.connectToServer(et.getContents());
                                    game.setScreen(new GameFactory().getGameScreen());
                                } else et.addChar(character);
                                return true;
                            }
                        });
                    }
                })
                .setCloseButtonText("Exit")
                .setCloseButtonAction(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                }).build(menuLayer, "Galactic Traitors");
        menu.setPoint(menuLayer.getBotCorner().add(new Point(menuLayer.getWidth() / 2, menuLayer.getHeight() / 2)));
        menuLayer.addActor(menu);
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

    private static class EditText extends StringStrategy {

        private StringBuilder sb;
        private boolean showingToolTip = true;

        EditText(String toolTip) {
            sb = new StringBuilder(toolTip);
        }

        void addChar(char ch) {
            sb.append(ch);
        }

        void delete() {
            sb.deleteCharAt(sb.length() - 1);
        }

        void setText(String text) {
            showingToolTip = false;
            sb = new StringBuilder(text);
        }

        @Override
        public String toString() {
            return sb.toString() + (! showingToolTip && (System.currentTimeMillis() / 750 )% 2 == 0? "|" : "");
        }

        String getContents() {
            return sb.toString();
        }
    }
}
