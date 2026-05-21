package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevaInternacionController implements Initializable {

    @FXML private ComboBox<String> cbPaciente;
    @FXML private ComboBox<String> cbVeterinario;
    @FXML private ComboBox<String> cbConsulta;
    @FXML private DatePicker dpFechaIngreso;
    @FXML private TextField txtCostoDia;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPaciente.getItems().addAll("Max - Perro", "Luna - Gato", "Rocky - Perro");
        cbVeterinario.getItems().addAll("Dr. Pérez", "Dra. López", "Dr. Martínez");
        cbConsulta.getItems().addAll("Consulta #1 - Max", "Consulta #2 - Luna", "Consulta #3 - Rocky");
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVeterinario.getValue() == null
                || dpFechaIngreso.getValue() == null || txtCostoDia.getText().trim().isEmpty()) {
            mostrarMensaje("Paciente, veterinario, fecha y costo son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Internación registrada exitosamente.", "#1B6B2F");
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
