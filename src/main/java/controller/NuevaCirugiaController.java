package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Cirugia;
import model.Paciente;
import model.Veterinario;
import service.CirugiaService;
import ui.NumericFormatter;
import service.PacienteService;
import service.VeterinarioService;

import util.ComboBoxFilter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador del formulario de registro de nueva cirugia.
 *
 * <p>Carga la lista de pacientes y veterinarios desde la base de datos en
 * {@code initialize()}. El tipo de cirugia se ingresa como texto libre;
 * el tipo de anestesia se selecciona de un combo fijo. El resultado
 * post-operatorio es opcional y puede completarse en una edicion posterior.</p>
 */
public class NuevaCirugiaController implements Initializable {

    @FXML private ComboBox<Paciente>    cbPaciente;
    @FXML private ComboBox<Veterinario> cbVeterinario;
    @FXML private TextField             txtTipo;
    @FXML private DatePicker            dpFecha;
    @FXML private ComboBox<String>      cbAnestesia;
    @FXML private TextField             txtCosto;
    @FXML private TextArea              txtDescripcion;
    @FXML private Label                 lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbAnestesia.getItems().addAll("General", "Local", "Sedación");
        NumericFormatter.apply(txtCosto);

        try {
            List<Paciente>    pacientes = new PacienteService().listarTodos();
            List<Veterinario> vets      = new VeterinarioService().listarActivos();
            ComboBoxFilter.apply(cbPaciente,    pacientes, Object::toString);
            ComboBoxFilter.apply(cbVeterinario, vets,      Object::toString);
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar datos: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVeterinario.getValue() == null
                || txtTipo.getText().trim().isEmpty()
                || dpFecha.getValue() == null
                || cbAnestesia.getValue() == null) {
            mostrarMensaje("Paciente, veterinario, tipo, fecha y anestesia son obligatorios.", "#D32F2F");
            return;
        }

        double costo = NumericFormatter.toDouble(txtCosto);

        try {
            Cirugia cir = new Cirugia();
            cir.setPaciente(cbPaciente.getValue());
            cir.setVeterinario(cbVeterinario.getValue());
            cir.setFechaHora(dpFecha.getValue().atTime(java.time.LocalTime.now()));
            cir.setTipoCirugia(txtTipo.getText().trim());
            cir.setAnestesia(cbAnestesia.getValue());
            cir.setDescripcion(txtDescripcion.getText().trim());
            cir.setResultado("");
            cir.setCosto(costo);

            new CirugiaService().guardar(cir);

            mostrarMensaje("Cirugía registrada exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), "#D32F2F");
        } catch (Exception e) {
            mostrarMensaje("Error inesperado: " + e.getClass().getSimpleName() + " — " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) lblMensaje.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}