package net.traitors.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import net.traitors.thing.platform.Platform;

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
            return new Point((float) Math.cos(angle) * distance, (float) Math.sin(angle) * distance);
        }
        return this;
    }

    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    public Point subtract(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    public Point scale(float amnt) {
        return new Point(x * amnt, y * amnt);
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    /**
     * Converts from screen coordinates to world coordinates
     *
     * @param camera to perform the unprojection
     * @return unprojected point
     */
    public Point unproject(Camera camera) {
        Vector3 v = new Vector3(x, y, 0);
        camera.unproject(v);
        return new Point(v.x, v.y);
    }

    /**
     * Converts from world coordinates to screen coordinates
     *
     * @param camera to perform the projection
     * @return projected point
     */
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

    //Speshul fysixy shtuffsh bellow

    /**
     * This point is the force
     * @param radius the radius this force is applied on
     * @param platform the platform this force is applied on
     * @return the angular acceleration in s^-2
     */
    public float angAccel(Point radius, Platform platform) {
        // radius X force = torque
        //   [x1,        y1,        z1]
        // X [x2,        y2,        z2]
        // = [y1z2-z1y2, z1x2-x1z2, x1y2-y1x2]
        // We only care about the z component of the cross product for torque
        float torque = radius.x * y - radius.y * x;
        // torque = moment of inertia * angular acceleration
        // moment of inertia for thin rectangular plate = m * (h^2 + w^2) / 12
        float I = platform.getMass() * (platform.getHeight() * platform.getHeight() + platform.getWidth() * platform.getWidth()) / 12;
        return torque / I;
    }

    /**
     * This point is the force
     * @param platform the platforme this force is applied on
     * @return the translational acceleration, in world coordinates, in m/s^2
     */
    public Point transAccel(Platform platform) {
        return scale(1 / platform.getMass()).rotate(platform.getWorldRotation());
    }
}
