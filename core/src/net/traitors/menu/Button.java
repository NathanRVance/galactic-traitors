package net.traitors.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.GalacticTraitors;
import net.traitors.controls.MouseoverCallback;
import net.traitors.thing.AbstractThing;
import net.traitors.ui.TextView;
import net.traitors.util.Point;

class Button extends AbstractThing implements Disposable, MouseoverCallback {

    private String text;
    private Runnable onClick;
    private boolean selected = false;
    private Texture unselectedTexture;
    private Texture selectedTexture;

    Button(float width, float height, String text, Runnable onClick) {
        super(width, height);
        this.text = text;
        this.onClick = onClick;
        unselectedTexture = makeTexture(Color.DARK_GRAY, Color.LIGHT_GRAY);
        selectedTexture = makeTexture(Color.CYAN, Color.LIGHT_GRAY);
    }

    private Texture makeTexture(Color border, Color center) {
        int width = (int) (getWidth() * 100);
        int height = (int) (getHeight() * 100);
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA4444);
        pixmap.setColor(border);
        pixmap.fill();

        pixmap.setColor(center);
        int edgeThickness = height / 10;
        pixmap.fillRectangle(edgeThickness, edgeThickness, width - edgeThickness * 2, height - edgeThickness * 2);

        return new Texture(pixmap);
    }

    @Override
    public void draw(Batch batch) {
        Point point = getWorldPoint();
        Texture button = (selected) ? selectedTexture : unselectedTexture;
        batch.draw(button, point.x - getWidth() / 2, point.y - getHeight() / 2, getWidth(), getHeight());
        GalacticTraitors.getTextView().drawStringInWorld(text, new Point(point.x, point.y + getHeight() / 4),
                TextView.Align.center, getWidth() * .8f, .3f, Color.BLACK);
    }

    @Override
    public void dispose() {
        unselectedTexture.dispose();
        selectedTexture.dispose();
        GalacticTraitors.getInputProcessor().removeCallback(this);
    }

    @Override
    public void mouseEnter() {
        selected = true;
    }

    @Override
    public void mouseExit() {
        selected = false;
    }

    @Override
    public void mouseDown() {
        //Do nothing
    }

    @Override
    public void mouseUp() {
        onClick.run();
    }
}
