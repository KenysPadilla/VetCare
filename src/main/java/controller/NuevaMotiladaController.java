package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Estilista;
import model.Paciente;
import model.ServicioEstetico;
import service.EstilistaService;
import service.PacienteService;
import service.ServicioEsteticoService;

import util.ComboBoxFilter;

import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador del formulario de registro de nuevo servicio de motilada.
 *
 * <p>Carga pacientes y estilistas desde la base de datos en
 * {@code initialize()}. El estilo de corte, el largo y los servicios
 * adicionales se seleccionan de listas fijas. El precio queda fijo en
 * {@code 60000.0}.</p>
 *
 * <p>Los valores almacenados para {@code estilo_corte} y {@code largo_corte}
 * siguen la convencion Oracle en mayusculas; la presentacion usa nombres
 * legibles. La conversion se realiza en {@link #mapearEstiloCorte(String)} y
 * {@link #mapearLargoCorte(String)}.</p>
 */
public class NuevaMotiladaController implements Initializable {

    @FXML private ComboBox<Paciente>  cbPaciente;
    @FXML private ComboBox<Estilista> cbEstilista;
    @FXML private DatePicker          dpFecha;
    @FXML private ComboBox<String>    cbHora;
    @FXML private ComboBox<String>    cbEstiloCorte;
    @FXML private ComboBox<String>    cbLargo;
    @FXML private CheckBox            chkUnas;
    @FXML private CheckBox            chkLimpieza;
    @FXML private TextArea            txtObservaciones;
    @FXML private Label               lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbHora.getItems().addAll(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00");

        cbEstiloCorte.getItems().addAll("Higiénico", "Estético", "Raza", "Personalizado");
        cbLargo.getItems().addAll("Corto", "Mediano", "Largo");

        try {
            List<Paciente>  pacientes  = new PacienteService().listarTodos();
            List<Estilista> estilistas = new EstilistaService().listarParaMotilada();
            ComboBoxFilter.apply(cbPaciente,  pacientes,  Object::toString);
            ComboBoxFilter.apply(cbEstilista, estilistas, Object::toString);
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar datos: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbEstilista.getValue() == null
                || dpFecha.getValue() == null
                || cbHora.getValue() == null
                || cbEstiloCorte.getValue() == null
                || cbLargo.getValue() == null) {
            mostrarMensaje("Paciente, estilista, fecha, hora, estilo y largo son obligatorios.", "#D32F2F");
            return;
        }

        try {
            System.out.println("[DEBUG] 1. Iniciando guardar motilada");
            ServicioEstetico s = new ServicioEstetico();
            s.setTipoServicio("MOTILADA");
            s.setPaciente(cbPaciente.getValue());
            s.setEstilista(cbEstilista.getValue());
            s.setFechaHora(dpFecha.getValue().atTime(LocalTime.parse(cbHora.getValue())));
            s.setPrecio(60000.0);
            s.setEstadoServicio("PROGRAMADO");
            s.setEstiloCorte(mapearEstiloCorte(cbEstiloCorte.getValue()));
            s.setLargoCorte(mapearLargoCorte(cbLargo.getValue()));
            s.setIncluyeUnas(chkUnas.isSelected());
            s.setIncluyeLimpieza(chkLimpieza.isSelected());
            s.setObservaciones(txtObservaciones.getText().trim());

            System.out.println("[DEBUG] 2. Paciente: " + cbPaciente.getValue());
            System.out.println("[DEBUG] 3. Estilista: " + cbEstilista.getValue());
            System.out.println("[DEBUG] 4. tipoServicio: " + s.getTipoServicio());
            System.out.println("[DEBUG] 5. fechaHora: " + s.getFechaHora());
            System.out.println("[DEBUG] 6. estiloCorte: " + s.getEstiloCorte() + ", largoCorte: " + s.getLargoCorte());
            System.out.println("[DEBUG] 7. Llamando al servicio...");

            new ServicioEsteticoService().guardar(s);

            System.out.println("[DEBUG] 8. Guardado exitosamente");
            mostrarMensaje("Motilada programada exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al guardar: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    // -------------------------------------------------------------------------
    // Mapeo de valores de presentacion a codigos Oracle
    // -------------------------------------------------------------------------

    /**
     * Convierte el estilo de corte legible a su codigo Oracle en mayusculas.
     *
     * @param display texto mostrado al usuario
     * @return codigo Oracle para {@code estilo_corte}
     */
    private String mapearEstiloCorte(String display) {
        switch (display) {
            case "Higiénico":    return "HIGIENICO";
            case "Estético":     return "ESTETICO";
            case "Raza":         return "RAZA";
            case "Personalizado":return "PERSONALIZADO";
            default:             return display.toUpperCase();
        }
    }

    /**
     * Convierte el largo de corte legible a su codigo Oracle en mayusculas.
     *
     * @param display texto mostrado al usuario
     * @return codigo Oracle para {@code largo_corte}
     */
    private String mapearLargoCorte(String display) {
        switch (display) {
            case "Corto":   return "CORTO";
            case "Mediano": return "MEDIANO";
            case "Largo":   return "LARGO";
            default:        return display.toUpperCase();
        }
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