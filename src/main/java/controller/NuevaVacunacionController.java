package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevaVacunacionController implements Initializable {

    @FXML private ComboBox<String> cbPaciente;
    @FXML private ComboBox<String> cbVacuna;
    @FXML private ComboBox<String> cbVeterinario;
    @FXML private DatePicker dpFechaAplicacion;
    @FXML private DatePicker dpFechaProxima;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPaciente.getItems().addAll("Max - Perro", "Luna - Gato", "Rocky - Perro");
        cbVacuna.getItems().addAll("Rabia", "Parvovirus", "Moquillo", "Triple Felina", "Leptospirosis");
        cbVeterinario.getItems().addAll("Dr. Pérez", "Dra. López", "Dr. Martínez");
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVacuna.getValue() == null
                || cbVeterinario.getValue() == null || dpFechaAplicacion.getValue() == null) {
            mostrarMensaje("Paciente, vacuna, veterinario y fecha son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Vacunación registrada exitosamente.", "#1B6B2F");
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) lblMensaje.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}
