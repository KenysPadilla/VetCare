package controller;

import dao.impl.EstilistaDAO;
import dao.impl.VeterinarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Estilista;
import model.Usuario;
import model.Veterinario;
import service.UsuarioService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NuevoUsuarioController implements Initializable {

    @FXML private TextField        txtUsername;
    @FXML private ComboBox<String> cbRol;
    @FXML private VBox             pnlEmpleado;
    @FXML private Label            lblEmpleado;
    @FXML private ComboBox<String> cbEmpleado;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCedula;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;

    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmar;

    @FXML private Button btnGuardar;
    @FXML private Label  lblMensaje;

    private Usuario usuarioEnEdicion = null;

    private final List<Veterinario> veterinariosList = new ArrayList<>();
    private final List<Estilista>   estilistasList   = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbRol.getItems().addAll("ADMIN", "VETERINARIO", "ESTILISTA", "RECEPCIONISTA");
        cbRol.setOnAction(e -> actualizarPorRol());
    }

    

    public void setModoEdicion(Usuario u) {
        this.usuarioEnEdicion = u;

        txtUsername.setText(u.getNombreUsuario());
        txtUsername.setDisable(true);

        txtNombre.setText(nvl(u.getNombre(), ""));
        txtApellido.setText(nvl(u.getApellido(), ""));
        txtCedula.setText(nvl(u.getCedula(), ""));
        txtTelefono.setText(nvl(u.getTelefono(), ""));
        txtEmail.setText(nvl(u.getEmail(), ""));

        cbRol.setValue(u.getRol());

        if (u.getCedulaEmpleado() != null) {
            seleccionarEmpleadoPorCedula(u.getCedulaEmpleado());
        }

        btnGuardar.setText("Actualizar");
    }

    

    private void actualizarPorRol() {
        String rol = cbRol.getValue();
        if ("VETERINARIO".equals(rol)) {
            lblEmpleado.setText("Veterinario vinculado");
            cargarEmpleados(true);
        } else if ("ESTILISTA".equals(rol)) {
            lblEmpleado.setText("Estilista vinculado");
            cargarEmpleados(false);
        } else {
            pnlEmpleado.setVisible(false);
            pnlEmpleado.setManaged(false);
            cbEmpleado.getSelectionModel().clearSelection();
            habilitarDatosPersonales(true);
        }
    }

    private void cargarEmpleados(boolean esVeterinario) {
        cbEmpleado.getItems().clear();
        veterinariosList.clear();
        estilistasList.clear();

        try {
            if (esVeterinario) {
                for (Veterinario v : new VeterinarioDAO().listarActivos()) {
                    cbEmpleado.getItems().add(v.getNombre() + " " + v.getApellido()
                            + " — CC: " + v.getCedula());
                    veterinariosList.add(v);
                }
            } else {
                for (Estilista e : new EstilistaDAO().listarActivos()) {
                    cbEmpleado.getItems().add(e.getNombre() + " " + e.getApellido()
                            + " — CC: " + e.getCedula());
                    estilistasList.add(e);
                }
            }
            pnlEmpleado.setVisible(true);
            pnlEmpleado.setManaged(true);
            habilitarDatosPersonales(true); 
            limpiarDatosPersonales();
        } catch (SQLException ex) {
            mostrarMensaje("Error al cargar empleados: " + ex.getMessage(), "#D32F2F");
        }

        cbEmpleado.setOnAction(ev -> autoRellenarDesdeEmpleado());
    }

    private void autoRellenarDesdeEmpleado() {
        int idx = cbEmpleado.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            limpiarDatosPersonales();
            habilitarDatosPersonales(true);
            return;
        }

        String nombre, apellido, cedula, telefono, email;

        if (!veterinariosList.isEmpty()) {
            Veterinario v = veterinariosList.get(idx);
            nombre = v.getNombre();   apellido = v.getApellido();
            cedula = v.getCedula();   telefono = v.getTelefono();
            email  = v.getEmail();
        } else {
            Estilista e = estilistasList.get(idx);
            nombre = e.getNombre();   apellido = e.getApellido();
            cedula = e.getCedula();   telefono = e.getTelefono();
            email  = e.getEmail();
        }

        txtNombre.setText(nvl(nombre, ""));
        txtApellido.setText(nvl(apellido, ""));
        txtCedula.setText(nvl(cedula, ""));
        txtTelefono.setText(nvl(telefono, ""));
        txtEmail.setText(nvl(email, ""));

        habilitarDatosPersonales(false);
    }

    private void seleccionarEmpleadoPorCedula(String cedula) {
        List<?> lista = !veterinariosList.isEmpty() ? veterinariosList : estilistasList;
        for (int i = 0; i < lista.size(); i++) {
            String c = !veterinariosList.isEmpty()
                    ? veterinariosList.get(i).getCedula()
                    : estilistasList.get(i).getCedula();
            if (cedula.equals(c)) {
                cbEmpleado.getSelectionModel().select(i); // dispara autoRellenarDesdeEmpleado()
                return;
            }
        }
    }

    private void habilitarDatosPersonales(boolean editable) {
        txtNombre.setDisable(!editable);
        txtApellido.setDisable(!editable);
        txtCedula.setDisable(!editable);
        txtTelefono.setDisable(!editable);
        txtEmail.setDisable(!editable);
    }

    private void limpiarDatosPersonales() {
        txtNombre.clear();
        txtApellido.clear();
        txtCedula.clear();
        txtTelefono.clear();
        txtEmail.clear();
    }

    

    @FXML
    private void handleGuardar() {
        if (usuarioEnEdicion == null) guardarNuevo();
        else actualizarExistente();
    }

    private void guardarNuevo() {
        if (txtUsername.getText().trim().isEmpty()
                || txtPassword.getText().trim().isEmpty()
                || txtConfirmar.getText().trim().isEmpty()
                || cbRol.getValue() == null) {
            mostrarMensaje("Usuario, contraseña y rol son obligatorios.", "#D32F2F");
            return;
        }
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()
                || txtCedula.getText().trim().isEmpty()) {
            mostrarMensaje("Nombre, apellido y cédula son obligatorios.", "#D32F2F");
            return;
        }
        if (!txtPassword.getText().equals(txtConfirmar.getText())) {
            mostrarMensaje("Las contraseñas no coinciden.", "#D32F2F");
            return;
        }

        String rol            = cbRol.getValue();
        String cedulaEmpleado = resolverCedulaEmpleado(rol);
        if (cedulaEmpleado == null && requiereEmpleado(rol)) {
            mostrarMensaje("Debe seleccionar el empleado vinculado.", "#D32F2F");
            return;
        }

        try {
            Usuario u = new Usuario();
            u.setNombreUsuario(txtUsername.getText().trim());
            u.setContrasena(txtPassword.getText());
            u.setRol(rol);
            u.setActivo(true);
            u.setNombre(txtNombre.getText().trim());
            u.setApellido(txtApellido.getText().trim());
            u.setCedula(txtCedula.getText().trim());
            u.setTelefono(txtTelefono.getText().trim());
            u.setEmail(txtEmail.getText().trim());
            u.setCedulaEmpleado(cedulaEmpleado);
            new UsuarioService().guardar(u);
            mostrarMensaje("Usuario creado exitosamente.", "#1B6B2F");
            ((Stage) lblMensaje.getScene().getWindow()).close();
        } catch (SQLException e) {
            mostrarMensaje("Error: " + e.getMessage(), "#D32F2F");
        }
    }

    private void actualizarExistente() {
        if (cbRol.getValue() == null) {
            mostrarMensaje("El rol es obligatorio.", "#D32F2F");
            return;
        }
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()
                || txtCedula.getText().trim().isEmpty()) {
            mostrarMensaje("Nombre, apellido y cédula son obligatorios.", "#D32F2F");
            return;
        }

        String rol            = cbRol.getValue();
        String cedulaEmpleado = resolverCedulaEmpleado(rol);
        if (cedulaEmpleado == null && requiereEmpleado(rol)) {
            mostrarMensaje("Debe seleccionar el empleado vinculado.", "#D32F2F");
            return;
        }

        String passAUsar;
        String nuevaPass = txtPassword.getText();
        if (!nuevaPass.isEmpty()) {
            if (!nuevaPass.equals(txtConfirmar.getText())) {
                mostrarMensaje("Las contraseñas no coinciden.", "#D32F2F");
                return;
            }
            passAUsar = nuevaPass;
        } else {
            passAUsar = usuarioEnEdicion.getContrasena();
        }

        try {
            Usuario u = new Usuario();
            u.setNombreUsuario(usuarioEnEdicion.getNombreUsuario());
            u.setNombre(txtNombre.getText().trim());
            u.setApellido(txtApellido.getText().trim());
            u.setCedula(txtCedula.getText().trim());
            u.setTelefono(txtTelefono.getText().trim());
            u.setEmail(txtEmail.getText().trim());
            u.setContrasena(passAUsar);
            u.setRol(rol);
            u.setActivo(usuarioEnEdicion.isActivo());
            u.setCedulaEmpleado(cedulaEmpleado);
            new UsuarioService().actualizar(u);
            mostrarMensaje("Usuario actualizado exitosamente.", "#1B6B2F");
            ((Stage) lblMensaje.getScene().getWindow()).close();
        } catch (SQLException e) {
            mostrarMensaje("Error: " + e.getMessage(), "#D32F2F");
        }
    }

    

    private boolean requiereEmpleado(String rol) {
        return "VETERINARIO".equals(rol) || "ESTILISTA".equals(rol);
    }

    private String resolverCedulaEmpleado(String rol) {
        if (!requiereEmpleado(rol)) return null;
        int idx = cbEmpleado.getSelectionModel().getSelectedIndex();
        if (idx < 0) return null;
        if (!veterinariosList.isEmpty()) return veterinariosList.get(idx).getCedula();
        if (!estilistasList.isEmpty())   return estilistasList.get(idx).getCedula();
        return null;
    }

    @FXML
    private void handleCancelar() {
        ((Stage) lblMensaje.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }

    private static String nvl(String valor, String defecto) {
        return (valor != null && !valor.isBlank()) ? valor : defecto;
    }
}