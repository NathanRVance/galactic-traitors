package net.traitors.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.GalacticTraitors;
import net.traitors.controls.MouseoverCallback;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.platform.Platform;
import net.traitors.ui.TextView;
import net.traitors.util.Point;

class Button extends AbstractThing implements Disposable, MouseoverCallback {

    private CharSequence text;
    private Runnable onClick;
    private boolean selected = false;
    private TextureRegion unselectedTexture;
    private TextureRegion selectedTexture;

    Button(float width, float height, CharSequence text, Runnable onClick) {
        super(width, height);
        this.text = text;
        this.onClick = onClick;
        unselectedTexture = makeTexture(Color.DARK_GRAY, Color.LIGHT_GRAY);
        selectedTexture = makeTexture(Color.CYAN, Color.LIGHT_GRAY);
    }

    private TextureRegion makeTexture(Color border, Color center) {
        int width = (int) (getWidth() * 100);
        int height = (int) (getHeight() * 100);
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA4444);
        pixmap.setColor(border);
        pixmap.fill();

        pixmap.setColor(center);
        int edgeThickness = width / 30;
        pixmap.fillRectangle(edgeThickness, edgeThickness, width - edgeThickness * 2, height - edgeThickness * 2);

        return new TextureRegion(new Texture(pixmap));
    }

    @Override
    public void setPlatform(Platform platform) {
        //Do nothing
    }

    void setPlatformPackagePrivate(Platform platform) {
        super.setPlatform(platform);
    }

    @Override
    public void draw(Batch batch) {
        Point point = getWorldPoint();
        float rotation = getWorldRotation();
        TextureRegion button = (selected) ? selectedTexture : unselectedTexture;
        batch.draw(button, point.x - getWidth() / 2, point.y - getHeight() / 2, getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(), 1, 1, rotation * MathUtils.radiansToDegrees);
        GalacticTraitors.getTextView().drawStringInWorld(text, new Point(0, getHeight() / 4).rotate(rotation).add(point),
                TextView.Align.center, getWidth() * .8f, .3f, Color.BLACK, getWorldRotation());
    }

    @Override
    public void dispose() {
        unselectedTexture.getTexture().dispose();
        selectedTexture.getTexture().dispose();
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
        onClick.run();
    }

    @Override
    public void mouseUp() {
        //Do nothing
    }
}
