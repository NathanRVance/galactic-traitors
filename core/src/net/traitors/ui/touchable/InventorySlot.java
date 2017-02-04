package net.traitors.ui.touchable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.traitors.thing.item.Item;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;

class InventorySlot extends Widget implements Touchable {

    private Item item;
    private boolean selected = false;
    private boolean touched = false;
    private Texture backgroundTexture;
    private Texture selectedTexture;
    private Texture cooldown;
    private Point drawImageAt = new Point();

    InventorySlot(final Inventory inventory) {
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

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) return false;
                drawImageAt = new Point(getX(), getY());
                touched = true;
                inventory.slotTapped(InventorySlot.this, !selected);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float minDragDistance = getWidth() / 2;
                if (item != null && new Point(x - getWidth() / 2, y - getHeight() / 2).distanceFromZero() > minDragDistance) {
                    drawImageAt = new Point(getX() + x - getWidth() / 2, getY() + y - getHeight() / 2);
                } else {
                    drawImageAt = new Point(getX(), getY());
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                touched = false;
                if(! drawImageAt.equals(new Point(getX(), getY()))) {
                    inventory.getPlayer().dropItem(item);
                    item = null;
                }
                drawImageAt = new Point(getX(), getY());
            }

        });
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        drawImageAt = new Point(x, y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
        batch.draw(backgroundTexture, getX(), getY(), getWidth(), getHeight());
        if (item != null) {
            float scale = .9f;
            batch.draw(item.getInventoryImage(), drawImageAt.x + getWidth() * (1 - scale) / 2,
                    drawImageAt.y + getHeight() * (1 - scale) / 2, getWidth() * scale, getHeight() * scale);
            if(item.getCooldownPercent() < 1) {
                batch.draw(cooldown, getX(), getY(), getWidth() * item.getCooldownPercent(), getHeight());
            }
        }
        if (selected) {
            batch.draw(selectedTexture, getX(), getY(), getWidth(), getHeight());
        }
    }

    void select() {
        selected = true;
    }

    void unselect() {
        selected = false;
    }

    boolean isSelected() {
        return selected;
    }

    Item getItem() {
        return item;
    }

    void setItem(Item item) {
        this.item = item;
    }

    @Override
    public boolean isTouched() {
        return touched;
    }

}
