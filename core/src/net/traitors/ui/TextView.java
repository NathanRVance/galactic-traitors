package net.traitors.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.traitors.GalacticTraitors;
import net.traitors.util.Point;

public class TextView extends Stage {

    private BitmapFont font;

    public TextView(BitmapFont font) {
        this.font = font;
        font.getData().setLineHeight(30);
    }

    /**
     * Draw a text where the location corresponds to the position on the screen, which is assumed
     * square and has dimensions 0-1.
     *
     * @param text   the string to be drawn
     * @param pos    coordinates, both x and y between 0 and 1
     * @param align  when center the text will be centered on x,y. When left, its left edge will be
     *               x,y. When right, its right edge will be on x,y.
     * @param width  between 0 and 1 (same units as x and y), will wrap text that extends beyond
     * @param height height of each character (not of string), 0 < height <= 1
     */
    public void drawStringOnScreen(CharSequence text, Point pos, Align align, float width, float height, Color color) {
        pos = new Point(pos.x * getWidth(), pos.y * getHeight());
        width *= getWidth() / GalacticTraitors.getCamera().zoom;
        height *= getHeight() / 40 / GalacticTraitors.getCamera().zoom;
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
        DrawText t = new DrawText(font, text, pos, align, width, height, color);
        addActor(t);
    }

    /**
     * Draws text in the world. Locations correspond to world locations
     *
     * @param text   the string to be drawn
     * @param pos    coordinates in world units
     * @param align  when center the text will be centered on x,y. When left, its right edge will be
     *               x,y. When right, its left edge will be on x,y.
     * @param width  width of string, in world units. Will wrap text that extends beyond
     * @param height height of each character (not of string)
     */
    public void drawStringInWorld(CharSequence text, Point pos, Align align, float width, float height, Color color) {
        pos = pos.project(GalacticTraitors.getCamera());
        pos = new Point(pos.x / getWidth(), pos.y / getHeight());
        width /= GalacticTraitors.getCamera().viewportWidth;
        height /= GalacticTraitors.getCamera().viewportHeight;
        drawStringOnScreen(text, pos, align, width, height, color);
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
        private float height;
        private Color color;

        DrawText(BitmapFont font, CharSequence text, Point point, Align align, float width, float height, Color color) {
            this.font = font;
            this.text = text;
            this.point = point;
            this.align = align;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            font.setColor(color);
            font.getData().setScale(height);
            font.draw(batch, text, point.x, point.y, width, align.value, true);
        }

    }

}
