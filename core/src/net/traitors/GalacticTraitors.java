package net.traitors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.traitors.controls.InputMultiprocessor;
import net.traitors.menu.MenuScreen;
import net.traitors.ui.TextView;
import net.traitors.util.BetterCamera;
import net.traitors.util.TextureCreator;

public class GalacticTraitors extends Game {

    private static SpriteBatch batch;
    private static TextView textView;
    private static BetterCamera camera;
    private static InputMultiprocessor inputProcessor;

    @Override
    public void create() {
        batch = new SpriteBatch();
        textView = new TextView(new BitmapFont(Gdx.files.internal("gamefont.fnt")));
        Pixmap cursor = TextureCreator.getCursor();
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, cursor.getHeight() / 2, cursor.getWidth() / 2));
        cursor.dispose();
        camera = new BetterCamera();
        inputProcessor = new InputMultiprocessor(camera);
        Gdx.input.setInputProcessor(inputProcessor);
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        textView.dispose();
    }

    public void resize() {
        textView.dispose();
        textView = new TextView(new BitmapFont(Gdx.files.internal("gamefont.fnt")));
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

    public static TextView getTextView() {
        return textView;
    }

    public static InputMultiprocessor getInputProcessor() {
        return inputProcessor;
    }

    public static BetterCamera getCamera() {
        return camera;
    }
}
