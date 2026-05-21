package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevoEstilistaController implements Initializable {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private ComboBox<String> cbEspecialidad;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEspecialidad.getItems().addAll("Baño", "Motilada", "Ambos");
    }

    @FXML
    private void handleGuardar() {
        if (txtCedula.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()
                || txtApellido.getText().trim().isEmpty() || cbEspecialidad.getValue() == null) {
            mostrarMensaje("Cédula, nombre, apellido y especialidad son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Estilista guardado exitosamente.", "#1B6B2F");
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
