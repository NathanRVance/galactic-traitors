package net.traitors.thing.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;

public class Projectile extends AbstractThing {

    private Point velocity;
    private TextureRegion texture;
    private float longevity;
    private Point location;

    public Projectile(float width, float height, Color color, Point start, Point velocity, float longevity) {
        super(width, height);
        setPoint(start);
        location = null;
        this.velocity = velocity.duplicate();
        this.longevity = longevity;

        int dim = 10;
        Pixmap pixmap = new Pixmap(dim, dim, Pixmap.Format.RGBA4444);
        pixmap.setColor(color);
        pixmap.fill();
        texture = new TextureRegion(new Texture(pixmap));
    }

    @Override
    public void draw(Batch batch) {
        Point drawLocation = (location == null)? getWorldPoint() : location;
        batch.draw(texture, drawLocation.x, drawLocation.y,
                0, 0, getWidth(), getHeight(), 1, 1,
                velocity.angle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(location == null) location = getWorldPoint();
        location.add(velocity.duplicate().scale(delta));
        longevity -= delta;
        if(longevity < 0) {
            texture.getTexture().dispose();
            GameScreen.getStuff().removeActor(this);
        }

        //TODO: Check if touching something (other than player who shot me) and deal damage
    }
}
