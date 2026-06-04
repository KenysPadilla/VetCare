package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Cita;
import model.Consulta;
import model.Medicamento;
import model.Paciente;
import model.Veterinario;
import service.CitaService;
import service.ConsultaService;
import service.MedicamentoService;
import service.PacienteService;
import service.VeterinarioService;

import util.ComboBoxFilter;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NuevaConsultaController implements Initializable {

    @FXML private CheckBox               chkSinCita;
    @FXML private VBox                   vboxConCita;
    @FXML private VBox                   vboxSinCita;
    @FXML private ComboBox<Cita>         cbCita;
    @FXML private ComboBox<Paciente>     cbPaciente;
    @FXML private ComboBox<Veterinario>  cbVeterinario;
    @FXML private TextArea               txtSintomas;
    @FXML private TextArea               txtDiagnostico;
    @FXML private TextArea               txtTratamiento;
    @FXML private Label                  lblMensaje;

    @FXML private TextField              txtBuscarMed;
    @FXML private ListView<Medicamento>  lvMedicamentos;
    @FXML private TextField              txtDosisEspecifica;
    @FXML private ListView<String>       lvPrescritos;
    @FXML private TextField              txtPrecioConsulta;
    @FXML private Label                  lblResumenConsulta;

    private List<Medicamento> todosMedicamentos = new ArrayList<>();

    private final ObservableList<String> prescripciones = FXCollections.observableArrayList();
    private final List<Medicamento> medicamentosPrescritos = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
        cbCita.setConverter(new StringConverter<Cita>() {
            @Override
            public String toString(Cita c) {
                if (c == null) return "";
                String pac = (c.getPaciente() != null) ? c.getPaciente().getNombre() : "?";
                String fecha = c.getFechaHora() != null
                        ? c.getFechaHora().toLocalDate() + " " + c.getFechaHora().toLocalTime() : "?";
                return "Cita #" + c.getId() + " — " + pac + " (" + fecha + ")";
            }
            @Override public Cita fromString(String s) { return null; }
        });

        
        cbPaciente.setConverter(new StringConverter<Paciente>() {
            @Override
            public String toString(Paciente p) {
                if (p == null) return "";
                String prop = (p.getPropietario() != null)
                        ? " / " + p.getPropietario().getNombreCompleto() : "";
                return p.getNombre() + " (" + p.getEspecie() + ")" + prop;
            }
            @Override public Paciente fromString(String s) { return null; }
        });

        
        cbVeterinario.setConverter(new StringConverter<Veterinario>() {
            @Override
            public String toString(Veterinario v) {
                if (v == null) return "";
                String esp = (v.getEspecialidad() != null) ? " — " + v.getEspecialidad() : "";
                return v.getNombreCompleto() + esp;
            }
            @Override public Veterinario fromString(String s) { return null; }
        });

        
        lvMedicamentos.setCellFactory(lv -> new javafx.scene.control.ListCell<Medicamento>() {
            @Override
            protected void updateItem(Medicamento m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setText(null); return; }
                String conc = (m.getConcentracion() != null && !m.getConcentracion().isEmpty())
                        ? " [" + m.getConcentracion() + "]" : "";
                String estado = m.calcularEstado();
                String sufijo = ("Vencido".equals(estado) || "Sin stock".equals(estado))
                        ? " ⚠ " + estado : "";
                setText(m.getNombre() + conc + " — " + m.getFabricante() + sufijo);
            }
        });

        
        try {
            List<Cita> citasDisponibles = new CitaService().listarDisponiblesParaConsulta();
            ComboBoxFilter.apply(cbCita, citasDisponibles,
                c -> "Cita #" + c.getId()
                    + (c.getPaciente() != null ? " – " + c.getPaciente().getNombre() : "")
                    + (c.getFechaHora() != null ? " " + c.getFechaHora().toLocalDate() : ""));
            if (citasDisponibles.isEmpty()) {
                mostrarMensaje("No hay citas disponibles en este momento.", "#856404");
            }
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar citas: " + e.getMessage(), "#D32F2F");
        }

        
        try {
            List<Paciente> pacientes = new PacienteService().listarTodos();
            ComboBoxFilter.apply(cbPaciente, pacientes, Object::toString);
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar pacientes: " + e.getMessage(), "#D32F2F");
        }
        try {
            List<Veterinario> vets = new VeterinarioService().listarActivos();
            ComboBoxFilter.apply(cbVeterinario, vets, Object::toString);
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar veterinarios: " + e.getMessage(), "#D32F2F");
        }

        
        try {
            todosMedicamentos = new MedicamentoService().listarTodos();
            lvMedicamentos.getItems().setAll(todosMedicamentos);
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar medicamentos: " + e.getMessage(), "#D32F2F");
        }

        txtBuscarMed.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal == null ? "" : newVal.trim().toLowerCase();
            if (filtro.isEmpty()) {
                lvMedicamentos.getItems().setAll(todosMedicamentos);
            } else {
                List<Medicamento> filtrados = new ArrayList<>();
                for (Medicamento m : todosMedicamentos) {
                    if (m.getNombre().toLowerCase().contains(filtro)
                            || (m.getDescripcion() != null
                                && m.getDescripcion().toLowerCase().contains(filtro))) {
                        filtrados.add(m);
                    }
                }
                lvMedicamentos.getItems().setAll(filtrados);
            }
        });

        lvPrescritos.setItems(prescripciones);

        ui.NumericFormatter.apply(txtPrecioConsulta);
        txtPrecioConsulta.textProperty().addListener((obs, o, n) -> recalcularTotal());
        recalcularTotal();
    }

    @FXML
    private void handleToggleModo() {
        boolean sinCita = chkSinCita.isSelected();
        vboxConCita.setVisible(!sinCita);
        vboxConCita.setManaged(!sinCita);
        vboxSinCita.setVisible(sinCita);
        vboxSinCita.setManaged(sinCita);
        if (sinCita) {
            cbCita.setValue(null);
        } else {
            cbPaciente.setValue(null);
            cbVeterinario.setValue(null);
        }
        mostrarMensaje("", "#000000");
    }

    @FXML
    private void handleAgregarMedicamento() {
        Medicamento sel = lvMedicamentos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarMensaje("Seleccione un medicamento de la lista.", "#D32F2F");
            return;
        }
        String estado = sel.calcularEstado();
        if ("Vencido".equals(estado)) {
            mostrarAlerta("El medicamento \"" + sel.getNombre() + "\" está vencido y no puede recetarse.");
            return;
        }
        if ("Sin stock".equals(estado)) {
            mostrarAlerta("El medicamento \"" + sel.getNombre() + "\" no tiene stock disponible.");
            return;
        }
        String dosis = txtDosisEspecifica.getText().trim();
        if (dosis.isEmpty()) {
            mostrarMensaje("Ingrese la dosis o instrucciones para el medicamento.", "#D32F2F");
            return;
        }
        String conc = (sel.getConcentracion() != null && !sel.getConcentracion().isEmpty())
                ? " [" + sel.getConcentracion() + "]" : "";
        prescripciones.add(sel.getNombre() + conc + " — " + dosis);
        medicamentosPrescritos.add(sel);
        txtDosisEspecifica.clear();
        recalcularTotal();
        mostrarMensaje("", "#000000");
    }

    @FXML
    private void handleQuitarMedicamento() {
        int idx = lvPrescritos.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            prescripciones.remove(idx);
            medicamentosPrescritos.remove(idx);
            recalcularTotal();
        }
    }

    @FXML
    private void handleGuardar() {
        boolean sinCita = chkSinCita.isSelected();
        String sintomas    = txtSintomas.getText().trim();
        String diagnostico = txtDiagnostico.getText().trim();

        if (sintomas.isEmpty() || diagnostico.isEmpty()) {
            mostrarMensaje("Síntomas y diagnóstico son obligatorios.", "#D32F2F");
            return;
        }
        double precioConsulta = ui.NumericFormatter.toDouble(txtPrecioConsulta);
        if (precioConsulta <= 0) {
            mostrarMensaje("Ingrese un precio válido para la consulta.", "#D32F2F");
            return;
        }
        if (!sinCita && cbCita.getValue() == null) {
            mostrarMensaje("Seleccione una cita o marque 'Sin cita previa'.", "#D32F2F");
            return;
        }
        if (sinCita && (cbPaciente.getValue() == null || cbVeterinario.getValue() == null)) {
            mostrarMensaje("Seleccione paciente y veterinario.", "#D32F2F");
            return;
        }

        try {
            StringBuilder tratamiento = new StringBuilder();
            if (!prescripciones.isEmpty()) {
                tratamiento.append("MEDICAMENTOS PRESCRITOS:\n");
                for (String p : prescripciones) {
                    tratamiento.append("  • ").append(p).append("\n");
                }
            }
            String notas = txtTratamiento.getText().trim();
            if (!notas.isEmpty()) {
                if (tratamiento.length() > 0) tratamiento.append("\n");
                tratamiento.append("NOTAS: ").append(notas);
            }

            Consulta c = new Consulta();
            c.setFechaHora(LocalDateTime.now());
            c.setSintomas(sintomas);
            c.setDiagnostico(diagnostico);
            c.setTratamiento(tratamiento.toString());
            c.setCosto(precioConsulta);

            ConsultaService svc = new ConsultaService();
            if (sinCita) {
                c.setPaciente(cbPaciente.getValue());
                c.setVeterinario(cbVeterinario.getValue());
                svc.guardarSinCita(c);
            } else {
                Cita cita = cbCita.getValue();
                c.setCita(cita);
                c.setPaciente(cita.getPaciente());
                c.setVeterinario(cita.getVeterinario());
                svc.guardar(c);
            }

            MedicamentoService medService = new MedicamentoService();
            List<String> erroresStock = new ArrayList<>();
            for (Medicamento med : medicamentosPrescritos) {
                try {
                    medService.descontarStock(med.getId(), 1);
                } catch (java.sql.SQLException ex) {
                    erroresStock.add(med.getNombre() + ": " + ex.getMessage());
                }
            }
            if (!erroresStock.isEmpty()) {
                mostrarAlerta("Consulta guardada, pero hubo errores al descontar stock:\n"
                        + String.join("\n", erroresStock));
            }

            mostrarMensaje("Consulta guardada exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), "#D32F2F");
        } catch (RuntimeException e) {
            mostrarMensaje("Error inesperado: " + e.getMessage(), "#D32F2F");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void recalcularTotal() {
        double base = ui.NumericFormatter.toDouble(txtPrecioConsulta);
        lblResumenConsulta.setText(base > 0 ? ui.NumericFormatter.formatCurrency(base) : "—");
    }

    private void cerrarVentana() {
        ((Stage) lblMensaje.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}