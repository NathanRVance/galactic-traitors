package net.traitors.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.GalacticTraitors;
import net.traitors.GameScreen;
import net.traitors.thing.platform.AbstractPlatform;
import net.traitors.ui.TextView;
import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AbstractPlatform {

    private static final float buttonSpacing = .1f;
    private static final List<Menu> OTHERS = new ArrayList<>();
    private String title;
    private List<Button> buttons;
    private Texture background;

    private Menu(float width, float height, String title, List<Button> buttons) {
        super(width, height);
        this.title = title;
        this.buttons = buttons;

        int w = (int) (width * 100);
        int h = (int) (height * 100);
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.CYAN);
        pixmap.fill();
        pixmap.setColor(Color.GRAY);
        int border = 5;
        pixmap.fillRectangle(border, border, w - border * 2, h - border * 2);
        background = new Texture(pixmap);
    }

    private void init() {
        float yoffset = super.getHeight() / 2 - .3f;
        for (Button button : buttons) {
            button.setPlatform(this);
            button.setPoint(new Point(0, yoffset - button.getHeight()));
            GalacticTraitors.getInputProcessor().addCallback(button);
            yoffset -= buttonSpacing + button.getHeight();
        }
        synchronized (OTHERS) {
            //THERE CAN ONLY BE ONE
            for (Menu menu : OTHERS) {
                menu.dismiss();
            }
            OTHERS.add(this);
        }
    }

    private void dismiss() {
        GameScreen.getStuff().removeActor(this);
        dispose();
    }

    @Override
    public void draw(Batch batch) {
        Point p = getWorldPoint();
        batch.draw(background, p.x - super.getWidth() / 2, p.y - super.getHeight() / 2, super.getWidth(), super.getHeight());
        GalacticTraitors.getTextView().drawStringInWorld(title, p.add(new Point(0, super.getHeight() / 2 - .1f)),
                TextView.Align.center, super.getWidth() * .9f, .3f, Color.BLACK);
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
        background.dispose();
        for (Button button : buttons) {
            button.dispose();
        }
    }

    public static class MenuBuilder {

        private final float width;
        private List<String> buttonText = new ArrayList<>();
        private List<Runnable> buttonActions = new ArrayList<>();

        public MenuBuilder(float width) {
            this.width = width;
        }

        public MenuBuilder addButton(String text, Runnable action) {
            buttonText.add(text);
            buttonActions.add(action);
            return this;
        }

        public Menu build(String title) {
            float height = .55f; //title
            height += (buttonText.size() + 1) * buttonSpacing; //spacing between buttons
            List<Button> buttons = new ArrayList<>();
            for (int i = 0; i < buttonText.size(); i++) {
                String text = buttonText.get(i);
                float h = calcButtonHeight(text);
                height += h;
                buttons.add(new Button(calcButtonWidth(text), calcButtonHeight(text), text, buttonActions.get(i)));
            }
            String closeButtonText = "Done";
            float closeButtonHeight = calcButtonHeight(closeButtonText);
            height += closeButtonHeight;
            final Menu menu = new Menu(width, height, title, buttons);
            Button b = new Button(calcButtonWidth(closeButtonText), closeButtonHeight, closeButtonText, new Runnable() {
                @Override
                public void run() {
                    menu.dismiss();
                }
            });
            menu.buttons.add(b);
            menu.init();
            return menu;
        }

        private float calcButtonWidth(String text) {
            return width * .9f;
        }

        private float calcButtonHeight(String text) {
            return .5f;
        }

    }

}
