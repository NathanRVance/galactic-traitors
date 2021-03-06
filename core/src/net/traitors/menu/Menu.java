package net.traitors.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import net.traitors.GalacticTraitors;
import net.traitors.Layer;
import net.traitors.controls.MouseoverCallback;
import net.traitors.thing.platform.AbstractPlatform;
import net.traitors.thing.platform.Platform;
import net.traitors.ui.TextView;
import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AbstractPlatform implements MouseoverCallback {

    private static final float buttonSpacing = .1f;
    private static final List<Menu> OTHERS = new ArrayList<>();
    private float titleSpacing;
    private CharSequence title;
    private List<Button> buttons;
    private TextureRegion background;

    private Menu(Layer layer, float width, float height, CharSequence title, List<Button> buttons, Platform platform) {
        super(layer, width, height);
        this.title = title;
        this.buttons = buttons;
        super.setPlatform(platform);

        int w = (int) (width * 100);
        int h = (int) (height * 100);
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.CYAN);
        pixmap.fill();
        pixmap.setColor(Color.GRAY);
        int border = 5;
        pixmap.fillRectangle(border, border, w - border * 2, h - border * 2);
        background = new TextureRegion(new Texture(pixmap));
    }

    private void init() {
        float yoffset = super.getHeight() / 2 - titleSpacing;
        for (Button button : buttons) {
            button.setPlatformPackagePrivate(this);
            button.setPoint(new Point(0, yoffset - button.getHeight() / 2));
            GalacticTraitors.getInputProcessor().addCallback(button);
            yoffset -= buttonSpacing + button.getHeight();
        }
        GalacticTraitors.getInputProcessor().addCallback(this);
        synchronized (OTHERS) {
            //THERE CAN ONLY BE ONE
            for (Menu menu : OTHERS) {
                menu.dismiss();
            }
            OTHERS.clear();
            OTHERS.add(this);
        }
    }

    private void dismiss() {
        GalacticTraitors.getInputProcessor().removeCallback(this);
        getLayer().removeActor(this);
        dispose();
    }

    @Override
    public void setPlatform(Platform platform) {
        //Do nothing
    }

    @Override
    public void draw(Batch batch) {
        Point p = getWorldPoint();
        float rotation = getWorldRotation();
        batch.draw(background, p.x - super.getWidth() / 2, p.y - super.getHeight() / 2,
                super.getWidth() / 2, super.getHeight() / 2, super.getWidth(), super.getHeight(),
                1, 1, rotation * MathUtils.radiansToDegrees);
        GalacticTraitors.getTextView().drawStringInWorld(title,
                new Point(0, super.getHeight() / 2 - titleSpacing / 3).rotate(rotation).add(p),
                TextView.Align.center, super.getWidth() * .9f, .3f, Color.BLACK, getWorldRotation(), getLayer());
        for (Button button : buttons) {
            button.draw(batch);
        }
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void dispose() {
        background.getTexture().dispose();
        for (Button button : buttons) {
            button.dispose();
        }
    }

    @Override
    public void mouseEnter() {
        //Do nothing
    }

    @Override
    public void mouseExit() {
        //Do nothing
    }

    @Override
    public boolean mouseDown(Point touchLoc) {
        return true; //Consume
    }

    @Override
    public boolean mouseDragged(Point touchLoc) {
        return false;
    }

    @Override
    public boolean mouseUp() {
        return true; //Consume
    }

    public static class MenuBuilder {

        private final float width;
        private List<CharSequence> buttonText = new ArrayList<>();
        private List<Runnable> buttonActions = new ArrayList<>();
        private CharSequence closeButtonText;
        private Runnable closeButtonAction;
        private Platform platform;

        public MenuBuilder(float width) {
            this.width = width;
        }

        public MenuBuilder addButton(CharSequence text, Runnable action) {
            buttonText.add(text);
            buttonActions.add(action);
            return this;
        }

        public MenuBuilder setPlatform(Platform platform) {
            this.platform = platform;
            return this;
        }

        public MenuBuilder setCloseButtonText(CharSequence text) {
            closeButtonText = text;
            return this;
        }

        public MenuBuilder setCloseButtonAction(Runnable action) {
            closeButtonAction = action;
            return this;
        }

        public Menu build(Layer layer, CharSequence title) {
            //Admitately arbitrary value to multiply by, could be function of font size
            float titleSpacing = calcButtonHeight(title) * 1.375f;
            float height = titleSpacing;
            height += (buttonText.size() + 1) * buttonSpacing; //spacing between buttons
            List<Button> buttons = new ArrayList<>();
            for (int i = 0; i < buttonText.size(); i++) {
                CharSequence text = buttonText.get(i);
                float h = calcButtonHeight(text.toString());
                height += h;
                buttons.add(new Button(layer, calcButtonWidth(), calcButtonHeight(text.toString()), text, buttonActions.get(i)));
            }
            CharSequence closeButtonText = this.closeButtonText == null ? "Done" : this.closeButtonText;
            float closeButtonHeight = calcButtonHeight(closeButtonText);
            height += closeButtonHeight;
            final Menu menu = new Menu(layer, width, height, title, buttons, platform);
            menu.titleSpacing = titleSpacing;
            Button b = new Button(layer, calcButtonWidth(), closeButtonHeight, closeButtonText, closeButtonAction == null ? new Runnable() {
                @Override
                public void run() {
                    menu.dismiss();
                }
            } : closeButtonAction);
            menu.buttons.add(b);
            menu.init();
            return menu;
        }

        private float calcButtonWidth() {
            return width * .9f;
        }

        private float calcButtonHeight(CharSequence text) {
            return .4f * (text.length() / 13 + 1);
        }

    }

}
