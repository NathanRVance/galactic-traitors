package net.traitors.ui.ScreenElements;

import java.util.ArrayList;
import java.util.List;

public class SelectableSwitch<T extends Selectable> {

    private List<T> selectables = new ArrayList<>();

    public SelectableSwitch() {

    }

    public void addSelectable(T selectable) {
        selectables.add(selectable);
    }

    public void removeSelectable(T selectable) {
        selectables.remove(selectable);
    }

    public List<T> getSelectables() {
        return selectables;
    }

    public void selectableTapped(T selectable, boolean selected) {
        for(T s : selectables) {
            s.unselect();
        }
        if(selected) {
            selectable.select();
        }
    }

    /**
     * Gets the selectable that the mouse is hovering over
     * @return the hovered selectable, or null
     */
    public T getHovered() {
        for(T selectable : selectables) {
            if(selectable.isHovered()) {
                return selectable;
            }
        }
        return null;
    }
}
