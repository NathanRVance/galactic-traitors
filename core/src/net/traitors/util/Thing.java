package net.traitors.util;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Thing {

    /**
     * Get the center of mass of this thing
     *
     * @return center of mass
     */
    Point getPoint();

    /**
     * Set the center fo mass of this thing
     *
     * @param point center of mass
     */
    void setPoint(Point point);

    /**
     * Get the current rotation of this thing
     *
     * @return rotation in radians
     */
    float getRotation();

    /**
     * Set the current rotation of this thing
     *
     * @param rotation rotation in radians
     */
    void setRotation(float rotation);

    float getWidth();

    float getHeight();

    /**
     * Draw this thing using its point, rotation, width, and height
     *
     * @param batch batch used to draw this thing
     */
    void draw(Batch batch);

}
