package net.traitors.ui.touchable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.traitors.thing.Thing;
import net.traitors.util.BetterCamera;
import net.traitors.util.PixmapRotateRec;

class Compass extends Widget implements Selectable {

    private BetterCamera camera;
    private boolean selected = false;
    private boolean touched = false;
    private TextureRegion compassSelected;
    private TextureRegion compassUnselected;
    private Texture needle;
    private Thing trackThing;

    //Variables for dragging
    private float startX = 0;
    private boolean doingDrag = false;

    Compass(final SelectableSwitch<Compass> selectableSwitch, final BetterCamera camera) {
        this.camera = camera;
        compassSelected = getCompass(Color.BLUE);
        compassUnselected = getCompass(Color.GRAY);
        needle = getNeedle();

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) return false;
                touched = true;
                camera.syncRotations();
                selectableSwitch.selectableTapped(Compass.this, true);
                startX = x;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(Math.abs(startX - x) > getWidth() / 10) doingDrag = true;
                if(doingDrag)
                    camera.setOffset((float) (x / getWidth() * Math.PI * 2 + Math.PI));
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                touched = false;
                doingDrag = false;
            }

        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);

        Thing cameraTracking = camera.getRotatingWith();
        camera.rotateWith(trackThing);
        TextureRegion compass = (isSelected())? compassSelected : compassUnselected;
        batch.draw(compass, getX(), getY(), getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, -camera.getOffset() * MathUtils.radiansToDegrees);
        camera.rotateWith(cameraTracking);

        float needleWidth = getWidth() / 25;
        batch.draw(needle, getX() + getWidth() / 2 - needleWidth / 2, getY() + getHeight() / 2, needleWidth, getHeight() / 3);
    }

    @Override
    public boolean isTouched() {
        return touched;
    }

    void setTrackThing(Thing trackThing) {
        this.trackThing = trackThing;
        if(camera.getRotatingWith() == trackThing) {
            select();
        } else if (isSelected()) {
            camera.rotateWith(trackThing);
        }
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

    @Override
    public void select() {
        camera.rotateWith(trackThing);
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
}
