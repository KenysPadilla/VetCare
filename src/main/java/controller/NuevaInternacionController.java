package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Consulta;
import model.Internacion;
import model.Paciente;
import model.Veterinario;
import service.ConsultaService;
import service.InternacionService;
import ui.NumericFormatter;
import service.PacienteService;
import service.VeterinarioService;

import util.ComboBoxFilter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class NuevaInternacionController implements Initializable {

    @FXML private ComboBox<Paciente>    cbPaciente;
    @FXML private ComboBox<Veterinario> cbVeterinario;
    @FXML private ComboBox<Consulta>    cbConsulta;
    @FXML private CheckBox              chkSinConsulta;
    @FXML private DatePicker            dpFechaIngreso;
    @FXML private TextField             txtCostoDia;
    @FXML private Label                 lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        cbConsulta.setConverter(new StringConverter<Consulta>() {
            @Override
            public String toString(Consulta c) {
                if (c == null) return "Sin consulta asociada";
                String diag = c.getDiagnostico();
                String diagCorto = (diag != null && diag.length() > 30)
                        ? diag.substring(0, 30) + "…" : diag;
                return "Consulta #" + c.getId()
                        + (c.getFechaHora() != null
                           ? " — " + c.getFechaHora().toLocalDate() : "")
                        + (diagCorto != null && !diagCorto.isBlank()
                           ? " — " + diagCorto : "");
            }
            @Override
            public Consulta fromString(String s) { return null; }
        });

        cbConsulta.setDisable(true);

        dpFechaIngreso.setValue(java.time.LocalDate.now());
        NumericFormatter.apply(txtCostoDia);

        try {
            List<Paciente>    pacientes = new PacienteService().listarTodos();
            List<Veterinario> vets      = new VeterinarioService().listarActivos();
            ComboBoxFilter.apply(cbPaciente,    pacientes, Object::toString);
            ComboBoxFilter.apply(cbVeterinario, vets,      Object::toString);
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar datos: " + e.getMessage(), "#D32F2F");
        }

        cbPaciente.setOnAction(event -> {
            cbConsulta.getItems().clear();
            cbConsulta.setValue(null);
            chkSinConsulta.setSelected(false);

            Paciente pacSeleccionado = cbPaciente.getValue();
            if (pacSeleccionado != null) {
                cargarConsultasDePaciente(pacSeleccionado.getId());
            } else {
                cbConsulta.setDisable(true);
                cbConsulta.setPromptText("Primero selecciona un paciente");
            }
        });

        chkSinConsulta.setOnAction(event -> {
            if (chkSinConsulta.isSelected()) {
                cbConsulta.getItems().clear();
                cbConsulta.setValue(null);
                cbConsulta.setDisable(true);
                cbConsulta.setPromptText("Sin consulta asociada");
            } else {
                Paciente p = cbPaciente.getValue();
                if (p != null) {
                    cargarConsultasDePaciente(p.getId());
                } else {
                    cbConsulta.setDisable(true);
                    cbConsulta.setPromptText("Primero selecciona un paciente");
                }
            }
        });
    }

    
    private void cargarConsultasDePaciente(int idPaciente) {
        try {
            ArrayList<Consulta> consultas =
                    new ConsultaService().listarPorPaciente(idPaciente);
            cbConsulta.getItems().clear();

            if (consultas.isEmpty()) {
                cbConsulta.setDisable(true);
                cbConsulta.setPromptText("Este paciente no tiene consultas registradas");
            } else {
                cbConsulta.getItems().addAll(consultas);
                cbConsulta.setDisable(false);
                cbConsulta.setPromptText("Selecciona la consulta asociada");
            }
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar consultas: " + e.getMessage(), "#D32F2F");
            cbConsulta.setDisable(true);
            cbConsulta.setPromptText("Error al cargar consultas");
        }
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVeterinario.getValue() == null
                || dpFechaIngreso.getValue() == null
                || txtCostoDia.getText().trim().isEmpty()) {
            mostrarMensaje("Paciente, veterinario, fecha y costo son obligatorios.", "#D32F2F");
            return;
        }

        double costoDia = NumericFormatter.toDouble(txtCostoDia);

        Consulta consultaAsociada = (cbConsulta.isDisabled() || cbConsulta.getValue() == null)
                ? null : cbConsulta.getValue();

        try {
            Internacion i = new Internacion();
            i.setPaciente(cbPaciente.getValue());
            i.setVeterinario(cbVeterinario.getValue());
            i.setConsulta(consultaAsociada);
            i.setFechaHoraIngreso(dpFechaIngreso.getValue().atStartOfDay());
            i.setFechaHoraEgreso(null);
            i.setMotivo("");
            i.setDiagnostico("");
            i.setCostoDia(costoDia);
            i.setObservaciones("");

            new InternacionService().guardar(i);

            mostrarMensaje("Internación registrada exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), "#D32F2F");
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