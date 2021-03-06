package net.traitors.thing.tile.thrust;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.Thing;
import net.traitors.thing.tile.RotationStrategy;
import net.traitors.thing.tile.Tile;
import net.traitors.thing.usable.FloatStrategy;
import net.traitors.thing.usable.PointStrategy;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;
import net.traitors.util.save.SaveData;

public class MainThrusterStrategy implements ThrustStrategy {

    private float rotation = (float) Math.PI * 3 / 2;
    private transient Thing base;
    private Tile tile;
    private TextureRegion cone = TextureCreator.getCone();
    private RotationStrategy rotationStrategy = new RotationStrategy((float) Math.PI / 7);
    private ProjectileFactory projectileFactory;
    private float forceMagnitude;

    private float initTimeToNextFire = 0;

    public MainThrusterStrategy(Tile tile, float forceMagnitude) {
        this.tile = tile;
        this.forceMagnitude = forceMagnitude;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeSavable(tile);
        sd.writeFloat(forceMagnitude);
        sd.writeFloat(projectileFactory.getTimeToNextFire());
        sd.writeFloat(rotation);
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        tile = (Tile) saveData.readSavable();
        forceMagnitude = saveData.readFloat();
        initTimeToNextFire = saveData.readFloat();
        rotation = saveData.readFloat();
    }

    @Override
    public void applyThrust(Thing user, final float extent) {
        if (extent == 0) return;
        rotation = rotationStrategy.getRotation(user.getRotation(), base.getRotation());
        projectileFactory.setCooldown(new FloatStrategy() {
            @Override
            public float toFloat() {
                return (1 / extent) * .01f;
            }
        });
        projectileFactory.use(base, base.getPlatform().convertToWorldRotation(rotation));
        Point force = new Point(forceMagnitude * -1 * extent, 0).rotate(rotation);
        base.getPlatform().applyPointForce(force, base.getPoint());
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = base.getWorldPoint();
        float rot = base.getPlatform().getWorldRotation() + base.getRotation();
        //draw cone
        float coneWidth = base.getWidth();
        float coneLen = base.getWidth() / 2;
        Point conep = new Point(base.getWidth() / 2 * .8f, 0);
        conep = conep.rotate(rot);
        batch.draw(cone,
                worldPoint.x + conep.x,
                worldPoint.y + conep.y - coneWidth / 2,
                0,
                coneWidth / 2,
                coneLen, coneWidth, 1, 1, base.getPlatform().convertToWorldRotation(rotation) * MathUtils.radiansToDegrees);
        //draw tile
        tile.setPlatform(base.getPlatform());
        tile.setRotation(base.getRotation());
        tile.setPoint(base.getPoint());
        tile.draw(batch);
    }

    @Override
    public void updateCooldown(float delta) {
        projectileFactory.updateCooldown(delta);
    }

    @Override
    public void setBase(final Thing base) {
        this.base = base;
        //rotation = this.base.getRotation();
        projectileFactory = new ProjectileFactory()
                .setCooldown(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return .01f;
                    }
                })
                .setOriginOffset(new PointStrategy() {
                    @Override
                    public Point toPoint() {
                        return new Point(base.getWidth() * .95f,
                                MathUtils.random(-base.getWidth() * .4f, base.getWidth() * .4f) - .1f);
                    }
                })
                .setRotationOffset(new PointStrategy() {
                    @Override
                    public Point toPoint() {
                        return new Point(base.getWidth() / 2, 0);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return MathUtils.random(.01f, .1f);
                    }
                })
                .setLength(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return MathUtils.random(.01f, .1f);
                    }
                })
                .setSpeed(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return MathUtils.random(5, 10);
                    }
                })
                .setColor(Color.RED)
                .setLongevity(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return 1;
                    }
                });
        projectileFactory.setTimeToNextFire(initTimeToNextFire);
    }

    @Override
    public void dispose() {
        tile.dispose();
    }
}
