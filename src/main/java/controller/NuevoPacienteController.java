package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Paciente;
import model.Propietario;
import service.PacienteService;
import service.PropietarioService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NuevoPacienteController implements Initializable {

    @FXML private ComboBox<Propietario> cbPropietario;
    @FXML private TextField             txtNombre;
    @FXML private ComboBox<String>      cbEspecie;
    @FXML private TextField             txtEspecieOtro;
    @FXML private TextField             txtRaza;
    @FXML private ComboBox<String>      cbSexo;
    @FXML private TextField             txtPeso;
    @FXML private DatePicker            dpFechaNacimiento;
    @FXML private TextField             txtMicrochip;
    @FXML private Button                btnGuardar;
    @FXML private Label                 lblMensaje;

    /** No nulo cuando el formulario abre en modo edicion. */
    private Paciente pacienteEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            cbPropietario.getItems().addAll(new PropietarioService().listarTodos());
        } catch (SQLException e) {
            System.err.println("Error cargando propietarios: " + e.getMessage());
        }
        cbEspecie.getItems().addAll("Perro", "Gato", "Ave", "Reptil", "Otro");
        cbEspecie.setOnAction(e -> {
            boolean esOtro = "Otro".equals(cbEspecie.getValue());
            txtEspecieOtro.setVisible(esOtro);
            txtEspecieOtro.setManaged(esOtro);
            if (!esOtro) txtEspecieOtro.clear();
        });
        cbSexo.getItems().addAll("Macho", "Hembra");
    }

    /**
     * Pre-rellena el formulario con los datos del paciente a editar,
     * deshabilita el selector de propietario y cambia el boton a "Actualizar".
     */
    public void setModoEdicion(Paciente pac) {
        this.pacienteEnEdicion = pac;

        // Seleccionar el propietario en el combo por cedula
        if (pac.getPropietario() != null) {
            String cedula = pac.getPropietario().getCedula();
            for (Propietario p : cbPropietario.getItems()) {
                if (p.getCedula().equals(cedula)) {
                    cbPropietario.setValue(p);
                    break;
                }
            }
        }
        cbPropietario.setDisable(true);

        txtNombre.setText(pac.getNombre());
        java.util.List<String> especiesFijas = java.util.List.of("Perro", "Gato", "Ave", "Reptil");
        if (pac.getEspecie() != null && !especiesFijas.contains(pac.getEspecie())) {
            cbEspecie.setValue("Otro");
            txtEspecieOtro.setText(pac.getEspecie());
            txtEspecieOtro.setVisible(true);
            txtEspecieOtro.setManaged(true);
        } else {
            cbEspecie.setValue(pac.getEspecie());
        }
        txtRaza.setText(pac.getRaza() != null ? pac.getRaza() : "");
        cbSexo.setValue(pac.getSexo());
        if (pac.getPeso() > 0) {
            txtPeso.setText(String.valueOf(pac.getPeso()));
        }
        dpFechaNacimiento.setValue(pac.getFechaNacimiento());
        txtMicrochip.setText(pac.getMicrochip() != null ? pac.getMicrochip() : "");
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        String especieVal = "Otro".equals(cbEspecie.getValue())
                ? txtEspecieOtro.getText().trim() : cbEspecie.getValue();

        if (txtNombre.getText().trim().isEmpty()
                || especieVal == null || especieVal.isEmpty() || cbSexo.getValue() == null) {
            mostrarMensaje("Nombre, especie y sexo son obligatorios.", "#D32F2F");
            return;
        }

        try {
            Paciente pac = new Paciente();
            pac.setPropietario(cbPropietario.getValue()); // puede ser null
            pac.setNombre(txtNombre.getText().trim());
            pac.setEspecie(especieVal);
            pac.setRaza(txtRaza.getText().trim());
            pac.setSexo(cbSexo.getValue());
            if (!txtPeso.getText().trim().isEmpty()) {
                pac.setPeso(Double.parseDouble(txtPeso.getText().trim()));
            }
            pac.setFechaNacimiento(dpFechaNacimiento.getValue());
            pac.setMicrochip(txtMicrochip.getText().trim());

            if (pacienteEnEdicion == null) {
                new PacienteService().guardar(pac);
                mostrarMensaje("Paciente guardado exitosamente.", "#1B6B2F");
            } else {
                pac.setId(pacienteEnEdicion.getId());
                new PacienteService().actualizar(pac);
                mostrarMensaje("Paciente actualizado exitosamente.", "#1B6B2F");
            }
            ((Stage) lblMensaje.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            mostrarMensaje("El peso debe ser un número válido.", "#D32F2F");
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