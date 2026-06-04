package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Estilista;
import service.EstilistaService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NuevoEstilistaController implements Initializable {

    @FXML private TextField        txtCedula;
    @FXML private TextField        txtNombre;
    @FXML private TextField        txtApellido;
    @FXML private ComboBox<String> cbEspecialidad;
    @FXML private TextField        txtTelefono;
    @FXML private TextField        txtCorreo;
    @FXML private Button           btnGuardar;
    @FXML private Label            lblMensaje;

    private Estilista estilistaEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEspecialidad.getItems().addAll("Baño", "Motilada", "Ambos");
    }

    
    public void setModoEdicion(Estilista e) {
        this.estilistaEnEdicion = e;
        txtCedula.setText(e.getCedula());
        txtCedula.setDisable(true);
        txtNombre.setText(e.getNombre());
        txtApellido.setText(e.getApellido());
        cbEspecialidad.setValue(e.getEspecialidadEstetica());
        txtTelefono.setText(e.getTelefono() != null ? e.getTelefono() : "");
        txtCorreo.setText(e.getEmail() != null ? e.getEmail() : "");
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        if (txtCedula.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()
                || txtApellido.getText().trim().isEmpty() || cbEspecialidad.getValue() == null) {
            mostrarMensaje("Cédula, nombre, apellido y especialidad son obligatorios.", "#D32F2F");
            return;
        }

        try {
            Estilista e = new Estilista(
                    txtCedula.getText().trim(),
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim(),
                    cbEspecialidad.getValue()
            );

            if (estilistaEnEdicion == null) {
                new EstilistaService().guardar(e);
                mostrarMensaje("Estilista guardado exitosamente.", "#1B6B2F");
            } else {
                new EstilistaService().actualizar(e);
                mostrarMensaje("Estilista actualizado exitosamente.", "#1B6B2F");
            }
            ((Stage) lblMensaje.getScene().getWindow()).close();
        } catch (SQLException ex) {
            mostrarMensaje("Error: " + ex.getMessage(), "#D32F2F");
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