package ui;

import javafx.scene.control.Button;

public final class NavHelper {

    private static final String NAV_ITEM = "nav-item";
    private static final String NAV_ACTIVE = "nav-item-active";

    private NavHelper() {
    }

    public static void setActive(Button active, Button... others) {
        if (active != null) {
            active.getStyleClass().remove(NAV_ITEM);
            if (!active.getStyleClass().contains(NAV_ACTIVE)) {
                active.getStyleClass().add(NAV_ACTIVE);
            }
            IconHelper.setNavIconActive(active, true);
        }
        if (others == null) {
            return;
        }
        for (Button btn : others) {
            if (btn == null) {
                continue;
            }
            btn.getStyleClass().remove(NAV_ACTIVE);
            if (!btn.getStyleClass().contains(NAV_ITEM)) {
                btn.getStyleClass().add(NAV_ITEM);
            }
            IconHelper.setNavIconActive(btn, false);
        }
    }

}