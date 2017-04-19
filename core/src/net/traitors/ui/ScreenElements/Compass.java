package net.traitors.ui.ScreenElements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GalacticTraitors;
import net.traitors.GameScreen;
import net.traitors.Layer;
import net.traitors.controls.MouseoverCallback;
import net.traitors.thing.AbstractThing;
import net.traitors.util.BetterCamera;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;

public class Compass extends AbstractThing implements Selectable, MouseoverCallback {

    private static TextureRegion compassSelected;
    private static TextureRegion compassUnselected;
    private boolean selected = false;
    private Texture needle;
    private int trackDepth;
    private SelectableSwitch<Compass> selectableSwitch;

    //Variables for dragging
    private float startX = 0;
    private boolean doingDrag = false;

    public Compass(Layer layer, SelectableSwitch<Compass> selectableSwitch, float dim) {
        super(layer, dim, dim);
        this.selectableSwitch = selectableSwitch;
        if (compassSelected == null) {
            compassSelected = getCompass(Color.BLUE);
        }
        if (compassUnselected == null) {
            compassUnselected = getCompass(Color.GRAY);
        }
        needle = getNeedle();
    }

    @Override
    public void mouseEnter() {

    }

    @Override
    public void mouseExit() {

    }

    @Override
    public boolean mouseDown(Point touchLoc) {
        GalacticTraitors.getCamera().syncRotations();
        selectableSwitch.selectableTapped(this, true);
        startX = touchLoc.x;
        return true;
    }

    @Override
    public boolean mouseDragged(Point touchLoc) {
        float currentX = touchLoc.x;
        if (Math.abs(startX - currentX) > getWidth() / 10) doingDrag = true;
        if (doingDrag) {
            GalacticTraitors.getCamera().setOffset((float) (currentX / getWidth() * Math.PI * 2 + Math.PI));
        }
        return false;
    }

    @Override
    public boolean mouseUp() {
        doingDrag = false;
        return false;
    }

    void setTrackDepth(int trackDepth) {
        this.trackDepth = trackDepth;
        if (GalacticTraitors.getCamera().getRotateDepth() == trackDepth || isSelected())
            select();
    }

    @Override
    public void draw(Batch batch) {
        TextureRegion compass = (isSelected()) ? compassSelected : compassUnselected;
        BetterCamera camera = GameScreen.getWorldLayer().getDefaultCamera();
        batch.draw(compass, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2,
                getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1,
                (-camera.getCameraAngle() + camera.getThingAtDepth(trackDepth).getWorldRotation()) * MathUtils.radiansToDegrees);


        float needleWidth = getWidth() / 25;
        batch.draw(needle, getWorldPoint().x - needleWidth / 2, getWorldPoint().y, needleWidth, getHeight() / 3);
    }

    @Override
    public void dispose() {
        compassSelected.getTexture().dispose();
        compassUnselected.getTexture().dispose();
        needle.dispose();
    }

    @Override
    public void select() {
        GameScreen.getWorldLayer().getDefaultCamera().setRotateDepth(trackDepth);
        selected = true;
    }

    @Override
    public void unselect() {
        selected = false;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    private TextureRegion getCompass(Color ringColor) {
        int dim = 400;
        int radius = dim / 2;
        PixmapRotateRec pixmap = new PixmapRotateRec(dim, dim, Pixmap.Format.RGBA4444);
        pixmap.setColor(ringColor);
        pixmap.fillCircle(radius, radius, radius);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(radius, radius, radius * 4 / 5);
        //Color for letters
        pixmap.setColor(Color.BLACK);

        //Draw N
        int height = dim / 5;
        int width = height;
        int thickness = width / 4;
        int x = dim / 2 - width / 2;
        int y = dim * 7 / 8 - height;
        //Vertical bar
        pixmap.fillRectangle(x, y, thickness, height);
        //Crossbar
        pixmap.fillQuadrahedron(x, y + height - thickness, x, y + height,
                x + width, y + thickness, x + width, y);
        //Other bar
        pixmap.fillRectangle(x + width - thickness, y, thickness, height);

        //Draw S
        //To do so, we will plot 3x(x + .9)(x - .9) from -1 to 1 sideways
        y = dim / 8;
        for (float pos = 0; pos < height; pos++) {
            float funcIn = pos / height * 2 - 1;
            float funcOut = 3 * funcIn * (funcIn + .9f) * (funcIn - .9f);
            float drawStart = (funcOut / 2 + .5f) * width;
            pixmap.drawLine((int) drawStart + x - thickness / 2, (int) pos + y,
                    (int) drawStart + x + thickness / 2, (int) pos + y);
        }

        //Draw E
        x = dim * 7 / 8 - width;
        y = dim / 2 - height / 2;
        //Bottom horizontal bar
        pixmap.fillRectangle(x, y, width, thickness);
        //Middle horizontal bar
        pixmap.fillRectangle(x, y + height / 2 - thickness / 2, width, thickness);
        //Top vertical bar
        pixmap.fillRectangle(x, y + height - thickness, width, thickness);
        //Left vertical bar
        pixmap.fillRectangle(x, y, thickness, height);

        //Draw W
        x = dim / 8;
        //thickness *= 2;
        //Left to right, slanty bars
        int xEnd = x + width / 4;
        for (int i = 0; i < 2; i++) {
            pixmap.fillQuadrahedron(x, y + height, x + thickness, y + height, xEnd + thickness, y, xEnd, y);
            x = xEnd;
            xEnd = x + width / 4;
            pixmap.fillQuadrahedron(x, y, x + thickness, y, xEnd + thickness, y + height, xEnd, y + height);
            x = xEnd;
            xEnd = x + width / 4;
        }
        return new TextureRegion(new Texture(flipVert(pixmap)));
    }

    private Pixmap flipVert(Pixmap pixmap) {
        Pixmap ret = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                ret.drawPixel(x, y, pixmap.getPixel(x, pixmap.getHeight() - y));
            }
        }
        return ret;
    }

    private Texture getNeedle() {
        int width = 10;
        int height = 100;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        return new Texture(pixmap);
    }
}
