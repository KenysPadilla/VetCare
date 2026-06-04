package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Cita;
import model.Paciente;
import model.Veterinario;
import service.CitaService;
import service.PacienteService;
import service.VeterinarioService;

import util.ComboBoxFilter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class NuevaCitaController implements Initializable {

    @FXML private ComboBox<Paciente>    cbPaciente;
    @FXML private Label                 lblEspecie;
    @FXML private Label                 lblRaza;
    @FXML private Label                 lblSexo;
    @FXML private ComboBox<Veterinario> cbVeterinario;
    @FXML private DatePicker            dpFecha;
    @FXML private ComboBox<String>      cbHora;
    @FXML private ComboBox<String>      cbTipo;
    @FXML private TextArea              txtMotivo;
    @FXML private Button                btnGuardar;
    @FXML private Label                 lblMensaje;

    private Cita citaEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            List<Paciente> pacientes = new PacienteService().listarTodos();
            ComboBoxFilter.apply(cbPaciente, pacientes, Object::toString);
        } catch (SQLException e) {
            System.err.println("Error cargando pacientes: " + e.getMessage());
        }

        cbPaciente.getSelectionModel().selectedItemProperty().addListener((obs, oldPac, newPac) -> {
            if (newPac != null) {
                lblEspecie.setText(orDash(newPac.getEspecie()));
                lblRaza.setText(orDash(newPac.getRaza()));
                lblSexo.setText(orDash(newPac.getSexo()));
            } else {
                lblEspecie.setText("—");
                lblRaza.setText("—");
                lblSexo.setText("—");
            }
        });

        try {
            List<Veterinario> vets = new VeterinarioService().listarActivos();
            ComboBoxFilter.apply(cbVeterinario, vets, Object::toString);
        } catch (SQLException e) {
            System.err.println("Error cargando veterinarios: " + e.getMessage());
        }

        cbHora.getItems().addAll(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00");

        cbTipo.getItems().addAll("Consulta General", "Vacunación", "Cirugía", "Control");

        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisabled(empty || date.compareTo(LocalDate.now()) <= 0);
                if (date.compareTo(LocalDate.now()) <= 0) {
                    setStyle("-fx-background-color: #e0e0e0;");
                }
            }
        });
        dpFecha.setValue(null);
    }

    
    public void setModoEdicion(Cita c) {
        this.citaEnEdicion = c;

        if (c.getPaciente() != null) {
            for (Paciente p : cbPaciente.getItems()) {
                if (p.getId() == c.getPaciente().getId()) {
                    cbPaciente.setValue(p);
                    lblEspecie.setText(orDash(p.getEspecie()));
                    lblRaza.setText(orDash(p.getRaza()));
                    lblSexo.setText(orDash(p.getSexo()));
                    break;
                }
            }
        }
        cbPaciente.setDisable(true);

        if (c.getVeterinario() != null) {
            for (Veterinario v : cbVeterinario.getItems()) {
                if (v.getCedula().equals(c.getVeterinario().getCedula())) {
                    cbVeterinario.setValue(v);
                    break;
                }
            }
        }
        cbVeterinario.setDisable(true);

        if (c.getFechaHora() != null) {
            dpFecha.setValue(c.getFechaHora().toLocalDate());
            cbHora.setValue(c.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        cbTipo.setValue(mapearTipoCitaInverso(c.getTipoCita()));
        txtMotivo.setText(c.getMotivo() != null ? c.getMotivo() : "");
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVeterinario.getValue() == null
                || dpFecha.getValue() == null || cbHora.getValue() == null
                || cbTipo.getValue() == null) {
            mostrarMensaje("Complete todos los campos obligatorios.", "#D32F2F");
            return;
        }

        if (dpFecha.getValue() == null || !dpFecha.getValue().isAfter(LocalDate.now())) {
            mostrarMensaje("La fecha debe ser a partir de mañana.", "#D32F2F");
            return;
        }

        try {
            LocalDateTime dt = LocalDateTime.of(
                    dpFecha.getValue(),
                    LocalTime.parse(cbHora.getValue()));

            if (citaEnEdicion == null) {
                Cita c = new Cita();
                c.setPaciente(cbPaciente.getValue());
                c.setVeterinario(cbVeterinario.getValue());
                c.setFechaHora(dt);
                c.setTipoCita(mapearTipoCita(cbTipo.getValue()));
                c.setEstadoCita("PROGRAMADA");
                c.setMotivo(txtMotivo.getText());
                new CitaService().guardar(c);
                mostrarMensaje("Cita guardada exitosamente.", "#1B6B2F");
            } else {
                citaEnEdicion.setFechaHora(dt);
                citaEnEdicion.setTipoCita(mapearTipoCita(cbTipo.getValue()));
                citaEnEdicion.setMotivo(txtMotivo.getText());
                new CitaService().actualizar(citaEnEdicion);
                mostrarMensaje("Cita actualizada exitosamente.", "#1B6B2F");
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

    private String mapearTipoCita(String tipo) {
        switch (tipo) {
            case "Consulta General": return "CONSULTA_GENERAL";
            case "Vacunación":       return "VACUNACION";
            case "Cirugía":          return "CIRUGIA";
            case "Control":          return "CONTROL";
            default:                 return tipo.toUpperCase().replace(" ", "_");
        }
    }

    private String mapearTipoCitaInverso(String tipoCita) {
        if (tipoCita == null) return null;
        switch (tipoCita) {
            case "CONSULTA_GENERAL": return "Consulta General";
            case "VACUNACION":       return "Vacunación";
            case "CIRUGIA":          return "Cirugía";
            case "CONTROL":          return "Control";
            default:                 return tipoCita;
        }
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}