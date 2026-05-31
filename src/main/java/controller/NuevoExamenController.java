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
import model.ExamenLab;
import model.Paciente;
import model.Veterinario;
import service.ConsultaService;
import service.ExamenLabService;
import service.PacienteService;
import service.VeterinarioService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class NuevoExamenController implements Initializable {

    @FXML private ComboBox<Paciente>    cbPaciente;
    @FXML private CheckBox              chkSinConsulta;
    @FXML private ComboBox<Consulta>    cbConsulta;
    @FXML private ComboBox<Veterinario> cbVeterinario;
    @FXML private ComboBox<String>      cbTipoExamen;
    @FXML private DatePicker            dpFechaSolicitud;
    @FXML private TextField             txtCosto;
    @FXML private Label                 lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // --- Converters ---
        cbPaciente.setConverter(new StringConverter<Paciente>() {
            @Override public String toString(Paciente p) { return p == null ? "" : p.getNombre() + " (" + p.getEspecie() + ")"; }
            @Override public Paciente fromString(String s) { return null; }
        });

        cbConsulta.setConverter(new StringConverter<Consulta>() {
            @Override
            public String toString(Consulta c) {
                if (c == null) return "";
                String fecha = (c.getFechaHora() != null)
                        ? " (" + c.getFechaHora().toLocalDate() + ")" : "";
                return "Consulta #" + c.getId() + fecha;
            }
            @Override public Consulta fromString(String s) { return null; }
        });

        cbVeterinario.setConverter(new StringConverter<Veterinario>() {
            @Override public String toString(Veterinario v) { return v == null ? "" : v.getNombreCompleto(); }
            @Override public Veterinario fromString(String s) { return null; }
        });

        // --- Cargar combos estáticos ---
        try {
            cbPaciente.getItems().setAll(new PacienteService().listarTodos());
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar pacientes: " + e.getMessage(), "#D32F2F");
        }
        try {
            cbVeterinario.getItems().setAll(new VeterinarioService().listarActivos());
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar veterinarios: " + e.getMessage(), "#D32F2F");
        }

        cbTipoExamen.getItems().addAll(
                "Hemograma", "Uroanálisis", "Cultivo", "Coprológico", "Rayos X", "Ecografía");

        // cbConsulta starts disabled until a patient is selected
        cbConsulta.setDisable(true);
    }

    @FXML
    private void handlePacienteSeleccionado() {
        Paciente pac = cbPaciente.getValue();
        cbConsulta.getItems().clear();
        cbConsulta.setValue(null);
        cbVeterinario.setValue(null);

        if (pac == null || chkSinConsulta.isSelected()) return;

        cbConsulta.setDisable(false);
        cbConsulta.setPromptText("Cargando...");
        try {
            List<Consulta> consultas = new ConsultaService().listarPorPaciente(pac.getId());
            cbConsulta.getItems().setAll(consultas);
            if (consultas.isEmpty()) {
                cbConsulta.setPromptText("Sin consultas registradas");
            } else {
                cbConsulta.setPromptText("Seleccionar consulta (opcional)");
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar consultas: " + e.getMessage(), "#D32F2F");
            cbConsulta.setPromptText("Error al cargar");
        }
    }

    @FXML
    private void handleConsultaSeleccionada() {
        Consulta sel = cbConsulta.getValue();
        if (sel == null) return;
        // Auto-fill veterinario from selected consultation
        if (sel.getVeterinario() != null) {
            for (Veterinario v : cbVeterinario.getItems()) {
                if (v.getCedula().equals(sel.getVeterinario().getCedula())) {
                    cbVeterinario.setValue(v);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSinConsulta() {
        boolean sinConsulta = chkSinConsulta.isSelected();
        cbConsulta.setDisable(sinConsulta);
        if (sinConsulta) {
            cbConsulta.setValue(null);
            cbConsulta.setPromptText("Sin consulta asociada");
        } else {
            // Re-trigger patient selection to reload consultations
            handlePacienteSeleccionado();
        }
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null) {
            mostrarMensaje("Debe seleccionar un paciente.", "#D32F2F");
            return;
        }
        if (cbVeterinario.getValue() == null) {
            mostrarMensaje("Debe seleccionar un veterinario.", "#D32F2F");
            return;
        }
        if (cbTipoExamen.getValue() == null || dpFechaSolicitud.getValue() == null) {
            mostrarMensaje("Tipo de examen y fecha son obligatorios.", "#D32F2F");
            return;
        }

        double costo = 0.0;
        if (!txtCosto.getText().trim().isEmpty()) {
            try {
                costo = Double.parseDouble(txtCosto.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarMensaje("El costo debe ser un valor numérico.", "#D32F2F");
                return;
            }
        }

        try {
            ExamenLab e = new ExamenLab();
            Consulta conSel = cbConsulta.getValue();
            if (conSel != null) {
                e.setConsulta(conSel);
            }
            e.setPaciente(cbPaciente.getValue());
            e.setVeterinario(cbVeterinario.getValue());
            e.setFechaHora(dpFechaSolicitud.getValue().atTime(java.time.LocalTime.now()));
            e.setTipoExamen(cbTipoExamen.getValue());
            e.setResultado("");
            e.setObservaciones("");
            e.setCosto(costo);

            new ExamenLabService().guardar(e);
            mostrarMensaje("Examen registrado exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (IllegalArgumentException ex) {
            mostrarMensaje(ex.getMessage(), "#D32F2F");
        } catch (java.sql.SQLException ex) {
            mostrarMensaje("Error al guardar: " + ex.getMessage(), "#D32F2F");
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