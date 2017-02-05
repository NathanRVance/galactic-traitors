package net.traitors.thing.platform;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.util.Point;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NullPlatform implements Platform {
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
    public Point getPoint() {
        throw new NotImplementedException();
    }

    @Override
    public void setPoint(Point point) {
        throw new NotImplementedException();
    }

    @Override
    public Point getWorldPoint() {
        throw new NotImplementedException();
    }

    @Override
    public void setWorldPoint(Point point) {
        throw new NotImplementedException();
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public void setRotation(float rotation) {
        throw new NotImplementedException();
    }

    @Override
    public float getWorldRotation() {
        return 0;
    }

    @Override
    public void setWorldRotation(float rotation) {
        throw new NotImplementedException();
    }

    @Override
    public Point getWorldVelocity() {
        return new Point();
    }

    @Override
    public float getWidth() {
        throw new NotImplementedException();
    }

    @Override
    public float getHeight() {
        throw new NotImplementedException();
    }

    @Override
    public Platform getPlatform() {
        throw new NotImplementedException();
    }

    @Override
    public void setPlatform(Platform platform) {
        throw new NotImplementedException();
    }

    @Override
    public void draw(Batch batch) {
        throw new NotImplementedException();
    }

    @Override
    public boolean contains(Point point) {
        return true;
    }

    @Override
    public void act(float delta) {
        throw new NotImplementedException();
    }
}
