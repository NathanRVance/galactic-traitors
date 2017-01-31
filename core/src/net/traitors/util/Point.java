package net.traitors.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class Point {

    public final float x;
    public final float y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(Vector3 vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public float distance(Point other) {
        return (float) Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public float distanceFromZero() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Computes the angle of this point as it would be in polar coordinates
     *
     * @return angle in radians
     */
    public float angle() {
        float angle = (float) Math.asin((y) / distanceFromZero());
        if (x < 0) angle = (float) Math.PI - angle;
        return angle;
    }

    /**
     * Rotate this point around (0, 0) by the specified amount
     *
     * @param rotation amount to rotate, in radians
     * @return rotated point
     */
    public Point rotate(float rotation) {
        if(isZero()) return this;
        float angle = angle() + rotation;
        float distance = distanceFromZero();
        return new Point((float) Math.cos(angle) * distance, (float) Math.sin(angle) * distance);
    }

    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    public Point subtract(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public Point unproject(Camera camera) {
        Vector3 v = new Vector3(x, y, 0);
        camera.unproject(v);
        return new Point(v.x, v.y);
    }

    public Point project(Camera camera) {
        Vector3 v = new Vector3(x, y, 0);
        camera.project(v);
        return new Point(v.x, v.y);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Point)) return false;
        Point o = (Point) other;
        return x == o.x && y == o.y;
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y;
    }

}
