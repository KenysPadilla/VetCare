package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevaMotiladaController implements Initializable {

    @FXML private ComboBox<String> cbPaciente;
    @FXML private ComboBox<String> cbEstilista;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbHora;
    @FXML private ComboBox<String> cbEstiloCorte;
    @FXML private ComboBox<String> cbLargo;
    @FXML private CheckBox chkUnas;
    @FXML private CheckBox chkLimpieza;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPaciente.getItems().addAll("Max - Perro", "Luna - Gato", "Rocky - Perro");
        cbEstilista.getItems().addAll("Ana García - Motilada", "Pedro Ruiz - Ambos");
        cbHora.getItems().addAll("08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00");
        cbEstiloCorte.getItems().addAll("Higiénico", "Estético", "Raza", "Personalizado");
        cbLargo.getItems().addAll("Corto", "Mediano", "Largo");
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbEstilista.getValue() == null
                || dpFecha.getValue() == null || cbHora.getValue() == null
                || cbEstiloCorte.getValue() == null || cbLargo.getValue() == null) {
            mostrarMensaje("Paciente, estilista, fecha, hora, estilo y largo son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Motilada programada exitosamente.", "#1B6B2F");
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
