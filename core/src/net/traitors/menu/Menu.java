package net.traitors.menu;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.traitors.GalacticTraitors;
import net.traitors.thing.AbstractThing;

public class Menu extends AbstractThing {

    private Menu(float width, float height) {
        super(width, height);
    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void dispose() {

    }

    public static class MenuBuilder {

        private Menu menu;

        public MenuBuilder(String title) {
        }

        public MenuBuilder addSubmenu(Menu menu) {
            return this;
        }

        public Menu build() {
            return menu;
        }

    }

}
