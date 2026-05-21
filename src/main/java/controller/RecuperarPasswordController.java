package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RecuperarPasswordController implements Initializable {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtTelefono;
    @FXML private Button btnEnviar;
    @FXML private Button btnVolver;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblMensaje.setVisible(false);
    }

    @FXML
    private void handleEnviar() {
        String usuario = txtUsuario.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (usuario.isEmpty() || telefono.isEmpty()) {
            mostrarMensaje("Complete todos los campos.", "#D32F2F");
            return;
        }

        mostrarMensaje("Contraseña enviada al número " + telefono, "#1B6B2F");
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
        lblMensaje.setVisible(true);
    }
}
