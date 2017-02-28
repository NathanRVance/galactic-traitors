package net.traitors.thing.tile.thrust;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.Thing;
import net.traitors.thing.tile.RotationStrategy;
import net.traitors.thing.tile.Tile;
import net.traitors.thing.usable.FloatStrategy;
import net.traitors.thing.usable.PointStrategy;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public class MainThrusterStrategy implements ThrustStrategy {

    private float rotation = 0;
    private transient Thing base;
    private Tile tile;
    private transient TextureRegion cone;
    private transient RotationStrategy rotationStrategy;
    private transient ProjectileFactory projectileFactory;
    private float forceMagnitude;

    private float initTimeToNextFire = 0;

    public MainThrusterStrategy(Tile tile, float forceMagnitude) {
        this.tile = tile;
        this.forceMagnitude = forceMagnitude;
        setup();
    }

    public MainThrusterStrategy() {

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

        rotationStrategy = new RotationStrategy((float) Math.PI / 7);
    }

    @Override
    public void applyThrust(Thing user) {
        rotation = rotationStrategy.getRotation(user.getRotation(), base.getRotation());
        projectileFactory.use(base, base.getPlatform().convertToWorldRotation(rotation));
        Point force = new Point(forceMagnitude * -1, 0).rotate(rotation);
        base.getPlatform().applyForce(force, base.getPoint(), GameScreen.getStuff().getDelta());
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
        rotation = this.base.getRotation();
        projectileFactory = new ProjectileFactory.Builder()
                .setCooldown(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return .001f;
                    }
                })
                .setOriginOffset(new PointStrategy() {
                    @Override
                    public Point getPoint() {
                        return new Point(base.getWidth() * .95f,
                                MathUtils.random(-base.getWidth() * .4f, base.getWidth() * .4f) - .1f);
                    }
                })
                .setRotationOffset(new PointStrategy() {
                    @Override
                    public Point getPoint() {
                        return new Point(base.getWidth() / 2, 0);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float getFloat() {
                        return MathUtils.random(.01f, .1f);
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
        projectileFactory.setTimeToNextFire(initTimeToNextFire);
    }

    @Override
    public void dispose() {
        tile.dispose();
        cone.getTexture().dispose();
    }
}
