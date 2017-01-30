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

    public float distance(Point other) {
        return (float) Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public float distanceFromZero() {
        return (float) Math.sqrt(x * x + y * y);
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
    public String toString() {
        return "X: " + x + ", Y: " + y;
    }

}