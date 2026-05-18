package controlador;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevaConsultaControlador implements Initializable {

    @FXML private ComboBox<String> cbCita;
    @FXML private TextArea txtSintomas;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtTratamiento;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbCita.getItems().addAll("Cita #1 - Max (Juan Pérez) - Consulta General",
                "Cita #2 - Luna (María López) - Vacunación",
                "Cita #3 - Rocky (Carlos Ruiz) - Control");
    }

    @FXML
    private void handleGuardar() {
        if (cbCita.getValue() == null || txtSintomas.getText().trim().isEmpty()
                || txtDiagnostico.getText().trim().isEmpty()) {
            mostrarMensaje("Cita, síntomas y diagnóstico son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Consulta guardada exitosamente.", "#1B6B2F");
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
