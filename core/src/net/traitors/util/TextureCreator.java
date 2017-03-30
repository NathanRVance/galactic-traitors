package net.traitors.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.Map;

public class TextureCreator {

    private static TextureRegion tileTexture;

    private static Map<Color, TextureRegion> colorRecs = new HashMap<>();

    public static TextureRegion getTileTexture() {
        if (tileTexture == null) {
            int width = 100;
            int height = 100;
            int edgeThickness = 2;
            Color edgeColor = Color.DARK_GRAY;
            int splashThickness = 4;
            Color splashColor = Color.CYAN;
            Color centerColor = Color.GRAY;
            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA4444);
            pixmap.setColor(edgeColor);
            pixmap.fillRectangle(0, 0, width, height);
            pixmap.setColor(splashColor);
            pixmap.fillRectangle(edgeThickness, edgeThickness, width - edgeThickness * 2, height - edgeThickness * 2);
            pixmap.setColor(centerColor);
            pixmap.fillRectangle(edgeThickness + splashThickness, edgeThickness + splashThickness,
                    width - (edgeThickness + splashThickness) * 2, height - (edgeThickness + splashThickness) * 2);
            tileTexture = new TextureRegion(new Texture(pixmap));
        }
        return tileTexture;
    }

    public static Touchpad.TouchpadStyle getTouchpadStyle(float knobWidth) {
        int radius = 100;
        Pixmap p = new Pixmap(radius * 2, radius * 2, Pixmap.Format.RGBA4444);
        p.setColor(Color.FIREBRICK);
        p.fillCircle(radius, radius, radius);
        Texture background = new Texture(p);
        p = new Pixmap(radius * 2, radius * 2, Pixmap.Format.RGBA4444);
        p.setColor(Color.SLATE);
        p.fillCircle(radius, radius, radius);
        Texture knob = new Texture(p);
        Touchpad.TouchpadStyle touchpadStyle =
                new Touchpad.TouchpadStyle(new TextureRegionDrawable(new TextureRegion(background)),
                        new TextureRegionDrawable(new TextureRegion(knob)));
        touchpadStyle.knob.setMinWidth(knobWidth);
        touchpadStyle.knob.setMinHeight(knobWidth);
        return touchpadStyle;
    }

    public static TextureRegion getColorRec(Color color) {
        if(! colorRecs.containsKey(color)) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
            pixmap.setColor(color);
            pixmap.fill();
            colorRecs.put(color, new TextureRegion(new Texture(pixmap)));
        }

        return colorRecs.get(color);
    }

    public static Pixmap getCursor() {
        int dim = 16;
        Pixmap pixmap = new Pixmap(dim, dim, Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.DARK_GRAY);
        int width = dim / 2;
        pixmap.fillRectangle(0, dim / 2 - width / 2, dim, width);
        pixmap.fillRectangle(dim / 2 - width / 2, 0, width, dim);

        pixmap.setColor(Color.LIGHT_GRAY);
        width /= 2;
        pixmap.fillRectangle(width / 2, dim / 2 - width / 2, dim - width, width);
        pixmap.fillRectangle(dim / 2 - width / 2, width / 2, width, dim - width);

        return pixmap;
    }

}
