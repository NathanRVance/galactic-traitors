package net.traitors.thing.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.item.Item;
import net.traitors.thing.platform.Platform;
import net.traitors.controls.Controls;
import net.traitors.util.Point;
import net.traitors.thing.Thing;

public class Player implements Thing {

    private static final float BASE_ANIMATION_LENGTH = 1; //seconds
    private static final float BASE_MOVE_SPEED = 1.4f; //meters per second
    private final int cycleLength = 200;
    private float rotation = 0f;
    private TextureRegion[] animation;
    private TextureRegion[] animationHolding;
    private Item holding;
    private float animationPoint = 0;
    //Time, in seconds, it takes to run through the animation
    private float animationLength = 1;
    private Point point = new Point();
    private Platform platform = null;

    public Player(Color bodyColor, Color skinColor, Color hairColor, Color pantsColor, Color shoesColor) {
        animation = getAnimation(bodyColor, skinColor, hairColor, pantsColor, shoesColor, false);
        animationHolding = getAnimation(bodyColor, skinColor, hairColor, pantsColor, shoesColor, true);
    }

    public void rotateToFace(Point point) {
        setRotation(point.subtract(getPoint()).angle() + (float) Math.PI / 2);
    }

    private void setAnimationLength(float animationLength) {
        animationPoint = animationPoint / this.animationLength * animationLength;
        this.animationLength = animationLength;
    }

    private void incAnimation(float delta) {
        animationPoint += delta;
        animationPoint %= animationLength;
    }

    private void resetAnimation() {
        animationPoint = 0;
    }

    private int getAnimationIndex() {
        int ret = (int) (animationPoint / animationLength * cycleLength);
        if (ret >= cycleLength) ret = cycleLength - 1;
        return ret;
    }

    @Override
    public void act(float delta) {
        float x = 0;
        float y = 0;
        float speedMult = 1;
        if (Controls.isKeyPressed(Controls.Key.UP)) {
            y += BASE_MOVE_SPEED * delta;
        }
        if (Controls.isKeyPressed(Controls.Key.DOWN)) {
            y -= BASE_MOVE_SPEED * delta;
        }
        if (Controls.isKeyPressed(Controls.Key.RIGHT)) {
            x += BASE_MOVE_SPEED * delta;
        }
        if (Controls.isKeyPressed(Controls.Key.LEFT)) {
            x -= BASE_MOVE_SPEED * delta;
        }
        if (Controls.isKeyPressed(Controls.Key.SPRINT)) {
            speedMult = 3;
        }
        Point d = new Point(x, y);
        d = d.rotate(GameScreen.getCameraAngle() - getPlatformRotation());

        float totMove = d.distanceFromZero();
        if (totMove != 0) {
            setAnimationLength(BASE_ANIMATION_LENGTH / speedMult);
            d = new Point(d.x * BASE_MOVE_SPEED * delta * speedMult / totMove,
                    d.y * BASE_MOVE_SPEED * delta * speedMult / totMove);
            Point direction = new Point(getPoint().x + d.x, getPoint().y + d.y);
            rotateToFace(direction);
            setPoint(new Point(getPoint().x + d.x, getPoint().y + d.y));
            incAnimation(delta);
        } else {
            if(platform != null) {
                setRotation(rotation + platform.getRotationalVelocity() * delta);
            }
            resetAnimation();
        }
    }

    public void setHolding(Item item) {
        holding = item;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public Point getWorldPoint() {
        return (platform == null) ? getPoint() : platform.convertToWorldCoordinates(getPoint());
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public float getWorldRotation() {
        return (platform == null) ? getRotation() : platform.convertToWorldRotation(getRotation());
    }

    public float getPlatformRotation() {
        return (platform == null) ? 0f : platform.getWorldRotation();
    }

    public Point convertToPlatformCoordinates(Point point) {
        return (platform == null) ? point : platform.convertToPlatformCoordinates(point);
    }

    @Override
    public float getWidth() {
        return .5f;
    }

    @Override
    public float getHeight() {
        return .5f;
    }

    @Override
    public void setPlatform(Platform platform) {
        setPoint(getWorldPoint());
        if (platform != null)
            setPoint(platform.convertToPlatformCoordinates(getPoint()));
        this.platform = platform;
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = getWorldPoint();
        float worldRotation = getRotation();//getWorldRotation();
        if (holding == null) {
            batch.draw(animation[getAnimationIndex()], worldPoint.x - getWidth() / 2, worldPoint.y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, worldRotation * MathUtils.radiansToDegrees);
        } else {
            batch.draw(animationHolding[getAnimationIndex()], worldPoint.x - getWidth() / 2, worldPoint.y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, worldRotation * MathUtils.radiansToDegrees);
            Texture inHand = holding.getHandImage();
            Point itemp = new Point(worldPoint.x + getWidth() / 22, worldPoint.y - getHeight() / 3);
            float width = getWidth() / 10;
            //keep ratio
            float height = width * inHand.getHeight() / inHand.getWidth();
            batch.draw(new TextureRegion(inHand), itemp.x - getWidth() / 2, itemp.y - getHeight() / 2, worldPoint.x - itemp.x + getWidth() / 2, worldPoint.y - itemp.y + getHeight() / 2, width, height, 1, 1, worldRotation * MathUtils.radiansToDegrees);
        }
    }

    private TextureRegion[] getAnimation(Color bodyColor, Color skinColor, Color hairColor, Color pantsColor, Color shoesColor, boolean armExtended) {
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

    private void addAppendage(Pixmap pixmap, int forStart, int backStart, Appendage appendage, int startX, float fracToFullyExtended, boolean forward) {
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
                //pixmap.fillRectangle(pixmap.getWidth() - startX - width, backStart, width, (int) (maxBackExt * fracToFullyExtended));
            } else {
                pixmap.fillRectangle(startX, backStart, width, (int) (maxBackExt * fracToFullyExtended));
                //pixmap.fillRectangle(pixmap.getWidth() - startX - width, forStart, width, (int) (maxForExt * fracToFullyExtended));
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
