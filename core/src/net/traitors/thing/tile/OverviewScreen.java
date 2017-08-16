package net.traitors.thing.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GalacticTraitors;
import net.traitors.GameFactory;
import net.traitors.Layer;
import net.traitors.controls.Controls;
import net.traitors.menu.Menu;
import net.traitors.thing.AbstractThing;
import net.traitors.thing.Thing;
import net.traitors.thing.platform.ship.Ship;
import net.traitors.thing.platform.ship.ShipComponent;
import net.traitors.thing.usable.FirstUseUsable;
import net.traitors.thing.usable.StringStrategy;
import net.traitors.util.BetterCamera;
import net.traitors.util.Point;
import net.traitors.util.save.SaveData;

public class OverviewScreen extends AbstractThing implements ShipComponent {

    private static boolean drawingMyself = false;
    private BetterCamera myCamera = new BetterCamera();
    private FrameBuffer frameBuffer;
    private Ship ship;
    private final FirstUseUsable usable = new FirstUseUsable(GameFactory.getScreenLayer()) {
        @Override
        protected void firstUse(Thing user, Point touchPoint) {
            Menu menu = new Menu.MenuBuilder(2)
                    .addButton(new StringStrategy() {
                                   @Override
                                   public String toString() {
                                       return "Autostop (" + (ship.getComputer().isAutostop() ? "on" : "off") + ")";
                                   }
                               },
                            new Runnable() {
                                @Override
                                public void run() {
                                    ship.getComputer().toggleAutostop();
                                }
                            })
                    .addButton("Heading", new Runnable() {
                        @Override
                        public void run() {
                            Menu menu = new Menu.MenuBuilder(2)
                                    .build(getLayer(), new StringStrategy() {
                                        @Override
                                        public String toString() {
                                            return "Velocity: " + ship.getTranslationalVelocity()
                                                    + "\nRotating at: " + ship.getRotationalVelocity();
                                        }
                                    });
                            menu.setPoint(getLayer().getBotCorner().add(new Point(getLayer().getWidth() / 2, getLayer().getHeight() / 2)));
                            menu.setRotation(0);
                            getLayer().addActor(menu);
                        }
                    }).build(getLayer(), "Ship Control");
            menu.setPoint(getLayer().getBotCorner().add(new Point(getLayer().getWidth() / 2, getLayer().getHeight() / 2)));
            menu.setRotation(0);
            getLayer().addActor(menu);
        }
    };

    public OverviewScreen(Layer layer, float width, float height) {
        super(layer, width, height);
        if(width != 0 && height != 0)
            setup();
    }

    @Override
    public void loadSaveData(SaveData saveData) {
        super.loadSaveData(saveData);
        setup();
    }

    private void setup() {
        int resolution = 100;
        int w = (int) (resolution * getWidth());
        int h = (int) (resolution * getHeight());
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA4444, w, h, false);
    }

    @Override
    public void draw(Batch batch) {
        if (frameBuffer == null) setup();
        //Detect that an overview screen isn't currently doing the drawing
        if (!drawingMyself) {
            drawingMyself = true;
            //End the old batch
            batch.end();
            //Sync up my camera
            myCamera.setToOrtho(false, getWidth(), getHeight());
            myCamera.syncRotations();
            myCamera.zoom = 25;
            myCamera.translate(getWorldPoint().x - myCamera.position.x, getWorldPoint().y - myCamera.position.y);
            myCamera.rotateTo(getWorldRotation());
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
            ship.getLayer().draw(myCamera);
            batch.end();
            frameBuffer.end();
            //Reset things so the rest of the render cycle isn't messed up
            batch.setProjectionMatrix(GalacticTraitors.getCamera().combined);
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

    @Override
    public void use(Thing user, Point touchPoint) {
        if(user.getID() == Controls.ID)
            usable.use(user, touchPoint);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        usable.act(delta);
    }

    @Override
    public float getCooldownPercent() {
        return 0;
    }

    @Override
    public void setShip(Ship ship) {
        this.ship = ship;
    }
}
