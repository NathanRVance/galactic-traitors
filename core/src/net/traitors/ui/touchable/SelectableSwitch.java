package net.traitors.ui.touchable;

import java.util.ArrayList;
import java.util.List;

public class SelectableSwitch <T extends Selectable> implements Touchable {

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

    @Override
    public boolean isTouched() {
        for (T selectable : selectables) {
            if (selectable.isTouched()) return true;
        }
        return false;
    }
}
