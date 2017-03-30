package net.traitors.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.traitors.util.Point;

public class TextView extends Stage {

    private BitmapFont font;
    private Camera camera;

    public TextView(BitmapFont font) {
        this.font = font;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Draw a text where the location corresponds to the position on the screen, which is assumed
     * square and has dimensions 0-1.
     *
     * @param text  the string to be drawn
     * @param pos   coordinates, both x and y between 0 and 1
     * @param align when center the text will be centered on x,y. When left, its left edge will be
     *              x,y. When right, its right edge will be on x,y.
     * @param width between 0 and 1 (same units as x and y), will wrap text that extends beyond
     */
    public void drawStringOnScreen(CharSequence text, Point pos, Align align, float width) {
        pos = new Point(pos.x * getWidth(), pos.y * getHeight());
        width *= getWidth();
        switch (align) {
            case left:
                break;
            case right:
                pos = new Point(pos.x - width, pos.y);
                break;
            case center:
                pos = new Point(pos.x - width / 2, pos.y);
                break;

        }
        DrawText t = new DrawText(font, text, pos, align, width);
        addActor(t);
    }

    /**
     * Draws text in the world. Locations correspond to world locations
     *
     * @param text  the string to be drawn
     * @param pos   coordinates in world units
     * @param align when center the text will be centered on x,y. When left, its right edge will be
     *              x,y. When right, its left edge will be on x,y.
     * @param width between 0 and 1 (same units as x and y), will wrap text that extends beyond
     */
    public void drawStringInWorld(CharSequence text, Point pos, Align align, float width) {
        pos = pos.project(camera);
        pos = new Point(pos.x / getWidth(), pos.y / getHeight());

        drawStringOnScreen(text, pos, align, width);
    }

    @Override
    public void draw() {
        super.draw();
        getActors().clear();
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public enum Align {
        left(-1),
        center(1),
        right(0);

        int value;
        Align(int value) {
            this.value = value;
        }
    }

    private static class DrawText extends Actor {
        private BitmapFont font;
        private CharSequence text;
        private Point point;
        private Align align;
        private float width;

        DrawText(BitmapFont font, CharSequence text, Point point, Align align, float width) {
            this.font = font;
            this.text = text;
            this.point = point;
            this.align = align;
            this.width = width;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            font.draw(batch, text, point.x, point.y, width, align.value, true);
        }

    }

}
