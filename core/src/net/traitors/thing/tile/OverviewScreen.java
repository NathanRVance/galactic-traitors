package net.traitors.thing.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
            myCamera.syncRotations();
            myCamera.zoom = 25;
            myCamera.update();
            //Set the batch to use the camera
            batch.setProjectionMatrix(myCamera.combined);
            //Start the framebuffer
            frameBuffer.begin();
            //Clear out the framebuffer
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            //Start the batch
            batch.begin();
            //Draw everything to our framebuffer
            GameScreen.getStuff().drawStuff(batch, myCamera);
            batch.end();
            frameBuffer.end();
            //Reset things so the rest of the render cycle isn't messed up
            batch.setProjectionMatrix(GameScreen.getStuff().getCamera().combined);
            batch.begin();
            //Get a sprite from the framebuffer and draw it
            Sprite sprite = new Sprite(frameBuffer.getColorBufferTexture());
            sprite.flip(false, true);
            batch.draw(sprite, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, getWorldRotation() * MathUtils.radiansToDegrees);
            //Clear out the framebuffer
            FrameBuffer.clearAllFrameBuffers(Gdx.app);
            drawingMyself = false;
        }
    }

    @Override
    public void dispose() {
        frameBuffer.dispose();
    }
}
