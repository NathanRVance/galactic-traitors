package net.traitors.thing.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.projectile.Projectile;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;

public class Gun extends AbstractThing implements Item {

    private Texture inventoryImage;
    private Texture handImage;

    public Gun(float width, float height) {
        super(width, height);
    }

    @Override
    public Texture getInventoryImage() {
        if (inventoryImage == null) {
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

    @Override
    public void use() {
        //Make a plasma blast
        Point velocity = new Point(3, 0).rotate(GameScreen.getStuff().getPlayer().getWorldRotation());
        Projectile projectile = new Projectile(.1f, 1, Color.RED, GameScreen.getStuff().getPlayer().getWorldPoint(), velocity, 1);
        GameScreen.getStuff().addActor(projectile);
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(new TextureRegion(inventoryImage), getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, getWorldRotation() * MathUtils.radiansToDegrees);
    }

    @Override
    public void act(float delta) {
        //Do nothing
    }
}
