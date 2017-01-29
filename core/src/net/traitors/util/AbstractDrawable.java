package net.traitors.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;

public abstract class AbstractDrawable implements Drawable, Disposable {

    private BaseDrawable baseDrawable = new BaseDrawable();
    private Point point = new Point();
    private final float width;
    private final float height;

    public AbstractDrawable(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        draw(batch);
    }

    public abstract void draw(Batch batch);

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public float getLeftWidth() {
        return baseDrawable.getLeftWidth();
    }

    @Override
    public void setLeftWidth(float leftWidth) {
        baseDrawable.setLeftWidth(leftWidth);
    }

    @Override
    public float getRightWidth() {
        return baseDrawable.getRightWidth();
    }

    @Override
    public void setRightWidth(float rightWidth) {
        baseDrawable.setRightWidth(rightWidth);
    }

    @Override
    public float getTopHeight() {
        return baseDrawable.getTopHeight();
    }

    @Override
    public void setTopHeight(float topHeight) {
        baseDrawable.setTopHeight(topHeight);
    }

    @Override
    public float getBottomHeight() {
        return baseDrawable.getBottomHeight();
    }

    @Override
    public void setBottomHeight(float bottomHeight) {
        baseDrawable.setBottomHeight(bottomHeight);
    }

    @Override
    public float getMinWidth() {
        return baseDrawable.getMinWidth();
    }

    @Override
    public void setMinWidth(float minWidth) {
        baseDrawable.setMinWidth(minWidth);
    }

    @Override
    public float getMinHeight() {
        return baseDrawable.getMinHeight();
    }

    @Override
    public void setMinHeight(float minHeight) {
        baseDrawable.setMinHeight(minHeight);
    }
}
