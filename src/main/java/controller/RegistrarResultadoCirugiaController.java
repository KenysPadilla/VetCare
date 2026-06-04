package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Cirugia;
import service.CirugiaService;

import java.sql.SQLException;

public class RegistrarResultadoCirugiaController {

    @FXML private Label lblHeaderCirugia;
    @FXML private ToggleButton btnExitosa;
    @FXML private ToggleButton btnComplicacion;
    @FXML private TextArea taObservaciones;

    private final ToggleGroup resultadoGroup = new ToggleGroup();
    private Cirugia cirugia;
    private Runnable onGuardado;

    private static final String ESTILO_VERDE =
            "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-background-radius: 12; -fx-background-insets: 0; " +
            "-fx-cursor: hand; -fx-border-color: transparent; -fx-border-width: 0; " +
            "-fx-effect: dropshadow(gaussian, rgba(39,174,96,0.40), 10, 0, 0, 4);";
    private static final String ESTILO_NARANJA =
            "-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-background-radius: 12; -fx-background-insets: 0; " +
            "-fx-cursor: hand; -fx-border-color: transparent; -fx-border-width: 0; " +
            "-fx-effect: dropshadow(gaussian, rgba(230,126,34,0.40), 10, 0, 0, 4);";
    private static final String ESTILO_NORMAL =
            "-fx-background-color: #f5f7fa; -fx-text-fill: #8a9fad; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-background-radius: 12; -fx-background-insets: 0; " +
            "-fx-cursor: hand; -fx-border-color: #dde3e8; -fx-border-width: 1.5; " +
            "-fx-border-radius: 12; -fx-effect: null;";

    @FXML
    private void initialize() {
        btnExitosa.setToggleGroup(resultadoGroup);
        btnComplicacion.setToggleGroup(resultadoGroup);

        btnExitosa.setText("✓   Exitosa");
        btnComplicacion.setText("△   Con complicación");

        resultadoGroup.selectedToggleProperty().addListener((obs, old, now) -> actualizarEstilos());

        btnExitosa.setSelected(true);
        actualizarEstilos();
    }

    private void actualizarEstilos() {
        if (btnExitosa.isSelected()) {
            btnExitosa.setStyle(ESTILO_VERDE);
            btnComplicacion.setStyle(ESTILO_NORMAL);
        } else if (btnComplicacion.isSelected()) {
            btnExitosa.setStyle(ESTILO_NORMAL);
            btnComplicacion.setStyle(ESTILO_NARANJA);
        } else {
            btnExitosa.setStyle(ESTILO_NORMAL);
            btnComplicacion.setStyle(ESTILO_NORMAL);
        }
    }

    public void setCirugia(Cirugia c) {
        this.cirugia = c;
        lblHeaderCirugia.setText(
                String.format("Q-%03d", c.getId()) + " — " + c.getTipoCirugia());
    }

    public void setOnGuardado(Runnable callback) {
        this.onGuardado = callback;
    }

    @FXML
    private void handleGuardar() {
        if (resultadoGroup.getSelectedToggle() == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setHeaderText(null);
            alerta.setContentText("Seleccione un resultado antes de guardar.");
            alerta.showAndWait();
            return;
        }

        String base = btnExitosa.isSelected() ? "Exitosa" : "Con complicación";
        String obs = taObservaciones.getText() != null ? taObservaciones.getText().trim() : "";
        String resultado = obs.isEmpty() ? base : base + " — " + obs;

        try {
            new CirugiaService().actualizarResultado(cirugia.getId(), resultado);
            if (onGuardado != null) onGuardado.run();
            cerrar();
        } catch (SQLException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText(null);
            alerta.setContentText("Error al guardar resultado: " + e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) btnExitosa.getScene().getWindow()).close();
    }
}
