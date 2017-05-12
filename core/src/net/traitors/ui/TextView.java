package net.traitors.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import net.traitors.GalacticTraitors;
import net.traitors.Layer;
import net.traitors.util.Point;

public class TextView extends Stage {

    private BitmapFont font;
    private SpriteBatch spriteFont = new SpriteBatch();

    public TextView(BitmapFont font) {
        super(new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()),
                new SpriteBatch());
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
     * @param rot    rotation of string on screen
     */
    public void drawStringOnScreen(CharSequence text, Point pos, Align align, float width, float height, Color color, float rot) {
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
        DrawText t = new DrawText(font, text, pos, align, width, height, color, rot);
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
     * @param rot    world rotation of string
     * @param layer  the layer this string is drawn on
     */
    public void drawStringInWorld(CharSequence text, Point pos, Align align, float width, float height, Color color, float rot, Layer layer) {
        pos = pos.project(layer.getDefaultCamera());
        pos = new Point(pos.x / getWidth(), pos.y / getHeight());
        width /= layer.getDefaultCamera().viewportWidth;
        height /= layer.getDefaultCamera().viewportHeight;
        drawStringOnScreen(text, pos, align, width, height, color, rot - layer.getDefaultCamera().getCameraAngle() );
    }

    @Override
    public void draw() {
        spriteFont.begin();
        super.draw();
        getActors().clear();
        spriteFont.end();
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
        private float rot;

        DrawText(BitmapFont font, CharSequence text, Point point, Align align, float width, float height, Color color, float rot) {
            this.font = font;
            this.text = text;
            this.point = point;
            this.align = align;
            this.width = width;
            this.height = height;
            this.color = color;
            this.rot = rot;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            font.setColor(color);
            font.getData().setScale(height);
            Matrix4 fontRot = new Matrix4();
            fontRot.setToRotation(new Vector3(0, 0, 1), rot * MathUtils.radiansToDegrees)
                    .trn(point.x + width / 2, point.y, 0);
            batch.setTransformMatrix(fontRot);
            font.draw(batch, text, -width / 2, 0, width, align.value, true);
        }

    }

}
