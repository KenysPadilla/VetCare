package controlador;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevoUsuarioControlador implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmar;
    @FXML private ComboBox<String> cbRol;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbRol.getItems().addAll("ADMIN", "VETERINARIO", "RECEPCIONISTA");
    }

    @FXML
    private void handleGuardar() {
        if (txtUsername.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty()
                || txtConfirmar.getText().trim().isEmpty() || cbRol.getValue() == null) {
            mostrarMensaje("Todos los campos son obligatorios.", "#D32F2F");
            return;
        }

        if (!txtPassword.getText().equals(txtConfirmar.getText())) {
            mostrarMensaje("Las contraseñas no coinciden.", "#D32F2F");
            return;
        }

        mostrarMensaje("Usuario creado exitosamente.", "#1B6B2F");
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
