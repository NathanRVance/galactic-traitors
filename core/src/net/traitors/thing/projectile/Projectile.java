package net.traitors.thing.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.Layer;
import net.traitors.thing.AbstractThing;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;
import net.traitors.util.save.SaveData;

public class Projectile extends AbstractThing {

    private Point velocity;
    private float longevity;
    //Keep track of our own location so that we don't rotate/move with our platform
    private Point location;
    private Color color;

    public Projectile(Layer layer, float width, float height, Color color, Point start, Point velocity, float longevity) {
        super(layer, width, height);
        setPoint(start);
        this.location = start;
        this.velocity = velocity;
        this.longevity = longevity;
        this.color = color;
    }

    @Override
    public SaveData getSaveData() {
        SaveData sd = super.getSaveData();
        sd.writePoint(velocity);
        sd.writeFloat(longevity);
        sd.writePoint(location);
        sd.writeInt(Color.rgba8888(color));
        return sd;
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        velocity = saveData.readPoint();
        longevity = saveData.readFloat();
        location = saveData.readPoint();
        color = new Color(saveData.readInt());
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(TextureCreator.getColorRec(color), location.x, location.y,
                0, 0, getWidth(), getHeight(), 1, 1,
                velocity.angle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        location = location.add(velocity.scale(delta));
        longevity -= delta;
        if (longevity < 0) {
            getLayer().removeActor(this);
        }

        //TODO: Check if touching something (other than player who shot) and deal damage
    }

    @Override
    public void dispose() {
        //Do nothing; this texture is obtained from the TextureCreator, which reuses them.
    }
}
