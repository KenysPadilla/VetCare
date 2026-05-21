package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevaCirugiaController implements Initializable {

    @FXML private ComboBox<String> cbPaciente;
    @FXML private ComboBox<String> cbVeterinario;
    @FXML private TextField txtTipo;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbAnestesia;
    @FXML private TextField txtCosto;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPaciente.getItems().addAll("Max - Perro", "Luna - Gato", "Rocky - Perro");
        cbVeterinario.getItems().addAll("Dr. Pérez", "Dra. López", "Dr. Martínez");
        cbAnestesia.getItems().addAll("General", "Local", "Sedación");
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVeterinario.getValue() == null
                || txtTipo.getText().trim().isEmpty() || dpFecha.getValue() == null
                || cbAnestesia.getValue() == null) {
            mostrarMensaje("Paciente, veterinario, tipo, fecha y anestesia son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Cirugía registrada exitosamente.", "#1B6B2F");
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