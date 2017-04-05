package net.traitors.thing.tile.thrust;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.Thing;
import net.traitors.thing.tile.RotationStrategy;
import net.traitors.thing.tile.Tile;
import net.traitors.thing.usable.FloatStrategy;
import net.traitors.thing.usable.PointStrategy;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

import java.util.HashSet;
import java.util.Set;

public class RotationalThrusterStrategy implements ThrustStrategy {

    private final float coneWidth = .5f;
    private final float coneLen = coneWidth / 2;
    private Thing base;
    private TextureRegion cone;
    private Tile tile;
    private float forceMagnitude;

    private RotationStrategy rotationStrategy;
    private ProjectileFactory projectileFactory;
    private float initTimeToNextFire = 0;

    public RotationalThrusterStrategy(Tile tile, float forceMagnitude) {
        this.tile = tile;
        this.forceMagnitude = forceMagnitude;
        setup();
    }

    public RotationalThrusterStrategy() {

    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = new SaveData();
        sd.writeSavable(tile);
        sd.writeFloat(forceMagnitude);
        sd.writeFloat(projectileFactory.getTimeToNextFire());
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        setup();
        tile = (Tile) saveData.readSavable(tile);
        forceMagnitude = saveData.readFloat();
        initTimeToNextFire = saveData.readFloat();
    }

    private void setup() {
        int coneDim = 100;
        PixmapRotateRec pixmap = new PixmapRotateRec(coneDim, coneDim, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fillQuadrahedron(0, coneDim / 3, coneDim, 0, coneDim, coneDim, 0, coneDim * 2 / 3);
        cone = new TextureRegion(new Texture(pixmap));

        rotationStrategy = new RotationStrategy(.1f);
    }

    @Override
    public void setBase(final Thing base) {
        this.base = base;
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
                        return new Point(base.getWidth() / 2 + coneLen, MathUtils.random(-coneWidth * .4f, coneWidth * .4f) - .05f);
                    }
                })
                .setRotationOffset(new PointStrategy() {
                    @Override
                    public Point toPoint() {
                        return new Point(0, 0);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return MathUtils.random(.01f, .05f);
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
    public void applyThrust(Thing user, final float extent) {
        if (extent == 0) return;
        float thrustRotation = (rotationStrategy.getRotation(base.getRotation() - user.getRotation() + (float) Math.PI / 4, 0) > Math.PI / 4) ?
                //Activate top thruster
                base.getRotation() + (float) Math.PI / 2
                : //Activate bottom thruster
                base.getRotation();
        projectileFactory.setCooldown(new FloatStrategy() {
            @Override
            public float toFloat() {
                return (1 / extent) * .01f;
            }
        });
        projectileFactory.use(base, base.getPlatform().convertToWorldRotation(thrustRotation));
        Point force = new Point(forceMagnitude * -1 * extent, 0).rotate(thrustRotation);
        base.getPlatform().applyPointForce(force, base.getPoint());
    }

    /**
     * Gets info for all the thrusters this strategy can fire
     *
     * @return set of points, direction is this frame, and magnitude is max thrust.
     * Note that direction is direction of force, not of user's facing position!
     */
    public Set<Point> getThrusters() {
        Set<Point> ret = new HashSet<>();
        ret.add(new Point(forceMagnitude * -1, 0).rotate(base.getRotation()));
        ret.add(new Point(forceMagnitude * -1, 0).rotate(base.getRotation() + (float) Math.PI / 2));
        return ret;
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = base.getWorldPoint();
        float rot = base.getPlatform().getWorldRotation() + base.getRotation();
        //draw cone
        Point conep = new Point(base.getWidth() / 2, 0);
        conep = conep.rotate(rot);
        batch.draw(cone,
                worldPoint.x + conep.x,
                worldPoint.y + conep.y - coneWidth / 2,
                0,
                coneWidth / 2,
                coneLen, coneWidth, 1, 1, base.getPlatform().convertToWorldRotation(base.getRotation()) * MathUtils.radiansToDegrees);

        //draw cone again
        conep = new Point(0, base.getHeight() / 2);
        conep = conep.rotate(rot);
        batch.draw(cone,
                worldPoint.x + conep.x,
                worldPoint.y + conep.y - coneWidth / 2,
                0,
                coneWidth / 2,
                coneLen, coneWidth, 1, 1, base.getPlatform().convertToWorldRotation(base.getRotation() + (float) Math.PI / 2) * MathUtils.radiansToDegrees);
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
    public void dispose() {
        tile.dispose();
        cone.getTexture().dispose();
    }
}
