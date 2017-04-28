package net.traitors.ui.ScreenElements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.GameScreen;
import net.traitors.Layer;
import net.traitors.controls.MouseoverCallback;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.item.Item;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;

class InventorySlot extends AbstractThing implements Selectable, MouseoverCallback {

    private Item item;
    private boolean selected = false;
    private Texture backgroundTexture;
    private Texture selectedTexture;
    private Texture cooldown;
    private Point imageOffset = new Point();
    private Point initialTap = new Point();
    private SelectableSwitch<InventorySlot> selectableSwitch;

    InventorySlot(Layer layer, SelectableSwitch<InventorySlot> selectableSwitch, float width, float height) {
        super(layer, width, height);
        this.selectableSwitch = selectableSwitch;
        PixmapRotateRec pixmap = new PixmapRotateRec(100, 100, Pixmap.Format.RGBA4444);
        pixmap.setColor(0, 0, 0, .7f);
        pixmap.fill();
        pixmap.setColor(Color.BLACK);
        pixmap.drawRectangle(0, 0, 100, 100);
        backgroundTexture = new Texture(pixmap);

        pixmap = new PixmapRotateRec(100, 100, Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.RED);
        pixmap.drawRectangle(0, 0, 100, 100, 4);
        selectedTexture = new Texture(pixmap);

        pixmap = new PixmapRotateRec(100, 100, Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.RED.r, Color.RED.g, Color.RED.b, .5f);
        pixmap.fill();
        cooldown = new Texture(pixmap);
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(backgroundTexture, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2, getWidth(), getHeight());
        if (item != null) {
            float scale = .9f;
            batch.draw(item.getInventoryImage(), imageOffset.x + getWorldPoint().x - getWidth() / 2 + getWidth() * (1 - scale) / 2,
                    imageOffset.y + getWorldPoint().y - getHeight() / 2 + getHeight() * (1 - scale) / 2,
                    getWidth() * scale, getHeight() * scale);
            if (item.getCooldownPercent() < 1) {
                batch.draw(cooldown, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2,
                        getWidth() * item.getCooldownPercent(), getHeight());
            }
        }
        if (selected) {
            batch.draw(selectedTexture, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2,
                    getWidth(), getHeight());
        }
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        selectedTexture.dispose();
        cooldown.dispose();
    }

    Item getItem() {
        return item;
    }

    void setItem(Item item) {
        this.item = item;
    }

    @Override
    public void select() {
        selected = true;
        GameScreen.getPlayer().setHolding(getItem());
    }

    @Override
    public void unselect() {
        if(selected) {
            GameScreen.getPlayer().setHolding(null);
        }
        selected = false;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void mouseEnter() {

    }

    @Override
    public void mouseExit() {

    }

    @Override
    public boolean mouseDown(Point touchLoc) {
        initialTap = touchLoc;
        imageOffset = new Point();
        selectableSwitch.selectableTapped(this, !selected);
        return true;
    }

    @Override
    public boolean mouseDragged(Point touchLoc) {
        float minDragDistance = getWidth() / 2;
        if (item != null && touchLoc.distance(initialTap) > minDragDistance) {
            imageOffset = touchLoc.subtract(getWorldPoint());
        } else {
            imageOffset = new Point();
        }
        return true;
    }

    @Override
    public boolean mouseUp() {
        if (!imageOffset.isZero()) {
            selectableSwitch.selectableTapped(this, false); //unselect
            GameScreen.getPlayer().dropItem(item);
            item = null;
        }
        imageOffset = new Point();
        initialTap = getWorldPoint();
        return true;
    }
}
