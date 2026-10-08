package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Optional;

public class PagoDialog {
    private static final String COLOR        = "#27ae60";
    private static final String COLOR_CLARO  = "#eafaf1";
    private static final String COLOR_TEXTO  = "#1a6e3d";
    private static final String BORDE_UNSEL  = "#dce6ec";
    private static final String TEXTO_UNSEL  = "#6b7f8e";

    private static final String[] METODOS = {"EFECTIVO", "TARJETA", "TRANSFERENCIA"};
    private static final String[] LABELS  = {"Efectivo", "Tarjeta", "Transferencia"};
    private static final String[] ICONOS  = {
        "fas-money-bill", "fas-credit-card", "fas-exchange-alt"
    };

    public static Optional<String> mostrar(String numFactura, String total) {
        FontIcon iconHeader = new FontIcon("fas-receipt");
        iconHeader.setStyle("-fx-icon-color: white; -fx-icon-size: 16px;");
        Label lblTitulo = new Label("Registrar Pago");
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox header = new HBox(10, iconHeader, lblTitulo);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + COLOR + "; -fx-padding: 12 22 12 22;");

        Label lblNum = new Label(numFactura);
        lblNum.setStyle("-fx-font-size: 12px; -fx-text-fill: #8a9fad; -fx-font-weight: bold;");
        Label lblTotal = new Label(total);
        lblTotal.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
        VBox infoBox = new VBox(4, lblNum, lblTotal);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(16, 22, 16, 22));
        infoBox.setStyle(
            "-fx-background-color: #f7fafc; "
            + "-fx-border-color: #dce6ec; "
            + "-fx-border-width: 0 0 1 0;");

        Label lblSub = new Label("MÉTODO DE PAGO");
        lblSub.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #8a9fad;");

        ToggleGroup group = new ToggleGroup();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getColumnConstraints().addAll(col1, col2);

        for (int i = 0; i < METODOS.length; i++) {
            ToggleButton btn = crearBtn(ICONOS[i], LABELS[i], METODOS[i], group);
            btn.setMaxWidth(Double.MAX_VALUE);
            grid.add(btn, i % 2, i / 2);
        }

        ((ToggleButton) group.getToggles().get(0)).setSelected(true);
        group.getToggles().forEach(t -> estiloBtn((ToggleButton) t, t.isSelected()));
        group.selectedToggleProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo == null) { group.selectToggle(anterior); return; }
            group.getToggles().forEach(t -> estiloBtn((ToggleButton) t, t.isSelected()));
        });

        VBox body = new VBox(12, lblSub, grid);
        body.setPadding(new Insets(18, 22, 20, 22));
        body.setStyle("-fx-background-color: white;");

        VBox content = new VBox(header, infoBox, body);
        content.setStyle("-fx-background-color: white;");

        ButtonType btnConfirmar = new ButtonType("Confirmar Pago", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Registrar Pago");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 0;");
        dialog.getDialogPane().setPrefWidth(400);

        Node btnOk = dialog.getDialogPane().lookupButton(btnConfirmar);
        btnOk.setStyle(
            "-fx-background-color: " + COLOR + "; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-font-size: 13px; "
            + "-fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((Region) btnOk).setPrefHeight(28);
        ((Region) btnOk).setMinHeight(28);
        ((Region) btnOk).setMaxHeight(28);

        Node btnCan = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCan.setStyle(
            "-fx-background-color: white; -fx-text-fill: #6b7f8e; "
            + "-fx-border-color: rgba(0,0,0,0.15); -fx-border-radius: 8; "
            + "-fx-border-width: 1; -fx-font-size: 13px; "
            + "-fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((Region) btnCan).setPrefHeight(28);
        ((Region) btnCan).setMinHeight(28);
        ((Region) btnCan).setMaxHeight(28);

        dialog.setResultConverter(bt -> {
            if (bt != btnConfirmar) return null;
            Toggle sel = group.getSelectedToggle();
            return sel != null ? (String) sel.getUserData() : "EFECTIVO";
        });

        return dialog.showAndWait();
    }

    private static ToggleButton crearBtn(String iconoLit, String label,
                                         String metodo, ToggleGroup group) {
        FontIcon icon = new FontIcon(iconoLit);
        icon.setStyle("-fx-icon-size: 20px;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        VBox box = new VBox(6, icon, lbl);
        box.setAlignment(Pos.CENTER);

        ToggleButton btn = new ToggleButton();
        btn.setGraphic(box);
        btn.setToggleGroup(group);
        btn.setUserData(metodo);
        btn.setPrefHeight(64);
        return btn;
    }

    private static void estiloBtn(ToggleButton btn, boolean sel) {
        String fondo   = sel ? COLOR_CLARO : "white";
        String borde   = sel ? COLOR       : BORDE_UNSEL;
        String grosor  = sel ? "2"         : "1.5";
        String txtColor= sel ? COLOR_TEXTO : TEXTO_UNSEL;
        String iconColor = sel ? COLOR : TEXTO_UNSEL;

        btn.setStyle(
            "-fx-background-color: " + fondo + "; "
            + "-fx-border-color: " + borde + "; "
            + "-fx-border-radius: 10; -fx-background-radius: 10; "
            + "-fx-border-width: " + grosor + "; -fx-cursor: hand;");

        if (btn.getGraphic() instanceof VBox vbox) {
            if (vbox.getChildren().get(0) instanceof FontIcon icon)
                icon.setStyle("-fx-icon-size: 20px; -fx-icon-color: " + iconColor + ";");
            if (vbox.getChildren().get(1) instanceof Label lbl)
                lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + txtColor + ";");
        }
    }
}
