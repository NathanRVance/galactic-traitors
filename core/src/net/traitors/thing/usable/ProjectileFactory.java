package net.traitors.thing.usable;

import com.badlogic.gdx.graphics.Color;

import net.traitors.GameScreen;
import net.traitors.thing.Thing;
import net.traitors.thing.projectile.Projectile;
import net.traitors.util.Point;

public class ProjectileFactory implements Usable {

    //Start with some reasonable defaults
    private FloatStrategy cooldown = new FloatStrategy() {
                @Override
                public float getFloat() {
                    return 1;
                }
            };
    private PointStrategy originOffset = new PointStrategy() {
        @Override
        public Point getPoint() {
            return new Point();
        }
    };
    private PointStrategy rotationOffset = new PointStrategy() {
        @Override
        public Point getPoint() {
            return new Point();
        }
    };
    private FloatStrategy thickness = new FloatStrategy() {
        @Override
        public float getFloat() {
            return .1f;
        }
    };
    private FloatStrategy length = new FloatStrategy() {
        @Override
        public float getFloat() {
            return .5f;
        }
    };
    private FloatStrategy speed = new FloatStrategy() {
        @Override
        public float getFloat() {
            return 20;
        }
    };
    private Color color = Color.RED;
    private FloatStrategy longevity = new FloatStrategy() {
        @Override
        public float getFloat() {
            return 2;
        }
    };

    private float timeToNextFire = 0;

    private ProjectileFactory() {
    }

    @Override
    public void use(Thing user) {
        use(user, user.getWorldRotation());
    }

    public void use(Thing user, float rotation) {
        if (timeToNextFire <= 0) {
            //Make a plasma blast
            float userRot = user.getWorldRotation();
            Point velocity = new Point(speed.getFloat(), 0).rotate(rotation).add(user.getWorldVelocity());
            Point rotOff = rotationOffset.getPoint().rotate(userRot);
            Point origOff = originOffset.getPoint().rotate(userRot);
            Point startPoint = user.getWorldPoint().add(origOff.subtract(rotOff).rotate(rotation - userRot).add(rotOff));
            Projectile projectile = new Projectile(length.getFloat(), thickness.getFloat(),
                    color, startPoint, velocity, longevity.getFloat());
            GameScreen.getStuff().addActor(projectile);
            timeToNextFire = cooldown.getFloat();
        }
    }

    @Override
    public float getCooldownPercent() {
        if (timeToNextFire <= 0)
            return 1;
        return 1 - timeToNextFire / cooldown.getFloat();
    }

    public void updateCooldown(float delta) {
        if (timeToNextFire > 0)
            timeToNextFire -= delta;
    }

    public float getTimeToNextFire() {
        return timeToNextFire;
    }

    public void setTimeToNextFire(float timeToNextFire){
        this.timeToNextFire = timeToNextFire;
    }

    public static class Builder {

        ProjectileFactory factory = new ProjectileFactory();

        public Builder setCooldown(FloatStrategy cooldown) {
            factory.cooldown = cooldown;
            return this;
        }

        public Builder setOriginOffset(PointStrategy originOffset) {
            factory.originOffset = originOffset;
            return this;
        }

        public Builder setRotationOffset(PointStrategy rotationOffset) {
            factory.rotationOffset = rotationOffset;
            return this;
        }

        public Builder setThickness(FloatStrategy thickness) {
            factory.thickness = thickness;
            return this;
        }

        public Builder setLength(FloatStrategy length) {
            factory.length = length;
            return this;
        }

        public Builder setSpeed(FloatStrategy speed) {
            factory.speed = speed;
            return this;
        }

        public Builder setColor(Color color) {
            factory.color = color;
            return this;
        }

        public Builder setLongevity(FloatStrategy longevity) {
            factory.longevity = longevity;
            return this;
        }

        public ProjectileFactory build() {
            return factory;
        }

    }
}
