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

public class NuevaCitaController implements Initializable {

    @FXML private ComboBox<String> cbPaciente;
    @FXML private ComboBox<String> cbVeterinario;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbHora;
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextArea txtMotivo;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPaciente.getItems().addAll("Max - Perro (Juan Pérez)", "Luna - Gato (María López)", "Rocky - Perro (Carlos Ruiz)");

        cbVeterinario.getItems().addAll("Dr. Pérez - Animales de compañía", "Dra. López - Exóticos", "Dr. Martínez - Cirugía");

        cbHora.getItems().addAll("08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00");

        cbTipo.getItems().addAll("Consulta General", "Vacunación", "Cirugía", "Control");
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVeterinario.getValue() == null ||
                dpFecha.getValue() == null || cbHora.getValue() == null || cbTipo.getValue() == null) {
            mostrarMensaje("Complete todos los campos obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Cita guardada exitosamente.", "#1B6B2F");
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
