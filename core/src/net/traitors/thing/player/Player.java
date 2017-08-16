package net.traitors.thing.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GalacticTraitors;
import net.traitors.GameFactory;
import net.traitors.Layer;
import net.traitors.controls.Controls;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.item.Gun;
import net.traitors.thing.item.Item;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.ui.ScreenElements.InventoryBar;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;
import net.traitors.util.save.SaveData;

import java.util.Queue;

public class Player extends AbstractThing {

    //Animation stuff
    private static final float BASE_ANIMATION_LENGTH = .5f; //seconds
    private static final float BASE_MOVE_SPEED = 2f; //meters per second
    private TextureRegion[] animation;
    private TextureRegion[] animationHolding;
    private float animationLength = 1; //Time, in seconds, it takes to run through the animation
    private float animationPoint = 0;
    private boolean initLock = false;

    private Inventory inventory;
    private Color[] colors = new Color[5];

    public Player(Layer layer, Color bodyColor, Color skinColor, Color hairColor, Color pantsColor, Color shoesColor, InventoryBar bar) {
        this(layer);
        colors[0] = bodyColor;
        colors[1] = skinColor;
        colors[2] = hairColor;
        colors[3] = pantsColor;
        colors[4] = shoesColor;
        inventory = new Inventory(bar);
        //Populate with default inventory
        inventory.addItem(new Gun(layer, .1f, .1f));

        if (bar != null) {
            //This is the main character
            bar.setPlayer(this);
            GalacticTraitors.getCamera().setTracking(this);
        }
    }

    public Player(Layer layer) {
        super(layer, .5f, .5f, 70);
    }

    private void generateAnimations() {
        animation = TextureCreator.getPlayerAnimation(colors);
        animationHolding = TextureCreator.getPlayerAnimationHolding(colors);
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        //Save inventory
        sd.writeSaveData(inventory.getSaveData());
        //Save colors
        sd.writeInt(colors.length);
        for (Color color : colors) {
            sd.writeInt(Color.rgba8888(color));
        }

        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        initLock = true;
        InventoryBar inventoryBar = Controls.ID == getID() ? GameFactory.getInventoryBar() : null;
        if (inventoryBar != null) {
            //This is the main character
            inventoryBar.setPlayer(this);
            GalacticTraitors.getCamera().setTracking(this);
        }
        inventory = new Inventory(inventoryBar);
        inventory.loadSaveData(saveData.readSaveData());
        //Read colors
        colors = new Color[saveData.readInt()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(saveData.readInt());
        }
        if (animation == null) {
            generateAnimations();
        }
        initLock = false;
    }

    private void rotateToFace(Point point) {
        setRotation(point.subtract(getPoint()).angle());
    }

    private void worldTouched(Point point) {
        point = getPlatform().convertToPlatformCoordinates(point);
        rotateToFace(point);
        if (getPoint().distance(point) < getWidth()) {
            Thing item = getLayer().getThingAt(getWorldPoint());
            if (item != null && item instanceof Item) {
                getLayer().removeActor(item);
                inventory.addItem((Item) item);
            }
        }
        if (inventory.getHeld() != null) {
            inventory.getHeld().use(this, point);
        } else if (getPlatform() instanceof Ship) {
            ((Ship) getPlatform()).useUsableAt(this, getWorldPoint(), point);
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
        int ret = (int) (animationPoint / animationLength * animation.length);
        if (ret >= animation.length) ret = animation.length - 1;
        return ret;
    }

    public void setHolding(int index) {
        if (!initLock) {
            SaveData sd = new SaveData();
            sd.writeInt(index);
            Controls.operationPerformed(Controls.Operation.HOLD, sd);
        }
    }

    public void drop(int index) {
        if (!initLock) {
            SaveData sd = new SaveData();
            sd.writeInt(index);
            Controls.operationPerformed(Controls.Operation.DROP, sd);
        }
    }

    public void swapItems(int item1, int item2) {
        if (!initLock) {
            SaveData sd = new SaveData();
            sd.writeInt(item1);
            sd.writeInt(item2);
            Controls.operationPerformed(Controls.Operation.SWAP, sd);
        }
    }

    private void processOperations(Queue<Controls.OperationStruct> operations) {
        while (!operations.isEmpty()) {
            Controls.OperationStruct opt = operations.remove();
            SaveData data = opt.getData();
            switch (opt.getOperation()) {
                case SWAP:
                    inventory.swapItems(data.readInt(), data.readInt());
                    break;
                case DROP:
                    int index = data.readInt();
                    Item item = inventory.get(index);
                    if (inventory.getHeld() == item) { //If we're holding the same instance of the item
                        inventory.setHeld(-1);
                    }
                    inventory.remove(index);
                    if (item != null) {
                        item.setPlatform(getPlatform());
                        item.setPoint(getPoint());
                        getLayer().addActor(item);
                    }
                    break;
                case HOLD:
                    inventory.setHeld(data.readInt());
                    break;
                case AUTOSTOP:
                    // TODO: Implement me
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        inventory.updateCooldowns(delta);
        Controls.UserInput input = Controls.getInput(getID());
        processOperations(input.operations);
        float x = 0;
        float y = 0;
        float speedMult = 1;
        if (input.keysPressed.contains(Controls.Key.UP)) {
            y++;
        }
        if (input.keysPressed.contains(Controls.Key.DOWN)) {
            y--;
        }
        if (input.keysPressed.contains(Controls.Key.RIGHT)) {
            x++;
        }
        if (input.keysPressed.contains(Controls.Key.LEFT)) {
            x--;
        }
        if (input.keysPressed.contains(Controls.Key.SPRINT)) {
            speedMult = 2;
        }
        Point d = new Point(x, y);
        d = d.rotate(GalacticTraitors.getCamera().getCameraAngle() - getPlatform().getWorldRotation());

        float totMove = d.distanceFromZero();
        if (totMove != 0) {
            setAnimationLength(BASE_ANIMATION_LENGTH / speedMult);
            d = d.scale(BASE_MOVE_SPEED * speedMult / totMove);
            rotateToFace(getPoint().add(d));
            if (!(getPlatform() instanceof UniverseTile)) {
                setTranslationalVelocity(d);
            }
            incAnimation(delta);
        } else {
            if (!(getPlatform() instanceof UniverseTile)) {
                setTranslationalVelocity(new Point());
            }
            resetAnimation();
        }

        if (input.pointsTouched.size() == 1) {
            worldTouched(input.pointsTouched.get(0));
        }
    }

    @Override
    public void draw(Batch batch) {
        if (animation == null) generateAnimations();
        Point worldPointLowLeft = getWorldPoint().subtract(new Point(getWidth() / 2, getHeight() / 2));
        float rotation = getPlatform().getWorldRotation() + getRotation() + (float) Math.PI / 2;
        if (inventory.getHeld() == null) {
            batch.draw(animation[getAnimationIndex()], worldPointLowLeft.x, worldPointLowLeft.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, rotation * MathUtils.radiansToDegrees);
        } else {
            batch.draw(animationHolding[getAnimationIndex()], worldPointLowLeft.x, worldPointLowLeft.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, rotation * MathUtils.radiansToDegrees);
            TextureRegion inHand = inventory.getHeld().getHandImage();
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

    @Override
    public void dispose() {
    }
}
