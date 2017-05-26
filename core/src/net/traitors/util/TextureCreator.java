package net.traitors.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.HashMap;
import java.util.Map;

public class TextureCreator {

    private static TextureRegion tileTexture;
    private static Map<Color, TextureRegion> colorRecs = new HashMap<>();
    private static Texture gunInventoryImage;
    private static TextureRegion gunHandImage;
    private static TextureRegion cone;
    private static TextureRegion dome;
    private static Map<Color, TextureRegion> compasses = new HashMap<>();
    private static Map<String, TextureRegion[]> playerAnimations = new HashMap<>();
    private static Map<String, TextureRegion[]> playerAnimationsHolding = new HashMap<>();

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

    public static TextureRegion getColorRec(Color color) {
        if (!colorRecs.containsKey(color)) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
            pixmap.setColor(color);
            pixmap.fill();
            colorRecs.put(color, new TextureRegion(new Texture(pixmap)));
        }

        return colorRecs.get(color);
    }

    public static Texture getGunInventoryImage() {
        if (gunInventoryImage == null) {
            int width = 100;
            PixmapRotateRec pixmap = new PixmapRotateRec(width, width, Pixmap.Format.RGBA4444);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fillRectangle(width / 10, width / 8, width / 5, width);
            pixmap.fillRectangle(0, width / 8, width, width / 4);
            pixmap.fillRectangle(width * 7 / 8, 0, width / 8, width / 8);
            int x = width * 3 / 10;
            int y = width * 3 / 8;
            int thickness = width / 10;
            int ext = width / 6;
            pixmap.fillQuadrahedron(x, y, x, y + thickness, x + ext, y + ext + thickness, x + ext, y + ext);
            gunInventoryImage = new Texture(pixmap);
        }
        return gunInventoryImage;
    }

    public static TextureRegion getGunHandImage() {
        if (gunHandImage == null) {
            int width = 10;
            Pixmap pixmap = new Pixmap(width, width * 4, Pixmap.Format.RGBA4444);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fill();
            gunHandImage = new TextureRegion(new Texture(pixmap));
        }
        return gunHandImage;
    }

    public static TextureRegion getCone() {
        if (cone == null) {
            int coneDim = 100;
            PixmapRotateRec pixmap = new PixmapRotateRec(coneDim, coneDim, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.GRAY);
            pixmap.fillQuadrahedron(0, coneDim / 3, coneDim, 0, coneDim, coneDim, 0, coneDim * 2 / 3);
            cone = new TextureRegion(new Texture(pixmap));
        }
        return cone;
    }

    public static TextureRegion getDome() {
        if (dome == null) {
            int domeExtent = 200;
            int domeWidth = 400;
            Pixmap pixmap = new Pixmap(domeExtent, domeWidth, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.GRAY);
            pixmap.fillCircle(0, domeWidth / 2, domeExtent);
            dome = new TextureRegion(new Texture(pixmap));
        }
        return dome;
    }

    public static TextureRegion getCompass(Color ringColor) {
        if (!compasses.containsKey(ringColor)) {
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
            int thickness = height / 4;
            int x = dim / 2 - height / 2;
            int y = dim * 7 / 8 - height;
            //Vertical bar
            pixmap.fillRectangle(x, y, thickness, height);
            //Crossbar
            pixmap.fillQuadrahedron(x, y + height - thickness, x, y + height,
                    x + height, y + thickness, x + height, y);
            //Other bar
            pixmap.fillRectangle(x + height - thickness, y, thickness, height);

            //Draw S
            //To do so, we will plot 3x(x + .9)(x - .9) from -1 to 1 sideways
            y = dim / 8;
            for (float pos = 0; pos < height; pos++) {
                float funcIn = pos / height * 2 - 1;
                float funcOut = 3 * funcIn * (funcIn + .9f) * (funcIn - .9f);
                float drawStart = (funcOut / 2 + .5f) * height;
                pixmap.drawLine((int) drawStart + x - thickness / 2, (int) pos + y,
                        (int) drawStart + x + thickness / 2, (int) pos + y);
            }

            //Draw E
            x = dim * 7 / 8 - height;
            y = dim / 2 - height / 2;
            //Bottom horizontal bar
            pixmap.fillRectangle(x, y, height, thickness);
            //Middle horizontal bar
            pixmap.fillRectangle(x, y + height / 2 - thickness / 2, height, thickness);
            //Top vertical bar
            pixmap.fillRectangle(x, y + height - thickness, height, thickness);
            //Left vertical bar
            pixmap.fillRectangle(x, y, thickness, height);

            //Draw W
            x = dim / 8;
            //thickness *= 2;
            //Left to right, slanty bars
            int xEnd = x + height / 4;
            for (int i = 0; i < 2; i++) {
                pixmap.fillQuadrahedron(x, y + height, x + thickness, y + height, xEnd + thickness, y, xEnd, y);
                x = xEnd;
                xEnd = x + height / 4;
                pixmap.fillQuadrahedron(x, y, x + thickness, y, xEnd + thickness, y + height, xEnd, y + height);
                x = xEnd;
                xEnd = x + height / 4;
            }
            Pixmap flipped = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
            for (x = 0; x < pixmap.getWidth(); x++) {
                for (y = 0; y < pixmap.getHeight(); y++) {
                    flipped.drawPixel(x, y, pixmap.getPixel(x, pixmap.getHeight() - y));
                }
            }
            compasses.put(ringColor, new TextureRegion(new Texture(flipped)));
        }
        return compasses.get(ringColor);
    }

    private static String getStringValue(Color[] colors) {
        StringBuilder sb = new StringBuilder();
        for (Color color : colors) {
            sb.append(color);
        }
        return sb.toString();
    }

    public static TextureRegion[] getPlayerAnimation(Color[] colors) {
        String key = getStringValue(colors);
        if (!playerAnimations.containsKey(key)) {
            playerAnimations.put(key, getAnimation(colors[0], colors[1], colors[2], colors[3], colors[4], false));
        }
        return playerAnimations.get(key);
    }

    public static TextureRegion[] getPlayerAnimationHolding(Color[] colors) {
        String key = getStringValue(colors);
        if (!playerAnimationsHolding.containsKey(key)) {
            playerAnimationsHolding.put(key, getAnimation(colors[0], colors[1], colors[2], colors[3], colors[4], true));
        }
        return playerAnimationsHolding.get(key);
    }

    private static TextureRegion[] getAnimation(Color bodyColor, Color skinColor, Color hairColor, Color pantsColor, Color shoesColor, boolean armExtended) {
        int cycleLength = 200;
        TextureRegion[] anim = new TextureRegion[cycleLength];
        int torsoWidth = 100;
        int torsoDepth = torsoWidth / 4;
        int armWidth = torsoWidth / 5;
        int handWidth = armWidth * 3 / 4;
        int legWidth = torsoWidth * 3 / 10;
        int maxExtension = armWidth * 2;
        int backswingExtension = maxExtension / 2;

        Appendage arms = new Appendage();
        arms.colors = new Color[]{bodyColor, skinColor};
        arms.widths = new int[]{armWidth, handWidth};
        arms.maxForExt = new int[]{maxExtension / 3, maxExtension / 9};
        arms.maxBackExt = new int[]{backswingExtension / 3, backswingExtension / 9};

        Appendage legs = new Appendage();
        legs.colors = new Color[]{pantsColor, shoesColor};
        legs.widths = new int[]{legWidth, legWidth};
        legs.maxForExt = new int[]{maxExtension * 3 / 4, maxExtension / 4};
        legs.maxBackExt = new int[]{backswingExtension * 3 / 4, backswingExtension / 4};


        for (int i = 0; i < cycleLength; i++) {
            Pixmap pixmap = new Pixmap(torsoWidth, torsoDepth + maxExtension + backswingExtension, Pixmap.Format.RGBA4444);
            //Torso
            pixmap.setColor(bodyColor);
            pixmap.fillRectangle(0, backswingExtension, torsoWidth, torsoDepth);

            //Swinging appendages
            boolean rightForward = i < cycleLength / 2;
            int j = (i < cycleLength / 2) ? i : cycleLength - i;
            j = (j < cycleLength / 4) ? j : cycleLength / 2 - j;
            float fracToFullyExtended = j * 1.0f / (cycleLength / 4);

            //Forearms
            if (armExtended) {
                int armLength = torsoWidth * 3 / 10;
                int slitLength = armLength / 20;
                int numSlits = 10;
                for (int k = 0; k < numSlits; k++) {
                    Color c = new Color(bodyColor);
                    float darken = (float) (.5 + ((float) k) / (numSlits * 2));
                    c.mul(darken, darken, darken, 1);
                    pixmap.setColor(c);
                    pixmap.fillRectangle(0, backswingExtension + torsoDepth + slitLength * k, armWidth, slitLength);
                }
                pixmap.fillRectangle(0, backswingExtension + torsoDepth + numSlits * slitLength, armWidth, armLength - numSlits * slitLength);
                pixmap.setColor(skinColor);
                pixmap.fillRectangle((armWidth - handWidth) / 2, backswingExtension + torsoDepth + armLength, handWidth, handWidth);
            } else {
                addAppendage(pixmap, backswingExtension + torsoDepth, backswingExtension, arms, 0, fracToFullyExtended, rightForward);
            }
            addAppendage(pixmap, backswingExtension + torsoDepth, backswingExtension, arms, torsoWidth - armWidth, fracToFullyExtended, !rightForward);

            //Legs
            addAppendage(pixmap, backswingExtension + torsoDepth, backswingExtension, legs, armWidth, fracToFullyExtended, !rightForward);
            addAppendage(pixmap, backswingExtension + torsoDepth, backswingExtension, legs, torsoWidth - armWidth * 2, fracToFullyExtended, rightForward);

            //Hair
            pixmap.setColor(hairColor);
            int hairWidth = torsoWidth * 4 / 9;
            int hairDepth = torsoDepth * 3 / 2;
            pixmap.fillRectangle((torsoWidth - hairWidth) / 2, backswingExtension, hairWidth, hairDepth);

            //Nose
            pixmap.setColor(skinColor);
            int noseWidth = torsoWidth / 10;
            int noseDepth = noseWidth / 2;
            pixmap.fillRectangle((torsoWidth - noseWidth) / 2, backswingExtension + hairDepth, noseWidth, noseDepth);

            anim[i] = new TextureRegion(new Texture(pixmap));
        }
        return anim;
    }

    private static void addAppendage(Pixmap pixmap, int forStart, int backStart, Appendage appendage, int startX, float fracToFullyExtended, boolean forward) {
        for (int i = 0; i < appendage.colors.length; i++) {
            Color c = new Color(appendage.colors[i]);
            float darken = fracToFullyExtended * .5f;
            c.mul(darken, darken, darken, 1);
            pixmap.setColor(c);

            int width = appendage.widths[i];
            if (i > 0) {
                //center it on the previous width
                startX += (appendage.widths[i - 1] - width) / 2;
            }
            int maxForExt = appendage.maxForExt[i];
            int maxBackExt = appendage.maxBackExt[i];
            backStart -= maxBackExt * fracToFullyExtended - 1; //That 1 pixel can make a big difference

            if (forward) {
                pixmap.fillRectangle(startX, forStart, width, (int) (maxForExt * fracToFullyExtended));
            } else {
                pixmap.fillRectangle(startX, backStart, width, (int) (maxBackExt * fracToFullyExtended));
            }
            forStart += maxForExt * fracToFullyExtended;
        }
    }

    private static class Appendage {
        Color[] colors;
        int[] widths;
        int[] maxForExt;
        int[] maxBackExt;
    }

}
