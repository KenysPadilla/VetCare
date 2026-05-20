package controlador;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevoExamenControlador implements Initializable {

    @FXML private ComboBox<String> cbConsulta;
    @FXML private ComboBox<String> cbTipoExamen;
    @FXML private DatePicker dpFechaSolicitud;
    @FXML private TextField txtCosto;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbConsulta.getItems().addAll("Consulta #1 - Max", "Consulta #2 - Luna", "Consulta #3 - Rocky");
        cbTipoExamen.getItems().addAll("Hemograma", "Uroanálisis", "Cultivo", "Coprológico", "Rayos X", "Ecografía");
    }

    @FXML
    private void handleGuardar() {
        if (cbConsulta.getValue() == null || cbTipoExamen.getValue() == null
                || dpFechaSolicitud.getValue() == null) {
            mostrarMensaje("Consulta, tipo de examen y fecha son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Examen registrado exitosamente.", "#1B6B2F");
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
