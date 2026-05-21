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

public class NuevoMedicamentoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrincipio;
    @FXML private ComboBox<String> cbPresentacion;
    @FXML private TextField txtDosis;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecio;
    @FXML private DatePicker dpVencimiento;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPresentacion.getItems().addAll("Tableta", "Jarabe", "Inyectable", "Crema", "Gotas", "Polvo");
    }

    @FXML
    private void handleGuardar() {
        if (txtNombre.getText().trim().isEmpty() || txtPrincipio.getText().trim().isEmpty()
                || cbPresentacion.getValue() == null || txtStock.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()) {
            mostrarMensaje("Nombre, principio activo, presentación, stock y precio son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Medicamento guardado exitosamente.", "#1B6B2F");
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
