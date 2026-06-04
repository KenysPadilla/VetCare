package controller;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import service.RecuperarPasswordService;
import ui.IconHelper;
import ui.StyleManager;

import java.net.URL;
import java.util.ResourceBundle;

public class RecuperarPasswordController implements Initializable {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private Button    btnEnviar;
    @FXML private Button    btnVolver;
    @FXML private Label     lblMensaje;

    @FXML private TextField  txtD1, txtD2, txtD3, txtD4, txtD5, txtD6;
    @FXML private Button     btnVerificar;
    @FXML private Hyperlink  lnkReenviar;
    @FXML private Label      lblMensajeCodigo;

    @FXML private PasswordField txtNuevaPassword;
    @FXML private TextField     txtNuevaVisible;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private TextField     txtConfirmarVisible;
    @FXML private Button        btnToggleNueva;
    @FXML private Button        btnToggleConfirmar;
    @FXML private Button        btnCambiar;
    @FXML private Label         lblMensajePassword;

    private boolean nuevaVisible     = false;
    private boolean confirmarVisible = false;

    @FXML private VBox  panelSolicitud;
    @FXML private VBox  panelCodigo;
    @FXML private VBox  panelNuevaPassword;
    @FXML private Label lblDescripcionIzquierda;

    private String usuarioActual;

