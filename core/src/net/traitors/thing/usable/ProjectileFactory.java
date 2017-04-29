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
                public float toFloat() {
                    return 1;
                }
            };
    private PointStrategy originOffset = new PointStrategy() {
        @Override
        public Point toPoint() {
            return new Point();
        }
    };
    private PointStrategy rotationOffset = new PointStrategy() {
        @Override
        public Point toPoint() {
            return new Point();
        }
    };
    private FloatStrategy thickness = new FloatStrategy() {
        @Override
        public float toFloat() {
            return .1f;
        }
    };
    private FloatStrategy length = new FloatStrategy() {
        @Override
        public float toFloat() {
            return .5f;
        }
    };
    private FloatStrategy speed = new FloatStrategy() {
        @Override
        public float toFloat() {
            return 20;
        }
    };
    private Color color = Color.RED;
    private FloatStrategy longevity = new FloatStrategy() {
        @Override
        public float toFloat() {
            return 2;
        }
    };

    private float timeToNextFire = 0;

    @Override
    public void use(Thing user, Point touchPoint) {
        use(user, user.getWorldRotation());
    }

    public void use(Thing user, float rotation) {
        if (timeToNextFire <= 0) {
            //Make a plasma blast
            float userRot = user.getWorldRotation();
            Point velocity = new Point(speed.toFloat(), 0).rotate(rotation).add(user.getWorldVelocity());
            Point rotOff = rotationOffset.toPoint().rotate(userRot);
            Point origOff = originOffset.toPoint().rotate(userRot);
            Point startPoint = user.getWorldPoint().add(origOff.subtract(rotOff).rotate(rotation - userRot).add(rotOff));
            Projectile projectile = new Projectile(user.getLayer(), length.toFloat(), thickness.toFloat(),
                    color, startPoint, velocity, longevity.toFloat());
            user.getLayer().addActor(projectile);
            timeToNextFire = cooldown.toFloat();
        }
    }

    @Override
    public float getCooldownPercent() {
        if (timeToNextFire <= 0)
            return 1;
        return 1 - timeToNextFire / cooldown.toFloat();
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

    public ProjectileFactory setCooldown(FloatStrategy cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public ProjectileFactory setOriginOffset(PointStrategy originOffset) {
        this.originOffset = originOffset;
        return this;
    }

    public ProjectileFactory setRotationOffset(PointStrategy rotationOffset) {
        this.rotationOffset = rotationOffset;
        return this;
    }

    public ProjectileFactory setThickness(FloatStrategy thickness) {
        this.thickness = thickness;
        return this;
    }

    public ProjectileFactory setLength(FloatStrategy length) {
        this.length = length;
        return this;
    }

    public ProjectileFactory setSpeed(FloatStrategy speed) {
        this.speed = speed;
        return this;
    }

    public ProjectileFactory setColor(Color color) {
        this.color = color;
        return this;
    }

    public ProjectileFactory setLongevity(FloatStrategy longevity) {
        this.longevity = longevity;
        return this;
    }
}
