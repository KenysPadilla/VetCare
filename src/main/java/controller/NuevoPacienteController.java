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

public class NuevoPacienteController implements Initializable {

    @FXML private ComboBox<String> cbPropietario;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbEspecie;
    @FXML private TextField txtRaza;
    @FXML private ComboBox<String> cbSexo;
    @FXML private TextField txtPeso;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtMicrochip;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPropietario.getItems().addAll("Juan Pérez", "María López", "Carlos Ruiz");
        cbEspecie.getItems().addAll("Perro", "Gato", "Ave", "Reptil", "Otro");
        cbSexo.getItems().addAll("Macho", "Hembra");
    }

    @FXML
    private void handleGuardar() {
        if (cbPropietario.getValue() == null || txtNombre.getText().trim().isEmpty()
                || cbEspecie.getValue() == null || cbSexo.getValue() == null) {
            mostrarMensaje("Propietario, nombre, especie y sexo son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Paciente guardado exitosamente.", "#1B6B2F");
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