    private final RecuperarPasswordService servicio = new RecuperarPasswordService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        panelCodigo.setVisible(false);
        panelCodigo.setManaged(false);
        panelNuevaPassword.setVisible(false);
        panelNuevaPassword.setManaged(false);
        btnVolver.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StyleManager.apply(newScene);
            }
        });
        configurarCajasDigito();
        actualizarIconoOjo(btnToggleNueva, false);
        actualizarIconoOjo(btnToggleConfirmar, false);
    }

    private void configurarCajasDigito() {
        TextField[] cajas = {txtD1, txtD2, txtD3, txtD4, txtD5, txtD6};
        for (int i = 0; i < cajas.length; i++) {
            final int idx = i;
            TextField caja = cajas[i];

            caja.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1) {
                    caja.setText(newVal.substring(0, 1));
                    return;
                }
                if (!newVal.isEmpty() && idx < cajas.length - 1) {
                    cajas[idx + 1].requestFocus();
                }
            });

            caja.setOnKeyPressed(evt -> {
                if (evt.getCode() == KeyCode.BACK_SPACE && caja.getText().isEmpty() && idx > 0) {
                    cajas[idx - 1].requestFocus();
                }
            });
        }
    }

    @FXML
    private void handleEnviar() {
        String usuario = txtUsuario.getText().trim();
        String correo  = txtCorreo.getText().trim();

        if (usuario.isEmpty() || correo.isEmpty()) {
            mostrarMensaje(lblMensaje, "Complete todos los campos.", "#D32F2F");
            return;
        }

        btnEnviar.setDisable(true);
        mostrarMensaje(lblMensaje, "Enviando codigo...", "#333333");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                servicio.solicitarRecuperacion(usuario, correo);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            usuarioActual = usuario;
            mostrarPanel(panelSolicitud, panelCodigo);
            btnEnviar.setDisable(false);
        });
        task.setOnFailed(e -> {
            mostrarMensaje(lblMensaje, task.getException().getMessage(), "#D32F2F");
            btnEnviar.setDisable(false);
        });
        ejecutarEnHilo(task);
    }

    @FXML
    private void handleVerificar() {
        String codigo = txtD1.getText() + txtD2.getText() + txtD3.getText()
                      + txtD4.getText() + txtD5.getText() + txtD6.getText();
        if (codigo.length() < 6) {
            mostrarMensaje(lblMensajeCodigo,
                    "Ingresa los 6 dígitos del código.", "#D32F2F");
            return;
        }

        btnVerificar.setDisable(true);
        mostrarMensaje(lblMensajeCodigo, "Verificando...", "#333333");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                servicio.verificarCodigo(usuarioActual, codigo);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            mostrarPanel(panelCodigo, panelNuevaPassword);
            btnVerificar.setDisable(false);
        });
        task.setOnFailed(e -> {
            mostrarMensaje(lblMensajeCodigo,
                    task.getException().getMessage(), "#D32F2F");
            btnVerificar.setDisable(false);
        });
        ejecutarEnHilo(task);
    }

    @FXML
    private void handleReenviar() {
        String correo = txtCorreo.getText().trim();

        lnkReenviar.setDisable(true);
        mostrarMensaje(lblMensajeCodigo, "Reenviando codigo...", "#333333");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                servicio.solicitarRecuperacion(usuarioActual, correo);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            mostrarMensaje(lblMensajeCodigo, "Codigo reenviado.", "#1B6B2F");
            lnkReenviar.setDisable(false);
        });
        task.setOnFailed(e -> {
            mostrarMensaje(lblMensajeCodigo,
                    task.getException().getMessage(), "#D32F2F");
            lnkReenviar.setDisable(false);
        });
        ejecutarEnHilo(task);
    }

    @FXML
    private void handleToggleNueva() {
        nuevaVisible = !nuevaVisible;
        alternarCampo(txtNuevaPassword, txtNuevaVisible, nuevaVisible);
        actualizarIconoOjo(btnToggleNueva, nuevaVisible);
    }

    @FXML
    private void handleToggleConfirmar() {
        confirmarVisible = !confirmarVisible;
        alternarCampo(txtConfirmarPassword, txtConfirmarVisible, confirmarVisible);
        actualizarIconoOjo(btnToggleConfirmar, confirmarVisible);
    }

    private void alternarCampo(PasswordField oculto, TextField visible, boolean mostrar) {
        if (mostrar) {
            visible.setText(oculto.getText());
            oculto.setVisible(false);
            oculto.setManaged(false);
            visible.setVisible(true);
            visible.setManaged(true);
            visible.requestFocus();
            visible.positionCaret(visible.getText().length());
        } else {
            oculto.setText(visible.getText());
            visible.setVisible(false);
            visible.setManaged(false);
            oculto.setVisible(true);
            oculto.setManaged(true);
        }
    }

    private void actualizarIconoOjo(Button btn, boolean visible) {
        btn.setGraphic(IconHelper.icon(
                visible ? FontAwesomeSolid.EYE_SLASH : FontAwesomeSolid.EYE,
                16, IconHelper.INPUT_MUTED));
        btn.setText(null);
    }

    @FXML
    private void handleCambiar() {
        String nueva     = nuevaVisible     ? txtNuevaVisible.getText()     : txtNuevaPassword.getText();
        String confirmar = confirmarVisible  ? txtConfirmarVisible.getText() : txtConfirmarPassword.getText();

        if (nueva.isEmpty() || confirmar.isEmpty()) {
            mostrarMensaje(lblMensajePassword, "Complete ambos campos.", "#D32F2F");
            return;
        }
        if (!nueva.equals(confirmar)) {
            mostrarMensaje(lblMensajePassword,
                    "Las contrasenas no coinciden.", "#D32F2F");
            return;
        }

        btnCambiar.setDisable(true);
        mostrarMensaje(lblMensajePassword, "Guardando...", "#333333");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                servicio.cambiarPassword(usuarioActual, nueva);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            mostrarMensaje(lblMensajePassword,
                    "Contrasena actualizada. Redirigiendo...", "#1B6B2F");
            PauseTransition pausa = new PauseTransition(Duration.seconds(2));
            pausa.setOnFinished(ev -> navegarALogin());
            pausa.play();
        });
        task.setOnFailed(e -> {
            mostrarMensaje(lblMensajePassword,
                    task.getException().getMessage(), "#D32F2F");
            btnCambiar.setDisable(false);
        });
        ejecutarEnHilo(task);
    }

    @FXML
    private void handleVolver() {
        navegarALogin();
    }

    private void mostrarPanel(VBox actual, VBox siguiente) {
        actual.setVisible(false);
        actual.setManaged(false);
        siguiente.setVisible(true);
        siguiente.setManaged(true);
    }

    private void navegarALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 960, 600);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarMensaje(Label lbl, String texto, String color) {
        lbl.setText(texto);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
        lbl.setVisible(true);
    }

    private void ejecutarEnHilo(Task<Void> task) {
        Thread hilo = new Thread(task);
        hilo.setDaemon(true);
        hilo.start();
    }
}
