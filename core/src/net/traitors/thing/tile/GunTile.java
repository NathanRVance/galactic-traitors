package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.AbstractThing;
import net.traitors.thing.usable.ProjectileFactory;
import net.traitors.thing.usable.Usable;
import net.traitors.thing.player.Player;
import net.traitors.util.Point;

public class GunTile extends AbstractThing implements Tile, Usable {

    private Tile base;
    private TextureRegion dome;
    private TextureRegion barrel;
    private float rotation = 0;
    private ProjectileFactory projectileFactory;

    public GunTile(float width, float height, float rotation, Tile base) {
        super(width, height);
        setRotation(rotation);
        this.base = base;

        int domeExtent = 200;
        int domeWidth = 400;
        Pixmap pixmap = new Pixmap(domeExtent, domeWidth, Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.GRAY);
        pixmap.fillCircle(0, domeWidth / 2, domeExtent);
        dome = new TextureRegion(new Texture(pixmap));

        int barrelLength = 10;
        pixmap = new Pixmap(barrelLength, barrelLength, Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        barrel = new TextureRegion(new Texture(pixmap));

        projectileFactory = new ProjectileFactory(.2f, new Point(getWidth() * 2, getHeight() / 2), .2f, .7f, 20, Color.RED, 5);
    }

    @Override
    public void draw(Batch batch) {
        Point worldPointLowLeft = getWorldPoint().subtract(new Point(getWidth() / 2, getHeight() / 2));
        //draw base
        base.setPlatform(getPlatform());
        base.setRotation(getRotation());
        base.setPoint(getPoint());
        base.draw(batch);
        //draw barrel
        float barrelLen = getHeight();
        float barrelWidth = barrelLen / 10;
        Point barrelp = new Point(getWidth() * 1.1f, getHeight() / 2 - barrelWidth / 2);
        batch.draw(barrel,
                worldPointLowLeft.x + barrelp.x,
                worldPointLowLeft.y + barrelp.y,
                0,
                barrelWidth / 2,
                barrelLen, barrelWidth, 1, 1, rotation * MathUtils.radiansToDegrees);
        //draw dome
        float domeWidth = getHeight();
        float domeExtent = domeWidth / 2;
        Point domep = new Point(getWidth(), getHeight() / 2 - domeWidth / 2);
        batch.draw(dome,
                worldPointLowLeft.x + domep.x,
                worldPointLowLeft.y + domep.y,
                getWidth() / 2 - domep.x,
                getHeight() / 2 - domep.y,
                domeExtent, domeWidth, 1, 1, rotation * MathUtils.radiansToDegrees);
    }

    @Override
    public void use(Player player) {
        rotation = player.getWorldRotation();
        projectileFactory.use(player);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        projectileFactory.updateCooldown(delta);
    }

    @Override
    public float getCooldownPercent() {
        return projectileFactory.getCooldownPercent();
    }
}
