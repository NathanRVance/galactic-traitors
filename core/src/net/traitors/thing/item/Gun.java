package net.traitors.thing.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.Layer;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.UniverseTile;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.usable.FloatStrategy;
import net.traitors.thing.usable.PointStrategy;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;
import net.traitors.util.save.SaveData;

public class Gun extends AbstractThing implements Item {

    private static final float kickbackTime = .05f;
    private ProjectileFactory projectileFactory;
    private Point kickbackForce = new Point(-2000, 0);
    private float kickingTimer = 0;
    private Thing kicking;

    public Gun(Layer layer, float width, float height) {
        super(layer, width, height);

        projectileFactory = new ProjectileFactory()
                .setCooldown(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return 1;
                    }
                })
                .setOriginOffset(new PointStrategy() {
                    @Override
                    public Point toPoint() {
                        return new Point(.4f, -.25f);
                    }
                })
                .setThickness(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return .1f;
                    }
                })
                .setLength(new FloatStrategy() {
                    @Override
                    public float toFloat() {
                        return .5f;
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
                        return 1;
                    }
                });
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

    @Override
    public Texture getInventoryImage() {
        return TextureCreator.getGunInventoryImage();
    }

    @Override
    public TextureRegion getHandImage() {
        return TextureCreator.getGunHandImage();
    }

    @Override
    public void use(Thing thing, Point touchPoint) {
        if (projectileFactory.getCooldownPercent() == 1) {
            kickingTimer = kickbackTime;
            kicking = thing;
        }
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
        if (kickingTimer > 0) {
            kickingTimer -= delta;
            kicking.applyForce(kickbackForce);
        }
        if(getPlatform() instanceof UniverseTile) {
            System.out.println("Space!");
        } else if (getPlatform() instanceof Ship) {
            System.out.println("Not space!");
        }
    }

    @Override
    public float getCooldownPercent() {
        return projectileFactory.getCooldownPercent();
    }

    @Override
    public void dispose() {
    }
}
