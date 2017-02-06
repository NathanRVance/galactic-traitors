package net.traitors.thing.tile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GameScreen;
import net.traitors.thing.AbstractThing;
import net.traitors.util.BetterCamera;

public class OverviewScreen extends AbstractThing implements Tile {

    private static boolean drawingMyself;

    public OverviewScreen(float width, float height) {
        super(width, height);
    }

    @Override
    public void draw(Batch batch) {
        //Detect that an overview screen isn't currently doing the drawing
        if (!drawingMyself) {
            drawingMyself = true;
            //End the old batch
            batch.end();
            //Make a frame buffer to draw to
            int resolution = 200;
            int width = (int) (resolution * getWidth());
            int height = (int) (resolution * getHeight());
            FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
            //Create a new camera
            BetterCamera camera = new BetterCamera();
            camera.setToOrtho(false, getWidth(), getHeight());
            camera.translate(getWorldPoint().x - camera.position.x, getWorldPoint().y - camera.position.y);
            camera.rotateWith(this);
            camera.syncRotations();
            camera.zoom = 25;
            camera.update();
            //Set the batch to use the camera
            batch.setProjectionMatrix(camera.combined);
            //Start the framebuffer and batch
            frameBuffer.begin();
            batch.begin();
            //Draw everything to our framebuffer
            GameScreen.getStuff().drawStuff(batch);
            batch.end();
            frameBuffer.end();
            //Reset things so the rest of the render cycle isn't messed up
            batch.setProjectionMatrix(GameScreen.getStuff().getCamera().combined);
            batch.begin();
            //Get a sprite from the framebuffer and draw it
            Sprite sprite = new Sprite(frameBuffer.getColorBufferTexture());
            sprite.flip(false, true);
            batch.draw(sprite, getWorldPoint().x - getWidth() / 2, getWorldPoint().y - getHeight() / 2, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, getWorldRotation() * MathUtils.radiansToDegrees);
            drawingMyself = false;
        }
    }
}
