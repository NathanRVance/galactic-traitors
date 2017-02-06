package net.traitors.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class Point {

    public float x;
    public float y;

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

    public Point duplicate() {
        return new Point(x, y);
    }

    public float distance(Point other) {
        return (float) Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public float distanceFromZero() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Computes the angle of this point. 0 is to the right.
     *
     * @return angle in radians
     */
    public float angle() {
        float angle = (float) Math.asin(y / distanceFromZero());
        if (x < 0) angle = (float) Math.PI - angle;
        return angle;
    }

    /**
     * Rotate this point around (0, 0) by the specified amount
     *
     * @param rotation amount to rotate, in radians
     * @return rotated point (this)
     */
    public Point rotate(float rotation) {
        if (!isZero()) {
            float angle = angle() + rotation;
            float distance = distanceFromZero();
            x = (float) Math.cos(angle) * distance;
            y = (float) Math.sin(angle) * distance;
        }
        return this;
    }

    public Point add(Point other) {
        x += other.x;
        y += other.y;
        return this;
    }

    public Point subtract(Point other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    public Point scale(float amnt) {
        x *= amnt;
        y *= amnt;
        return this;
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public Point unproject(Camera camera) {
        Vector3 v = new Vector3(x, y, 0);
        camera.unproject(v);
        x = v.x;
        y = v.y;
        return this;
    }

    public Point project(Camera camera) {
        Vector3 v = new Vector3(x, y, 0);
        camera.project(v);
        x = v.x;
        y = v.y;
        return this;
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
