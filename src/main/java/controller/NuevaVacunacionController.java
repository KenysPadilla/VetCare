package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Paciente;
import model.Vacuna;
import model.Vacunacion;
import model.Veterinario;
import service.PacienteService;
import service.VacunaService;
import service.VacunacionService;
import service.VeterinarioService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NuevaVacunacionController implements Initializable {

    @FXML private ComboBox<Paciente>    cbPaciente;
    @FXML private ComboBox<Vacuna>      cbVacuna;
    @FXML private ComboBox<Veterinario> cbVeterinario;
    @FXML private DatePicker            dpFechaAplicacion;
    @FXML private DatePicker            dpFechaProxima;
    @FXML private TextArea              txtObservaciones;
    @FXML private Label                 lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            cbPaciente.getItems().setAll(new PacienteService().listarTodos());
            cbVeterinario.getItems().setAll(new VeterinarioService().listarActivos());

            // Solo cargar vacunas disponibles (no Vencido, no Sin stock)
            List<Vacuna> disponibles = new ArrayList<>();
            for (Vacuna v : new VacunaService().listarTodos()) {
                String estado = v.calcularEstado();
                if (!"Vencido".equals(estado) && !"Sin stock".equals(estado)) {
                    disponibles.add(v);
                }
            }
            cbVacuna.getItems().setAll(disponibles);
            if (disponibles.isEmpty()) {
                mostrarMensaje("No hay vacunas disponibles en inventario.", "#856404");
            }

        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al cargar datos: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleGuardar() {
        if (cbPaciente.getValue() == null || cbVacuna.getValue() == null
                || cbVeterinario.getValue() == null
                || dpFechaAplicacion.getValue() == null) {
            mostrarMensaje("Paciente, vacuna, veterinario y fecha son obligatorios.", "#D32F2F");
            return;
        }

        // Validación de estado en el momento de guardar (doble verificación)
        Vacuna vac = cbVacuna.getValue();
        String estado = vac.calcularEstado();
        if ("Vencido".equals(estado) || "Sin stock".equals(estado)) {
            mostrarMensaje("La vacuna seleccionada no está disponible (" + estado + ").", "#D32F2F");
            return;
        }

        try {
            Vacunacion v = new Vacunacion();
            v.setPaciente(cbPaciente.getValue());
            v.setVacuna(vac);
            v.setVeterinario(cbVeterinario.getValue());
            v.setFechaHoraAplicacion(dpFechaAplicacion.getValue().atStartOfDay());
            v.setFechaProxima(dpFechaProxima.getValue());
            v.setObservaciones(txtObservaciones.getText().trim());

            new VacunacionService().guardar(v);

            mostrarMensaje("Vacunación registrada exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (java.sql.SQLException e) {
            String msg = e.getMessage();
            // El VacunacionDAO lanza un mensaje claro si no hay stock
            mostrarMensaje(msg != null && msg.contains("stock")
                    ? msg : "Error al guardar: " + msg, "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) lblMensaje.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}