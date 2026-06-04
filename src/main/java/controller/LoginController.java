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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import ui.IconHelper;
import ui.StyleManager;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Button btnTogglePassword;
    @FXML private Hyperlink linkRecuperar;
    @FXML private HBox boxUsuario;
    @FXML private HBox boxPassword;

    private boolean passwordVisible;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblError.setVisible(false);
        lblError.setManaged(false);
        txtPassword.setOnAction(event -> handleLogin());
        txtUsuario.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StyleManager.apply(newScene);
            }
        });
        bindFocusStyle(txtUsuario, boxUsuario);
        bindFocusStyle(txtPassword, boxPassword);
        updateTogglePasswordIcon();
    }

    private static void bindFocusStyle(javafx.scene.control.Control field, HBox container) {
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                container.getStyleClass().add("input-with-icon-focused");
            } else {
                container.getStyleClass().remove("input-with-icon-focused");
            }
        });
    }

    private void updateTogglePasswordIcon() {
        btnTogglePassword.setGraphic(IconHelper.icon(
                passwordVisible ? FontAwesomeSolid.EYE_SLASH : FontAwesomeSolid.EYE,
                16,
                IconHelper.INPUT_MUTED));
        btnTogglePassword.setText(null);
    }

    @FXML
    private void handleTogglePassword() {
        passwordVisible = !passwordVisible;
        updateTogglePasswordIcon();
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

        try {
            model.Usuario u = new service.UsuarioService().autenticar(username, password);
            if (u == null) {
                mostrarError("Usuario o contraseña incorrectos.");
                return;
            }
            util.Sesion.setUsuario(u);
            abrirVentanaPrincipal();
        } catch (java.sql.SQLException e) {
            mostrarError("Error de conexión: " + e.getMessage());
        }
    }

    @FXML
    private void handleRecuperar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recuperarPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) linkRecuperar.getScene().getWindow();
            Scene scene = new Scene(root, 960, 600);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.setResizable(false);
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
            Scene scene = new Scene(root, 1280, 800);
            StyleManager.apply(scene);
            Stage stagePrincipal = new Stage();
            stagePrincipal.setTitle("VetCare");
            stagePrincipal.setScene(scene);
            stagePrincipal.setMinWidth(1100);
            stagePrincipal.setMinHeight(700);
            stagePrincipal.setMaximized(true);
            stagePrincipal.show();
            Stage stageLogin = (Stage) btnLogin.getScene().getWindow();
            stageLogin.close();
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            String detalle = cause.getMessage() != null ? cause.getMessage() : e.getClass().getSimpleName();
            mostrarError("Error al cargar la ventana principal: " + detalle);
            e.printStackTrace();
        }
    }
}
