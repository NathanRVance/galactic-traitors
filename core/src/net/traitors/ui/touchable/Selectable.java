package net.traitors.ui.touchable;

public interface Selectable extends Touchable {

    void select();

    void unselect();

    boolean isSelected();

}
