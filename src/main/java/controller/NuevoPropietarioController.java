package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Propietario;
import service.PropietarioService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NuevoPropietarioController implements Initializable {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private Button    btnGuardar;
    @FXML private Label     lblMensaje;

    /** No nulo cuando el formulario abre en modo edicion. */
    private Propietario propietarioEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Pre-rellena el formulario con los datos del propietario a editar,
     * deshabilita la cedula (PK) y cambia el texto del boton a "Actualizar".
     */
    public void setModoEdicion(Propietario p) {
        this.propietarioEnEdicion = p;
        txtCedula.setText(p.getCedula());
        txtCedula.setDisable(true);
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        txtTelefono.setText(p.getTelefono() != null ? p.getTelefono() : "");
        txtCorreo.setText(p.getEmail() != null ? p.getEmail() : "");
        txtDireccion.setText(p.getDireccion() != null ? p.getDireccion() : "");
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        if (txtCedula.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()
                || txtApellido.getText().trim().isEmpty()) {
            mostrarMensaje("Cédula, nombre y apellido son obligatorios.", "#D32F2F");
            return;
        }

        try {
            Propietario p = new Propietario(
                    txtCedula.getText().trim(),
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtDireccion.getText().trim()
            );

            if (propietarioEnEdicion == null) {
                new PropietarioService().guardar(p);
                mostrarMensaje("Propietario guardado exitosamente.", "#1B6B2F");
            } else {
                new PropietarioService().actualizar(p);
                mostrarMensaje("Propietario actualizado exitosamente.", "#1B6B2F");
            }
            ((Stage) lblMensaje.getScene().getWindow()).close();
        } catch (SQLException e) {
            mostrarMensaje("Error: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        ((Stage) lblMensaje.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}