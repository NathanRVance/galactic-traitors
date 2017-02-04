package net.traitors.thing.usable;

import com.badlogic.gdx.graphics.Color;

import net.traitors.GameScreen;
import net.traitors.thing.player.Player;
import net.traitors.thing.projectile.Projectile;
import net.traitors.util.Point;

public class ProjectileFactory implements Usable {

    private final float cooldown;
    private final Point originOffset;
    private final float projectileThickness;
    private final float projectileLength;
    private final float projectileSpeed;
    private final Color projectileColor;
    private final float projectileLongevity;

    private float timeToNextFire = 0;

    public ProjectileFactory(float cooldown, Point originOffset, float projectileThickness, float projectileLength, float projectileSpeed, Color projectileColor, float projectileLongevity) {
        this.cooldown = cooldown;
        this.originOffset = originOffset;
        this.projectileThickness = projectileThickness;
        this.projectileLength = projectileLength;
        this.projectileSpeed = projectileSpeed;
        this.projectileColor = projectileColor;
        this.projectileLongevity = projectileLongevity;
    }

    @Override
    public void use(Player player) {
        if (timeToNextFire <= 0) {
            //Make a plasma blast
            float rotation = player.getWorldRotation();
            Point velocity = new Point(projectileSpeed, 0).rotate(rotation).add(player.getWorldVelocity());
            Point startPoint = player.getWorldPoint().add(originOffset.rotate(rotation));
            Projectile projectile = new Projectile(projectileLength, projectileThickness,
                    projectileColor, startPoint, velocity, projectileLongevity);
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
}
