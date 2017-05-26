package net.traitors.ui.ScreenElements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GalacticTraitors;
import net.traitors.Layer;
import net.traitors.controls.MouseoverCallback;
import net.traitors.thing.AbstractThing;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.TextureCreator;

class Compass extends AbstractThing implements Selectable, MouseoverCallback {

    private boolean selected = false;
    private int trackDepth;
    private SelectableSwitch<Compass> selectableSwitch;

    //Variables for dragging
    private float startX = 0;
    private boolean doingDrag = false;

    Compass(Layer layer, SelectableSwitch<Compass> selectableSwitch, float dim) {
        super(layer, dim, dim);
        this.selectableSwitch = selectableSwitch;
    }

    @Override
    public void mouseEnter() {

    }

    @Override
    public void mouseExit() {

    }

    @Override
    public boolean mouseDown(Point touchLoc) {
        GalacticTraitors.getCamera().syncRotations();
        selectableSwitch.selectableTapped(this, true);
        startX = touchLoc.x;
        return true;
    }

    @Override
    public boolean mouseDragged(Point touchLoc) {
        float currentX = touchLoc.x;
        if (Math.abs(startX - currentX) > getWidth() / 10) doingDrag = true;
        if (doingDrag) {
            GalacticTraitors.getCamera().setOffset((float) (currentX / getWidth() * Math.PI * 2 + Math.PI));
        }
        return false;
    }

    @Override
    public boolean mouseUp() {
        doingDrag = false;
        return false;
    }

    void setTrackDepth(int trackDepth) {
        this.trackDepth = trackDepth;
        if (GalacticTraitors.getCamera().getRotateDepth() == trackDepth || isSelected())
            select();
    }

    @Override
    public void draw(Batch batch) {
        TextureRegion compass = (isSelected()) ? TextureCreator.getCompass(Color.BLUE) : TextureCreator.getCompass(Color.GRAY);
        BetterCamera camera = GalacticTraitors.getCamera();
        batch.draw(compass, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2,
                getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1,
                (-camera.getCameraAngle() + camera.getThingAtDepth(trackDepth).getWorldRotation()) * MathUtils.radiansToDegrees);


        float needleWidth = getWidth() / 25;
        batch.draw(TextureCreator.getColorRec(Color.RED), getWorldPoint().x - needleWidth / 2, getWorldPoint().y, needleWidth, getHeight() / 3);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void select() {
        GalacticTraitors.getCamera().setRotateDepth(trackDepth);
        selected = true;
    }

    @Override
    public void unselect() {
        selected = false;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

}
