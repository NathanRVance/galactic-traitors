package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.usable.Usable;
import net.traitors.util.PixmapRotateRec;
import net.traitors.util.Point;

public class ThrusterTile extends AbstractThing implements Tile, Usable {

    private float rotation = 0;
    private Tile base;
    private TextureRegion cone;
    private RotationStrategy rotationStrategy;

    public ThrusterTile(float width, float height, float rotation, Tile base) {
        super(width, height);
        setRotation(rotation);
        this.rotation = rotation;
        this.base = base;

        int coneDim = 100;
        PixmapRotateRec pixmap = new PixmapRotateRec(coneDim, coneDim, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fillQuadrahedron(0, coneDim / 3, coneDim, 0, coneDim, coneDim, 0, coneDim * 2 / 3);
        cone = new TextureRegion(new Texture(pixmap));

        rotationStrategy = new RotationStrategy((float) Math.PI * 2 / 7);
    }

    @Override
    public void draw(Batch batch) {
        Point worldPoint = getWorldPoint();
        float rot = getPlatform().getWorldRotation() + getRotation();
        //draw cone
        float coneWidth = getWidth();
        float coneLen = getWidth() / 2;
        Point conep = new Point(getWidth() / 2 * .8f, 0);
        conep = conep.rotate(rot);
        batch.draw(cone,
                worldPoint.x + conep.x,
                worldPoint.y + conep.y - coneWidth / 2,
                0,
                coneWidth / 2,
                coneLen, coneWidth, 1, 1, rotation * MathUtils.radiansToDegrees);
        //draw base
        base.setPlatform(getPlatform());
        base.setRotation(getRotation());
        base.setPoint(getPoint());
        base.draw(batch);
    }

    @Override
    public void use(Thing user) {
        rotation = rotationStrategy.getRotation(user.getWorldRotation(), getWorldRotation());
    }

    @Override
    public float getCooldownPercent() {
        return 100;
    }
}
