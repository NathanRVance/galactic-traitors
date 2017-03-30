package net.traitors.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.ui.TextView;
import net.traitors.util.Point;

public class Button extends Widget implements Disposable {

    TextView tv;
    private String text;
    private Point lowerLeft, upperRight;
    private boolean selected = false;
    private Texture unselectedTexture;
    private Texture selectedTexture;

    public Button(TextView tv, String text, Point lowerLeft, Point upperRight, final Runnable onClick) {
        this.tv = tv;
        this.text = text;
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;

        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                selected = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                selected = false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true; //consume so I get notified on touchUp
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onClick.run();
            }
        });

        unselectedTexture = makeTexture(Color.DARK_GRAY, Color.LIGHT_GRAY);
        selectedTexture = makeTexture(Color.CYAN, Color.LIGHT_GRAY);
    }

    private Texture makeTexture(Color border, Color center) {
        int width = (int) ((upperRight.x - lowerLeft.x) * 100);
        int height = (int) ((upperRight.y - lowerLeft.y) * 100);
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA4444);
        pixmap.setColor(border);
        pixmap.fill();

        pixmap.setColor(center);
        int edgeThickness = height / 10;
        pixmap.fillRectangle(edgeThickness, edgeThickness, width - edgeThickness * 2, height - edgeThickness * 2);

        return new Texture(pixmap);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Texture button = (selected)? selectedTexture : unselectedTexture;
        batch.draw(button, lowerLeft.x, lowerLeft.y, upperRight.x - lowerLeft.x, upperRight.y - lowerLeft.y);
        tv.drawStringInWorld(text, new Point((lowerLeft.x + upperRight.x) / 2, (lowerLeft.y + upperRight.y) / 2), TextView.Align.center,
                (upperRight.x - lowerLeft.x) * .8f);
    }

    @Override
    public void dispose() {
        unselectedTexture.dispose();
        selectedTexture.dispose();
    }

}
