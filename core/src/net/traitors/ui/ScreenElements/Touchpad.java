package net.traitors.ui.ScreenElements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.GalacticTraitors;
import net.traitors.Layer;
import net.traitors.controls.Controls;
import net.traitors.controls.MouseoverCallback;
import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;

/**
 * A touchpad already exists in libgdx. However, since multiple inheritance isn't a thing in
 * Java, I'm reinventing the wheel here.
 */

public class Touchpad extends AbstractThing implements MouseoverCallback {

    private static final float cutoffPercent = .25f;
    private Texture background;
    private Texture knob;
    private Point knobPos = new Point();

    public Touchpad(Layer layer, float dim) {
        super(layer, dim, dim);

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        p.setColor(Color.FIREBRICK);
        p.fill();
        background = new Texture(p);

        p = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        p.setColor(Color.SLATE);
        p.fill();
        knob = new Texture(p);

        GalacticTraitors.getInputProcessor().addCallback(this);
    }

    @Override
    public void setPoint(Point point) {
        super.setPoint(point);
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(background, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2, getWidth(), getHeight());
        Point knobPos = this.knobPos.add(getWorldPoint());
        float knobWidth = getWidth() / 3;
        batch.draw(knob, knobPos.x - knobWidth / 2, knobPos.y - knobWidth / 2, knobWidth, knobWidth);
    }

    @Override
    public void dispose() {
        background.dispose();
        knob.dispose();
        GalacticTraitors.getInputProcessor().removeCallback(this);
    }

    @Override
    public void mouseEnter() {

    }

    @Override
    public void mouseExit() {

    }

    @Override
    public boolean mouseDown(Point touchLoc) {
        knobPos = touchLoc.subtract(getWorldPoint());
        if (knobPos.distanceFromZero() > getWidth() / 2) {
            knobPos = knobPos.scale(getWidth() / 2 / knobPos.distanceFromZero());
        }
        Point p = knobPos.scale(1 / (getWidth() / 2)); //p ranges from -1 to 1 (or more if touching outside of touchpad)
        pressKey(p.y, Controls.Key.UP, cutoffPercent);
        pressKey(-p.y, Controls.Key.DOWN, cutoffPercent);
        pressKey(p.x, Controls.Key.RIGHT, cutoffPercent);
        pressKey(-p.x, Controls.Key.LEFT, cutoffPercent);
        pressKey(p.distanceFromZero(), Controls.Key.SPRINT, .95f);
        return true;
    }

    private void pressKey(float position, Controls.Key key, float cutoffPercent) {
        if (position > cutoffPercent) Controls.keyPressed(key);
        else Controls.keyReleased(key);
    }

    @Override
    public boolean mouseDragged(Point touchLoc) {
        return mouseDown(touchLoc);
    }

    @Override
    public boolean mouseUp() {
        knobPos = new Point();
        for(Controls.Key key : Controls.Key.values()) {
            Controls.keyReleased(key);
        }
        return false;
    }
}
