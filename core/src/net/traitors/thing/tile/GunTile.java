package net.traitors.thing.tile;

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
import net.traitors.thing.usable.Usable;
import net.traitors.util.Point;

public class GunTile extends AbstractThing implements Tile, Usable {

    private final float barrelLength;
    private Tile base;
    private TextureRegion dome;
    private TextureRegion barrel;
    private float rotation = 0;
    private ProjectileFactory projectileFactory;
    private RotationStrategy rotationStrategy;

    public GunTile(float width, float height, float rotation, Tile base) {
        super(width, height);
        setRotation(rotation);
        this.rotation = rotation;
        this.base = base;

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

        this.barrelLength = getHeight() * 3 / 4;

        projectileFactory = new ProjectileFactory.Builder()
                .setCooldown(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return .2f;
                    }
                })
                .setOriginOffset(new PointStrategy() {
                    @Override
                    public Point getPoint() {
                        return new Point(getWidth() / 2 * 1.2f + barrelLength, -.1f);
                    }
                })
                .setRotationOffset(new PointStrategy() {
                    @Override
                    public Point getPoint() {
                        return new Point(getWidth() / 2 * 1.2f, 0);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return .2f;
                    }
                })
                .setLength(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return .7f;
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
                        return 5;
                    }
                })
                .build();

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
        barrelp.rotate(rot);
        batch.draw(barrel,
                worldPoint.x + barrelp.x,
                worldPoint.y + barrelp.y - barrelWidth / 2,
                0,
                barrelWidth / 2,
                drawLen, barrelWidth, 1, 1, rotation * MathUtils.radiansToDegrees);
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
    public void use(Thing user) {
        rotation = rotationStrategy.getRotation(user.getWorldRotation(), getWorldRotation());
        projectileFactory.use(this, rotation);
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
}
