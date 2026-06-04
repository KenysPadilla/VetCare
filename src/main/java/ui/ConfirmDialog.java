package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Diálogos de confirmación estilizados alineados al diseño de VetCare.
 *
 * <p>Reemplaza los {@code Alert.AlertType.CONFIRMATION} nativos del sistema
 * por ventanas con cabecera de color, icono y botones consistentes con el
 * resto de la interfaz.</p>
 */
public class ConfirmDialog {

    /**
     * Muestra un diálogo de confirmación con diseño personalizado.
     *
     * @param titulo        Título visible en la cabecera (ej: "Confirmar Pago")
     * @param icono         Emoji representativo (ej: "💳", "⚠️", "✅")
     * @param mensaje       Texto descriptivo de la acción a confirmar
     * @param textoPrimario Etiqueta del botón de confirmación (ej: "Pagar", "Desactivar")
     * @param colorPrimario Color HEX de la cabecera y botón primario
     * @return {@code true} si el usuario confirmó; {@code false} si canceló
     */
    public static boolean mostrar(String titulo, String icono, String mensaje,
                                   String textoPrimario, String colorPrimario) {

        // ── Cabecera coloreada ─────────────────────────────────────────────────
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 20px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox header = new HBox(10, lblIcono, lblTitulo);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: " + colorPrimario + "; "
                + "-fx-padding: 9 22 9 22;");

        // ── Cuerpo del mensaje ─────────────────────────────────────────────────
        Label lblMsg = new Label(mensaje);
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(340);
        lblMsg.setStyle(
                "-fx-font-size: 13px; -fx-text-fill: #3d5a6a; -fx-line-spacing: 3;");

        VBox body = new VBox(lblMsg);
        body.setPadding(new Insets(18, 24, 14, 24));
        body.setStyle("-fx-background-color: white;");

        // ── Contenedor principal ───────────────────────────────────────────────
        VBox content = new VBox(header, body);
        content.setStyle("-fx-background-color: white;");

        // ── Diálogo ────────────────────────────────────────────────────────────
        ButtonType btnConfirmar = new ButtonType(textoPrimario, ButtonBar.ButtonData.OK_DONE);
        Dialog<Boolean> dialog  = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: white; -fx-padding: 0;");
        dialog.getDialogPane().setPrefWidth(400);

        // ── Botón de confirmación (coloreado) ─────────────────────────────────
        Node btnOk = dialog.getDialogPane().lookupButton(btnConfirmar);
        btnOk.setStyle(
                "-fx-background-color: " + colorPrimario + "; "
                + "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; "
                + "-fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((Region) btnOk).setPrefHeight(28);
        ((Region) btnOk).setMinHeight(28);
        ((Region) btnOk).setMaxHeight(28);

        // ── Botón cancelar (neutro) ────────────────────────────────────────────
        Node btnCan = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCan.setStyle(
                "-fx-background-color: white; -fx-text-fill: #6b7f8e; "
                + "-fx-border-color: rgba(0,0,0,0.15); -fx-border-radius: 8; "
                + "-fx-border-width: 1; -fx-font-size: 13px; "
                + "-fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((Region) btnCan).setPrefHeight(28);
        ((Region) btnCan).setMinHeight(28);
        ((Region) btnCan).setMaxHeight(28);

        dialog.setResultConverter(bt -> bt == btnConfirmar);

        Optional<Boolean> result = dialog.showAndWait();
        return result.isPresent() && result.get();
    }
}