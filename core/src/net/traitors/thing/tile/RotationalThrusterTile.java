package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.usable.FloatStrategy;
import net.traitors.thing.usable.PointStrategy;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.thing.usable.Usable;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;

public class RotationalThrusterTile extends AbstractThing implements Tile, Usable {

    private float rotation;
    private Tile base;
    private TextureRegion cone;
    private ProjectileFactory projectileFactory;
    private final float coneWidth = .5f;
    private final float coneLen = coneWidth / 2;
    private RotationStrategy rotationStrategy;
    private float forceMagnitude = 1000;

    /**
     * This tile has a thruster on the top and on the right. Rotate accordingly.
     *
     * @param rotation Rotation of this tile relative to the platform
     * @param base     tile the player steps on
     */
    public RotationalThrusterTile(float rotation, Tile base) {
        super(1, 1);
        this.rotation = rotation;
        this.base = base;

        int coneDim = 100;
        PixmapRotateRec pixmap = new PixmapRotateRec(coneDim, coneDim, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fillQuadrahedron(0, coneDim / 3, coneDim, 0, coneDim, coneDim, 0, coneDim * 2 / 3);
        cone = new TextureRegion(new Texture(pixmap));

        projectileFactory = new ProjectileFactory.Builder()
                .setCooldown(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return .01f;
                    }
                })
                .setOriginOffset(new PointStrategy() {
                    @Override
                    public Point getPoint() {
                        return new Point(getWidth() / 2 + coneLen, MathUtils.random(-coneWidth * .4f, coneWidth * .4f) - .05f);
                    }
                })
                .setRotationOffset(new PointStrategy() {
                    @Override
                    public Point getPoint() {
                        return new Point(0, 0);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return MathUtils.random(.01f, .05f);
                    }
                })
                .setLength(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return MathUtils.random(.01f, .1f);
                    }
                })
                .setSpeed(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return MathUtils.random(5, 10);
                    }
                })
                .setColor(Color.RED)
                .setLongevity(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return 1;
                    }
                })
                .build();

        rotationStrategy = new RotationStrategy(.1f);

    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = getWorldPoint();
        float rot = getPlatform().getWorldRotation() + getRotation();
        //draw cone
        Point conep = new Point(getWidth() / 2, 0);
        conep.rotate(rot + rotation);
        batch.draw(cone,
                worldPoint.x + conep.x,
                worldPoint.y + conep.y - coneWidth / 2,
                0,
                coneWidth / 2,
                coneLen, coneWidth, 1, 1, getPlatform().convertToWorldRotation(rotation) * MathUtils.radiansToDegrees);

        //draw cone again
        conep = new Point(0, getHeight() / 2);
        conep.rotate(rot + rotation);
        batch.draw(cone,
                worldPoint.x + conep.x,
                worldPoint.y + conep.y - coneWidth / 2,
                0,
                coneWidth / 2,
                coneLen, coneWidth, 1, 1, getPlatform().convertToWorldRotation(rotation + (float) Math.PI / 2) * MathUtils.radiansToDegrees);
        //draw base
        base.setPlatform(getPlatform());
        base.setRotation(getRotation());
        base.setPoint(getPoint());
        base.draw(batch);
    }

    @Override
    public void dispose() {
        base.dispose();
        cone.getTexture().dispose();
    }

    @Override
    public void use(Thing user) {
        if(rotationStrategy.getRotation(rotation - user.getRotation() + (float) Math.PI / 4, 0) > Math.PI / 4) {
            //Activate top thruster
            applyThrust(rotation + (float) Math.PI / 2);
            if(rtt != null) {
                rtt.applyThrust(rotation + (float) Math.PI / 2);
            }
        } else {
            //Activate bottom thruster
            applyThrust(rotation);
        }
    }

    private void applyThrust(float rotation) {
        projectileFactory.use(this, getPlatform().convertToWorldRotation(rotation));
        Point force = new Point(forceMagnitude * -1, 0).rotate(rotation);
        getPlatform().applyForce(force, getPoint(), GameScreen.getStuff().getDelta());
    }

    private RotationalThrusterTile rtt;

    public void secretSpecialTestStuff(RotationalThrusterTile rtt) {
        this.rtt = rtt;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        projectileFactory.updateCooldown(delta);
    }

    @Override
    public float getCooldownPercent() {
        return 0;
    }
}
