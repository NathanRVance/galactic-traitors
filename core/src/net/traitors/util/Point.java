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
    public String toString() {
        return "X: " + x + ", Y: " + y;
    }

}
