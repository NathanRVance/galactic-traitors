package net.traitors.thing.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.AbstractThing;
import net.traitors.util.BetterCamera;

public class OverviewScreen extends AbstractThing implements Tile {

    private static boolean drawingMyself = false;
    private BetterCamera myCamera = new BetterCamera();
    private FrameBuffer frameBuffer;

    public OverviewScreen(float width, float height) {
        super(width, height);
        myCamera.setToOrtho(false, getWidth(), getHeight());
        myCamera.rotateWith(this);
        myCamera.syncRotations();
        myCamera.zoom = 25;

        int resolution = 100;
        int w = (int) (resolution * getWidth());
        int h = (int) (resolution * getHeight());
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA4444, w, h, false);
    }

    @Override
    public void draw(Batch batch) {
        //Detect that an overview screen isn't currently doing the drawing
        if (!drawingMyself) {
            drawingMyself = true;
            //End the old batch
            batch.end();
            //Sync up my camera
            myCamera.translate(getWorldPoint().x - myCamera.position.x, getWorldPoint().y - myCamera.position.y);
            myCamera.act(0);
            myCamera.update();
            //Set the batch to use my camera
            batch.setProjectionMatrix(myCamera.combined);
            //Start the framebuffer
            frameBuffer.begin();
            //Clear out the framebuffer
            Gdx.gl.glClearColor(0, 0, 0, 1); //black background
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            //Start the batch
            batch.begin();
            //Draw everything to the framebuffer
            GameScreen.getStuff().drawStuff(batch, myCamera);
            batch.end();
            frameBuffer.end();
            //Reset things so the rest of the render cycle isn't messed up
            batch.setProjectionMatrix(GameScreen.getStuff().getCamera().combined);
            batch.begin();
            //Draw the framebuffer
            batch.draw(new TextureRegion(frameBuffer.getColorBufferTexture()),
                    getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2,
                    getWidth() / 2, getHeight() / 2,
                    getWidth(), getHeight(),
                    1, -1, //flip vertically
                    getWorldRotation() * MathUtils.radiansToDegrees);
            drawingMyself = false;
        }
    }

    @Override
    public void dispose() {
        frameBuffer.dispose();
    }
}
