package net.traitors.thing.platform;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.BetterCamera;
import net.traitors.util.Point;

public class NullPlatform implements Platform {
    private static final long serialVersionUID = 3113137130998697554L;

    @Override
    public Point getTranslationalVelocity() {
        return new Point();
    }

    @Override
    public void setTranslationalVelocity(Point velocity) {
        //Do nothing
    }

    @Override
    public float getRotationalVelocity() {
        return 0;
    }

    @Override
    public void setRotationalVelocity(float velocity) {
        //Do nothing
    }

    @Override
    public Point convertToWorldCoordinates(Point point) {
        return point;
    }

    @Override
    public Point convertToPlatformCoordinates(Point point) {
        return point;
    }

    @Override
    public float convertToWorldRotation(float rotation) {
        return rotation;
    }

    @Override
    public float convertToPlatformRotation(float rotation) {
        return rotation;
    }

    @Override
    public void applyForce(Point force, Point position, float delta) {

    }

    @Override
    public Point getPoint() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPoint(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Point getWorldPoint() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWorldPoint(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public void setRotation(float rotation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getWorldRotation() {
        return 0;
    }

    @Override
    public void setWorldRotation(float rotation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Point getWorldVelocity() {
        return new Point();
    }

    @Override
    public float getWidth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getHeight() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getMass() {
        return 0;
    }

    @Override
    public Platform getPlatform() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPlatform(Platform platform) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Batch batch, BetterCamera camera) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Batch batch) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Point point) {
        return true;
    }

    @Override
    public void act(float delta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispose() {

    }
}
