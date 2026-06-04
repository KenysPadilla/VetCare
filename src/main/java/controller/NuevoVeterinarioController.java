package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Veterinario;
import service.VeterinarioService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NuevoVeterinarioController implements Initializable {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtLicencia;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private Button    btnGuardar;
    @FXML private Label     lblMensaje;

    
    private Veterinario veterinarioEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    
    public void setModoEdicion(Veterinario v) {
        this.veterinarioEnEdicion = v;
        txtCedula.setText(v.getCedula());
        txtCedula.setDisable(true);
        txtNombre.setText(v.getNombre());
        txtApellido.setText(v.getApellido());
        txtEspecialidad.setText(v.getEspecialidad() != null ? v.getEspecialidad() : "");
        txtLicencia.setText(v.getNumeroLicencia() != null ? v.getNumeroLicencia() : "");
        txtTelefono.setText(v.getTelefono() != null ? v.getTelefono() : "");
        txtCorreo.setText(v.getEmail() != null ? v.getEmail() : "");
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        if (txtCedula.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()
                || txtApellido.getText().trim().isEmpty() || txtEspecialidad.getText().trim().isEmpty()) {
            mostrarMensaje("Cédula, nombre, apellido y especialidad son obligatorios.", "#D32F2F");
            return;
        }

        try {
            Veterinario v = new Veterinario(
                    txtCedula.getText().trim(),
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtEspecialidad.getText().trim(),
                    txtLicencia.getText().trim()
            );

            if (veterinarioEnEdicion == null) {
                new VeterinarioService().guardar(v);
                mostrarMensaje("Veterinario guardado exitosamente.", "#1B6B2F");
            } else {
                new VeterinarioService().actualizar(v);
                mostrarMensaje("Veterinario actualizado exitosamente.", "#1B6B2F");
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