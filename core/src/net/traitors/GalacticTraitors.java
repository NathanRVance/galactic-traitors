package net.traitors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.traitors.menu.MenuScreen;
import net.traitors.ui.TextView;
import net.traitors.util.TextureCreator;

public class GalacticTraitors extends Game {

    private SpriteBatch batch;
    private TextView textView;

    @Override
    public void create() {
        batch = new SpriteBatch();
        textView = new TextView(new BitmapFont(Gdx.files.internal("gamefont.fnt")));
        Pixmap cursor = TextureCreator.getCursor();
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, cursor.getHeight() / 2, cursor.getWidth() / 2));
        cursor.dispose();
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

    public SpriteBatch getBatch() {
        return batch;
    }

    public TextView getTextView() {
        return textView;
    }
}
