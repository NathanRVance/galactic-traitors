package net.traitors.thing.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;

public class Projectile extends AbstractThing {

    private Point velocity;
    private transient TextureRegion texture;
    private float longevity;
    //Keep track of our own location so that we don't rotate/move with our platform
    private Point location;

    public Projectile(float width, float height, Color color, Point start, Point velocity, float longevity) {
        super(width, height);
        setPoint(start);
        location = null;
        this.velocity = velocity;
        this.longevity = longevity;
        texture = TextureCreator.getColorRec(color);
    }

    @Override
    public void draw(Batch batch) {
        Point drawLocation = (location == null) ? getWorldPoint() : location;
        batch.draw(texture, drawLocation.x, drawLocation.y,
                0, 0, getWidth(), getHeight(), 1, 1,
                velocity.angle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (location == null) location = getWorldPoint();
        location = location.add(velocity.scale(delta));
        longevity -= delta;
        if (longevity < 0) {
            GameScreen.getStuff().removeActor(this);
        }

        //TODO: Check if touching something (other than player who shot me) and deal damage
    }

    @Override
    public void dispose() {
        //Do nothing; this texture is obtained from the TextureCreator, which reuses them.
    }
}
