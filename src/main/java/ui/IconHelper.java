package ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public final class IconHelper {

    public static final Color NAV_MUTED  = Color.web("#6b7f8e");
    public static final Color NAV_ACTIVE = Color.WHITE;
    public static final Color INPUT_MUTED = Color.web("#6b7f8e");
    public static final Color TEAL = Color.web("#2b87a0");

    private static final double ICON_BOX = 20;

    private IconHelper() {}

    public static FontIcon icon(Ikon ikon, int size, Color color) {
        FontIcon fi = FontIcon.of(ikon, size);
        fi.setIconColor(color);
        fi.getStyleClass().add("app-icon");
        return fi;
    }

    
    public static void attachNavIcon(Button button, Ikon ikon) {
        FontIcon fi = icon(ikon, 16, NAV_MUTED);
        StackPane box = new StackPane(fi);
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(ICON_BOX);
        box.setMaxWidth(ICON_BOX);
        box.setPrefWidth(ICON_BOX);
        box.setMinHeight(ICON_BOX);
        box.setMaxHeight(ICON_BOX);
        box.setPrefHeight(ICON_BOX);
        button.setGraphic(box);
        button.setGraphicTextGap(8);
    }

    
    public static void setNavIconActive(Button button, boolean active) {
        Color color;
        if (active) {
            boolean isGroupItem = button.getParent() != null
                    && button.getParent().getStyleClass().contains("nav-group-items");
            color = isGroupItem ? TEAL : NAV_ACTIVE;
        } else {
            color = NAV_MUTED;
        }
        Node graphic = button.getGraphic();
        if (graphic instanceof FontIcon fi) {
            fi.setIconColor(color);
        } else if (graphic instanceof StackPane sp && !sp.getChildren().isEmpty()
                   && sp.getChildren().get(0) instanceof FontIcon fi) {
            fi.setIconColor(color);
        }
    }

}