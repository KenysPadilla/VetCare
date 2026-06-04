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

public class NuevoBanoController implements Initializable {

    @FXML private ComboBox<Paciente>  cbPaciente;
    @FXML private ComboBox<Estilista> cbEstilista;
    @FXML private DatePicker          dpFecha;
    @FXML private ComboBox<String>    cbHora;
    @FXML private ComboBox<String>    cbTipoBano;
    @FXML private CheckBox            chkSecado;
    @FXML private CheckBox            chkPerfume;
    @FXML private TextArea            txtObservaciones;
    @FXML private Label               lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbHora.getItems().addAll(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00");

        cbTipoBano.getItems().addAll("Básico", "Medicado", "Antipulgas", "Hidratante");

        try {
            List<Paciente>  pacientes  = new PacienteService().listarTodos();
            List<Estilista> estilistas = new EstilistaService().listarParaBano();
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
                || cbTipoBano.getValue() == null) {
            mostrarMensaje("Paciente, estilista, fecha, hora y tipo son obligatorios.", "#D32F2F");
            return;
        }

        try {
            ServicioEstetico s = new ServicioEstetico();
            s.setTipoServicio("BANO");
            s.setPaciente(cbPaciente.getValue());
            s.setEstilista(cbEstilista.getValue());
            s.setFechaHora(dpFecha.getValue().atTime(LocalTime.parse(cbHora.getValue())));
            s.setPrecio(50000.0);
            s.setEstadoServicio("PROGRAMADO");
            s.setTipoBano(mapearTipoBano(cbTipoBano.getValue()));
            s.setIncluyeSecado(chkSecado.isSelected());
            s.setIncluyePerfume(chkPerfume.isSelected());
            s.setObservaciones(txtObservaciones.getText().trim());

            new ServicioEsteticoService().guardar(s);
            mostrarMensaje("Baño programado exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private String mapearTipoBano(String display) {
        switch (display) {
            case "Básico":     return "BASICO";
            case "Medicado":   return "MEDICADO";
            case "Antipulgas": return "ANTIPULGAS";
            case "Hidratante": return "HIDRATANTE";
            default:           return display.toUpperCase();
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
