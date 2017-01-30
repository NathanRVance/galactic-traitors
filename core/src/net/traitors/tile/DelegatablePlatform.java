package net.traitors.tile;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.Point;

public class DelegatablePlatform implements Platform {

    private Point translationalVelocity = new Point();
    private float rotationalVelocity = 0f;

    private Point position = new Point();
    private float rotation = 0f;

    private Platform platform = null;

    @Override
    public Point getTranslationalVelocity() {
        return translationalVelocity;
    }

    @Override
    public void setTranslationalVelocity(Point velocity) {
        translationalVelocity = velocity;
    }

    @Override
    public float getRotationalVelocity() {
        return rotationalVelocity;
    }

    @Override
    public void setRotationalVelocity(float velocity) {
        rotationalVelocity = velocity;
    }

    @Override
    public void move(float delta) {
        //These methods handle moving the things in stuff
        setPoint(new Point(position.x + translationalVelocity.x * delta, position.y + translationalVelocity.y * delta));
        setRotation((rotation + rotationalVelocity * delta) % (float) (Math.PI * 2));
    }

    @Override
    public Point convertToWorldCoordinates(Point point) {
        //First convert to the coordinates of the platform one level up (if there is one)
        //Resolve rotation induced differences
        if (!point.equals(new Point())) {
            float d = point.distance(new Point());
            float angle = (float) Math.asin((point.y) / d);
            if (point.x < 0) angle = (float) Math.PI - angle;
            angle += rotation;
            point = new Point((float) Math.cos(angle) * d, (float) Math.sin(angle) * d);
        }
        //And translation
        point = new Point(point.x + position.x, point.y + position.y);

        //Then, convert from that platform's coordinates
        if (platform != null) {
            return platform.convertToWorldCoordinates(point);
        } else {
            return point;
        }
    }

    @Override
    public float convertToWorldRotation(float rotation) {
        rotation = (rotation + this.rotation) % (float) (Math.PI * 2);
        if(platform != null) {
            return platform.convertToWorldRotation(rotation);
        } else {
            return rotation;
        }
    }

    @Override
    public Point getPoint() {
        return position;
    }

    @Override
    public void setPoint(Point point) {
        this.position = point;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    //We don't care about stuff below here. The class that delegates to this class should handle them.

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void draw(Batch batch) {

    }
}
