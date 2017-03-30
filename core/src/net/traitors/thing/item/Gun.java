package net.traitors.thing.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.usable.FloatStrategy;
import net.traitors.thing.usable.PointStrategy;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public class Gun extends AbstractThing implements Item {

    private Texture inventoryImage;
    private TextureRegion handImage;
    private ProjectileFactory projectileFactory;

    public Gun(float width, float height) {
        super(width, height);
        setup();
    }

    public Gun() {
        setup();
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        sd.writeFloat(projectileFactory.getTimeToNextFire());
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        projectileFactory.setTimeToNextFire(saveData.readFloat());
    }

    private void setup() {
        projectileFactory = new ProjectileFactory()
                .setCooldown(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return 1;
                    }
                })
                .setOriginOffset(new PointStrategy() {
                    @Override
                    public Point getPoint() {
                        return new Point(.4f, -.25f);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return .1f;
                    }
                })
                .setLength(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return .5f;
                    }
                })
                .setSpeed(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return 20;
                    }
                })
                .setColor(Color.RED)
                .setLongevity(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return 1;
                    }
                });
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
    public TextureRegion getHandImage() {
        if (handImage == null) {
            int width = 10;
            Pixmap pixmap = new Pixmap(width, width * 4, Pixmap.Format.RGBA4444);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fill();
            handImage = new TextureRegion(new Texture(pixmap));
        }
        return handImage;
    }

    @Override
    public void use(Thing thing, Point touchPoint) {
        projectileFactory.use(thing, touchPoint);
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(new TextureRegion(getInventoryImage()), getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, getWorldRotation() * MathUtils.radiansToDegrees);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        projectileFactory.updateCooldown(delta);
    }

    @Override
    public float getCooldownPercent() {
        return projectileFactory.getCooldownPercent();
    }

    @Override
    public void dispose() {
        getInventoryImage().dispose();
        inventoryImage = null; //Set to null so that if getInventoryImage is called, it returns the right thing.
        getHandImage().getTexture().dispose();
        handImage = null;
    }
}
