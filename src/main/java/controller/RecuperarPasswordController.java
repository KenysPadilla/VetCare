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

/**
 * Controlador del flujo de 3 pasos para recuperacion de contrasena.
 *
 * <p>GRASP Controlador: este controlador actua como receptor de los eventos
 * de la vista y delega toda la logica de negocio a
 * {@link RecuperarPasswordService}. No valida reglas de dominio ni accede
 * directamente a la base de datos.</p>
 *
 * <p>Cada paso del flujo se muestra en su propio {@link VBox} ({@code panelSolicitud},
 * {@code panelCodigo}, {@code panelNuevaPassword}). El metodo {@link #mostrarPanel}
 * oculta el panel actual y hace visible el siguiente, usando las propiedades
 * {@code visible} y {@code managed} para que el layout se reajuste
 * correctamente.</p>
 *
 * <p>Las operaciones de red (envio de correo) y de base de datos se ejecutan
 * en un hilo secundario mediante {@link Task} para no bloquear el hilo
 * de JavaFX.</p>
 */
public class RecuperarPasswordController implements Initializable {

    // -------------------------------------------------------------------------
    // Paso 1 — Solicitud
    // -------------------------------------------------------------------------
    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private Button    btnEnviar;
    @FXML private Button    btnVolver;
    @FXML private Label     lblMensaje;

    // -------------------------------------------------------------------------
    // Paso 2 — Verificacion del codigo (6 cajas individuales)
    // -------------------------------------------------------------------------
    @FXML private TextField  txtD1, txtD2, txtD3, txtD4, txtD5, txtD6;
    @FXML private Button     btnVerificar;
    @FXML private Hyperlink  lnkReenviar;
    @FXML private Label      lblMensajeCodigo;

    // -------------------------------------------------------------------------
    // Paso 3 — Nueva contrasena
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Paneles y panel izquierdo
    // -------------------------------------------------------------------------
    @FXML private VBox  panelSolicitud;
    @FXML private VBox  panelCodigo;
    @FXML private VBox  panelNuevaPassword;
    @FXML private Label lblDescripcionIzquierda;

    // -------------------------------------------------------------------------
    // Estado interno
    // -------------------------------------------------------------------------
    /** Nombre de usuario confirmado en el Paso 1; se reutiliza en Pasos 2 y 3. */
    private String usuarioActual;

    /** Servicio que coordina los 3 pasos del flujo de recuperacion. */
    private final RecuperarPasswordService servicio = new RecuperarPasswordService();

    // -------------------------------------------------------------------------
    // Inicializacion
    // -------------------------------------------------------------------------

    /**
     * Oculta los paneles de los Pasos 2 y 3; solo {@code panelSolicitud}
     * es visible al abrir la ventana.
     */
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

    // -------------------------------------------------------------------------
    // Handlers FXML
    // -------------------------------------------------------------------------

    /**
     * Paso 1: valida los campos, llama a
     * {@link RecuperarPasswordService#solicitarRecuperacion} en un hilo
     * secundario y avanza al Paso 2 si tiene exito.
     */
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

    /**
     * Paso 2: verifica el codigo ingresado contra el guardado en BD y avanza
     * al Paso 3 si es correcto y no ha expirado.
     */
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

    /**
     * Paso 2 (reenvio): genera y envia un nuevo codigo al mismo correo,
     * sin cambiar de panel.
     */
    @FXML
    private void handleReenviar() {
        // Capturar el correo en el hilo FX antes de entregar el Task al hilo secundario
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

    /**
     * Paso 3: valida que las contrasenas coincidan, llama a
     * {@link RecuperarPasswordService#cambiarPassword} y redirige al login
     * tras 2 segundos de confirmacion visual.
     */
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

    /**
     * Navega de vuelta a la pantalla de login desde cualquier paso.
     */
    @FXML
    private void handleVolver() {
        navegarALogin();
    }

    // -------------------------------------------------------------------------
    // Metodos privados de utilidad
    // -------------------------------------------------------------------------

    /**
     * Oculta {@code actual} y muestra {@code siguiente}, actualizando las
     * propiedades {@code visible} y {@code managed} para que el VBox padre
     * recalcule el layout correctamente.
     *
     * @param actual    panel que se oculta
     * @param siguiente panel que se muestra
     */
    private void mostrarPanel(VBox actual, VBox siguiente) {
        actual.setVisible(false);
        actual.setManaged(false);
        siguiente.setVisible(true);
        siguiente.setManaged(true);
    }

    /**
     * Carga {@code /fxml/login.fxml} y reemplaza la escena actual.
     */
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

    /**
     * Actualiza texto y color de un {@link Label} de mensaje y lo hace visible.
     *
     * @param lbl    etiqueta de mensaje a actualizar
     * @param texto  mensaje a mostrar
     * @param color  color CSS (p. ej. {@code "#D32F2F"} para error,
     *               {@code "#1B6B2F"} para exito)
     */
    private void mostrarMensaje(Label lbl, String texto, String color) {
        lbl.setText(texto);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
        lbl.setVisible(true);
    }

    /**
     * Lanza el {@link Task} en un hilo daemon para no bloquear el cierre
     * de la aplicacion.
     *
     * @param task tarea a ejecutar en segundo plano
     */
    private void ejecutarEnHilo(Task<Void> task) {
        Thread hilo = new Thread(task);
        hilo.setDaemon(true);
        hilo.start();
    }
}