package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.platform.ship.ShipComponent;
import net.traitors.thing.usable.FloatStrategy;
import net.traitors.thing.usable.PointStrategy;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.thing.usable.Usable;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public class GunTile extends AbstractThing implements ShipComponent {

    private float barrelLength;
    private Tile base;
    private transient TextureRegion dome;
    private transient TextureRegion barrel;
    private float rotation = 0;
    private transient ProjectileFactory projectileFactory;
    private transient RotationStrategy rotationStrategy;
    private Ship ship;

    public GunTile(float width, float height, float rotation, Tile base) {
        super(width, height);
        setRotation(rotation);
        this.rotation = rotation;
        this.base = base;

        setup();
    }

    public GunTile() {
        setup();
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        sd.writeSavable(base);
        sd.writeFloat(projectileFactory.getTimeToNextFire());
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        base = (Tile) saveData.readSavable(base);
        projectileFactory.setTimeToNextFire(saveData.readFloat());
    }

    private void setup() {
        this.barrelLength = getHeight() * 3 / 4;
        int domeExtent = 200;
        int domeWidth = 400;
        Pixmap pixmap = new Pixmap(domeExtent, domeWidth, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fillCircle(0, domeWidth / 2, domeExtent);
        dome = new TextureRegion(new Texture(pixmap));

        int barrelLen = 10;
        pixmap = new Pixmap(barrelLen, barrelLen, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        barrel = new TextureRegion(new Texture(pixmap));

        projectileFactory = new ProjectileFactory()
                .setCooldown(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return .2f;
                    }
                })
                .setOriginOffset(new PointStrategy() {
                    @Override
                    public Point toPoint() {
                        return new Point(getWidth() / 2 * 1.2f + barrelLength, -.1f);
                    }
                })
                .setRotationOffset(new PointStrategy() {
                    @Override
                    public Point toPoint() {
                        return new Point(getWidth() / 2 * 1.2f, 0);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return .2f;
                    }
                })
                .setLength(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return .7f;
                    }
                })
                .setSpeed(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return 20;
                    }
                })
                .setColor(Color.RED)
                .setLongevity(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return 5;
                    }
                });

        rotationStrategy = new RotationStrategy((float) Math.PI * 2 / 3);
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = getWorldPoint();
        float rot = getPlatform().getWorldRotation() + getRotation();
        //draw base
        base.setPlatform(getPlatform());
        base.setRotation(getRotation());
        base.setPoint(getPoint());
        base.draw(batch);
        //draw barrel
        float barrelWidth = barrelLength / 8;
        float drawLen = (projectileFactory.getCooldownPercent() * .1f + .9f) * barrelLength;
        Point barrelp = new Point(getWidth() / 2 * 1.2f, 0);
        barrelp = barrelp.rotate(rot);
        batch.draw(barrel,
                worldPoint.x + barrelp.x,
                worldPoint.y + barrelp.y - barrelWidth / 2,
                0,
                barrelWidth / 2,
                drawLen, barrelWidth, 1, 1,
                getPlatform().convertToWorldRotation(rotation) * MathUtils.radiansToDegrees);
        //draw dome
        float domeWidth = getHeight();
        float domeExtent = domeWidth / 2;
        Point domep = new Point(getWidth() / 2, -domeWidth / 2);
        batch.draw(dome,
                worldPoint.x + domep.x,
                worldPoint.y + domep.y,
                -domep.x,
                -domep.y,
                domeExtent, domeWidth, 1, 1, rot * MathUtils.radiansToDegrees);
    }

    @Override
    public void use(Thing user, Point touchPoint) {
        rotation = rotationStrategy.getRotation(user.getRotation(), getRotation());
        projectileFactory.use(this, getPlatform().convertToWorldRotation(rotation));
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
        base.dispose();
        dome.getTexture().dispose();
        barrel.getTexture().dispose();
    }

    @Override
    public void setShip(Ship ship) {
        this.ship = ship;
    }
}
