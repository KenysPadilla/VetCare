package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRecuperar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblError.setVisible(false);
        lblError.setManaged(false);
        txtPassword.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        ocultarError();

        String username = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, complete todos los campos.");
            return;
        }

        if (username.equals("admin") && password.equals("1234")) {
            abrirVentanaPrincipal();
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void handleRecuperar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recuperarPassword.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) linkRecuperar.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            mostrarError("Error al cargar la ventana.");
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setText("");
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    private void abrirVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/principal.fxml"));
            Parent root = loader.load();

            Stage stagePrincipal = new Stage();
            stagePrincipal.setTitle("VetCare System");
            stagePrincipal.setScene(new Scene(root));
            stagePrincipal.setMaximized(true);
            stagePrincipal.show();

            Stage stageLogin = (Stage) btnLogin.getScene().getWindow();
            stageLogin.close();

        } catch (Exception e) {
            mostrarError("Error al cargar la ventana principal.");
            e.printStackTrace();
        }
    }
}
