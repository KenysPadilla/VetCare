package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Paciente;
import model.Propietario;
import service.PacienteService;
import service.PropietarioService;
import util.ConexionBD;

import java.net.URL;
import java.sql.Connection;
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

    @FXML private VBox             panelMascota;
    @FXML private TextField        txtNombreMascota;
    @FXML private ComboBox<String> cbEspecie;
    @FXML private TextField        txtEspecieOtro;
    @FXML private ComboBox<String> cbSexo;
    @FXML private TextField        txtRaza;
    @FXML private TextField        txtPeso;
    @FXML private DatePicker       dpFechaNacimiento;
    @FXML private TextField        txtMicrochip;

    private Propietario propietarioEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEspecie.getItems().addAll("Canino", "Felino", "Ave", "Reptil", "Roedor", "Otro");
        cbSexo.getItems().addAll("Macho", "Hembra");

        cbEspecie.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esOtro = "Otro".equals(newVal);
            txtEspecieOtro.setVisible(esOtro);
            txtEspecieOtro.setManaged(esOtro);
            if (!esOtro) txtEspecieOtro.clear();
        });
    }

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
        panelMascota.setVisible(false);
        panelMascota.setManaged(false);
    }

    @FXML
    private void handleGuardar() {
        String cedula   = txtCedula.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Cédula, nombre y apellido son obligatorios.", "#D32F2F");
            return;
        }

        String nombreMascota = txtNombreMascota != null ? txtNombreMascota.getText().trim() : "";
        if (propietarioEnEdicion == null && nombreMascota.isEmpty()) {
            mostrarMensaje("El nombre de la mascota es obligatorio.", "#D32F2F");
            return;
        }
        boolean guardarMascota = !nombreMascota.isEmpty() && propietarioEnEdicion == null;

        double peso = 0;
        if (guardarMascota && txtPeso != null && !txtPeso.getText().trim().isEmpty()) {
            try {
                peso = Double.parseDouble(txtPeso.getText().trim().replace(",", "."));
                if (peso < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarMensaje("El peso debe ser un número positivo (ej: 4.5).", "#D32F2F");
                return;
            }
        }

        Connection con = ConexionBD.getInstancia().getConexion();
        try {
            Propietario p = new Propietario(
                    cedula, nombre, apellido,
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtDireccion.getText().trim()
            );

            if (propietarioEnEdicion == null) {
                con.setAutoCommit(false);
                try {
                    new PropietarioService().guardar(p);

                    String especie = "Otro".equals(cbEspecie.getValue())
                            ? txtEspecieOtro.getText().trim()
                            : (cbEspecie.getValue() != null ? cbEspecie.getValue() : "");

                    Paciente mascota = new Paciente(
                            nombreMascota,
                            especie,
                            txtRaza.getText().trim(),
                            cbSexo.getValue() != null ? cbSexo.getValue() : "",
                            peso,
                            dpFechaNacimiento.getValue(),
                            txtMicrochip.getText().trim(),
                            p
                    );
                    new PacienteService().guardar(mascota);
                    con.commit();
                } catch (Exception ex) {
                    try { con.rollback(); } catch (SQLException ignored) {}
                    mostrarMensaje("Error al guardar: " + ex.getMessage(), "#D32F2F");
                    return;
                } finally {
                    try { con.setAutoCommit(true); } catch (SQLException ignored) {}
                }
                mostrarMensaje("Propietario y mascota guardados exitosamente.", "#1B6B2F");

            } else {
                new PropietarioService().actualizar(p);
                mostrarMensaje("Propietario actualizado exitosamente.", "#1B6B2F");
            }

            ((Stage) lblMensaje.getScene().getWindow()).close();

        } catch (Exception e) {
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
