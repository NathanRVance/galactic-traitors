package net.traitors.thing.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.controls.Controls;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.item.Item;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.usable.Usable;
import net.traitors.ui.touchable.Inventory;
import net.traitors.util.Point;

public class Player extends AbstractThing {

    //Animation stuff
    private static final float BASE_ANIMATION_LENGTH = 1; //seconds
    private static final float BASE_MOVE_SPEED = 1.4f; //meters per second
    private final int cycleLength = 200;
    private TextureRegion[] animation;
    private TextureRegion[] animationHolding;
    private float animationLength = 1; //Time, in seconds, it takes to run through the animation
    private float animationPoint = 0;

    private Item holding;
    private Inventory inventory;

    public Player(Color bodyColor, Color skinColor, Color hairColor, Color pantsColor, Color shoesColor) {
        super(.5f, .5f, 70);
        animation = getAnimation(bodyColor, skinColor, hairColor, pantsColor, shoesColor, false);
        animationHolding = getAnimation(bodyColor, skinColor, hairColor, pantsColor, shoesColor, true);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    private void rotateToFace(Point point) {
        setRotation(point.subtract(getPoint()).angle());
    }

    private void worldTouched(Point point) {
        point = getPlatform().convertToPlatformCoordinates(point);
        rotateToFace(point);
        if (getPoint().distance(point) < getWidth()) {
            Item item = GameScreen.getStuff().getItemAt(getPlatform().convertToWorldCoordinates(point));
            if (item != null) {
                GameScreen.getStuff().removeActor(item);
                inventory.addItem(item);
            }
        }
        if (holding != null) {
            holding.use(this);
        } else if (getPlatform() instanceof Ship) {
            Usable usable = ((Ship) getPlatform()).getUsableAt(getWorldPoint());
            if (usable != null) {
                usable.use(this);
            }
        }
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

    public void setHolding(Item item) {
        holding = item;
    }

    public void dropItem(Item item) {
        if (holding == item) { //If we're holding the same instance of the item
            holding = null;
        }
        item.setPlatform(getPlatform());
        item.setPoint(getPoint());
        GameScreen.getStuff().addActor(item);
    }

    public void move(float delta, Controls.UserInput input) {
        float x = 0;
        float y = 0;
        float speedMult = 1;
        if (input.keysPressed.contains(Controls.Key.UP)) {
            y += BASE_MOVE_SPEED * delta;
        }
        if (input.keysPressed.contains(Controls.Key.DOWN)) {
            y -= BASE_MOVE_SPEED * delta;
        }
        if (input.keysPressed.contains(Controls.Key.RIGHT)) {
            x += BASE_MOVE_SPEED * delta;
        }
        if (input.keysPressed.contains(Controls.Key.LEFT)) {
            x -= BASE_MOVE_SPEED * delta;
        }
        if (input.keysPressed.contains(Controls.Key.SPRINT)) {
            speedMult = 3;
        }
        Point d = new Point(x, y);
        d = d.rotate(GameScreen.getStuff().getCamera().getCameraAngle() - getPlatform().getWorldRotation());

        float totMove = d.distanceFromZero();
        if (totMove != 0) {
            setAnimationLength(BASE_ANIMATION_LENGTH / speedMult);
            d = d.scale(BASE_MOVE_SPEED * delta * speedMult / totMove);
            Point destination = getPoint().add(d);
            rotateToFace(destination);
            setPoint(destination);
            incAnimation(delta);
        } else {
            resetAnimation();
        }

        if(input.pointsTouched.size() == 1) {
            worldTouched(input.pointsTouched.get(0));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        inventory.updateCooldowns(delta);
    }

    @Override
    public void draw(Batch batch) {
        Point worldPointLowLeft = getWorldPoint().subtract(new Point(getWidth() / 2, getHeight() / 2));
        float rotation = getPlatform().getWorldRotation() + getRotation() + (float) Math.PI / 2;
        if (holding == null) {
            batch.draw(animation[getAnimationIndex()], worldPointLowLeft.x, worldPointLowLeft.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, rotation * MathUtils.radiansToDegrees);
        } else {
            batch.draw(animationHolding[getAnimationIndex()], worldPointLowLeft.x, worldPointLowLeft.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, rotation * MathUtils.radiansToDegrees);
            TextureRegion inHand = holding.getHandImage();
            Point itemp = new Point(getWidth() / 22, -getHeight() / 3);
            float width = getWidth() / 10;
            //keep ratio
            float height = width * inHand.getTexture().getHeight() / inHand.getTexture().getWidth();
            batch.draw(inHand,
                    worldPointLowLeft.x + itemp.x,
                    worldPointLowLeft.y + itemp.y,
                    getWidth() / 2 - itemp.x,
                    getHeight() / 2 - itemp.y,
                    width, height, 1, 1, rotation * MathUtils.radiansToDegrees);
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
            } else {
                pixmap.fillRectangle(startX, backStart, width, (int) (maxBackExt * fracToFullyExtended));
            }
            forStart += maxForExt * fracToFullyExtended;
        }
    }

    @Override
    public void dispose() {
        for(TextureRegion textureRegion : animation) {
            textureRegion.getTexture().dispose();
        }
        for(TextureRegion textureRegion : animationHolding) {
            textureRegion.getTexture().dispose();
        }
    }

    private static class Appendage {
        Color[] colors;
        int[] widths;
        int[] maxForExt;
        int[] maxBackExt;
    }
}
