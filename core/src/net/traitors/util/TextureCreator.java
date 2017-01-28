package net.traitors.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TextureCreator {

    private static Texture tileTexture;

    public static Texture getTileTexture() {
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
            tileTexture = new Texture(pixmap);
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

}
