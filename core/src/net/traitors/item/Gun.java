package net.traitors.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Gun implements Item {

    private Texture inventoryImage;
    private Texture handImage;

    @Override
    public Texture getInventoryImage() {
        if (inventoryImage == null) {
            int width = 100;
            Pixmap pixmap = new Pixmap(width, width, Pixmap.Format.RGBA4444);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fillRectangle(width / 10, 0, width / 5, width);
            pixmap.fillRectangle(0, width / 8, width, width / 4);
            pixmap.fillRectangle(width * 7 / 8, 0, width / 8, width / 8);
            inventoryImage = new Texture(pixmap);
        }
        return inventoryImage;
    }

    @Override
    public Texture getHandImage() {
        if (handImage == null) {
            int width = 10;
            Pixmap pixmap = new Pixmap(width, width * 4, Pixmap.Format.RGBA4444);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fill();
            handImage = new Texture(pixmap);
        }
        return handImage;
    }
}
