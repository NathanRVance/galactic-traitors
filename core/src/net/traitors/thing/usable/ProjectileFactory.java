package net.traitors.thing.usable;

import com.badlogic.gdx.graphics.Color;

import net.traitors.GameScreen;
import net.traitors.thing.Thing;
import net.traitors.thing.projectile.Projectile;
import net.traitors.util.Point;

public class ProjectileFactory implements Usable {

    //Start with some reasonable defaults
    private float cooldown = 1;
    private Point originOffset = new Point();
    private Point rotationOffset = new Point();
    private float thickness = .1f;
    private float length = .5f;
    private float speed = 20;
    private Color color = Color.RED;
    private float longevity = 2;

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
            Point velocity = new Point(speed, 0).rotate(rotation).add(user.getWorldVelocity());
            Point rotOff = rotationOffset.rotate(userRot);
            Point origOff = originOffset.rotate(userRot);
            Point startPoint = user.getWorldPoint().add(origOff.subtract(rotOff).rotate(rotation - userRot).add(rotOff));
            Projectile projectile = new Projectile(length, thickness,
                    color, startPoint, velocity, longevity);
            GameScreen.getStuff().addActor(projectile);
            timeToNextFire = cooldown;
        }
    }

    @Override
    public float getCooldownPercent() {
        if (timeToNextFire <= 0)
            return 1;
        return 1 - timeToNextFire / cooldown;
    }

    public void updateCooldown(float delta) {
        if (timeToNextFire > 0)
            timeToNextFire -= delta;
    }

    public static class Builder {

        ProjectileFactory factory = new ProjectileFactory();

        public Builder setCooldown(float cooldown) {
            factory.cooldown = cooldown;
            return this;
        }

        public Builder setOriginOffset(Point originOffset) {
            factory.originOffset = originOffset;
            return this;
        }

        public Builder setRotationOffset(Point rotationOffset) {
            factory.rotationOffset = rotationOffset;
            return this;
        }

        public Builder setThickness(float thickness) {
            factory.thickness = thickness;
            return this;
        }

        public Builder setLength(float length) {
            factory.length = length;
            return this;
        }

        public Builder setSpeed(float speed) {
            factory.speed = speed;
            return this;
        }

        public Builder setColor(Color color) {
            factory.color = color;
            return this;
        }

        public Builder setLongevity(float longevity) {
            factory.longevity = longevity;
            return this;
        }

        public ProjectileFactory build() {
            return factory;
        }

    }
}
