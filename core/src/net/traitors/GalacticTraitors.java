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
        textView = new TextView(new BitmapFont(Gdx.files.internal("gamefont.fnt"), Gdx.files.internal("gamefont.png"), false));
        Pixmap cursor = TextureCreator.getCursor();
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, cursor.getHeight() / 2, cursor.getWidth() / 2));
        cursor.dispose();
        camera = new BetterCamera();
        inputProcessor = new InputMultiprocessor(camera);
        Gdx.input.setInputProcessor(inputProcessor);
        setScreen(new MenuScreen(this));
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

    public static void resize() {
        float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        GalacticTraitors.getCamera().setToOrtho(false, 5 * aspectRatio, 5);

        textView.dispose();
        textView = new TextView(new BitmapFont(Gdx.files.internal("gamefont.fnt"), Gdx.files.internal("gamefont.png"), false));
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
