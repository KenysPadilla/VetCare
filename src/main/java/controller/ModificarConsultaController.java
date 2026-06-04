package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Consulta;
import service.ConsultaService;
import ui.NumericFormatter;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ModificarConsultaController {

    @FXML private Label   lblHeaderSubtitle;
    @FXML private Label   lblPaciente;
    @FXML private Label   lblVeterinario;
    @FXML private Label   lblFecha;
    @FXML private TextArea txtSintomas;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtTratamiento;
    @FXML private TextField txtCosto;
    @FXML private Label   lblMensaje;

    private Consulta consulta;
    private Runnable onGuardado;

    private static final DateTimeFormatter FECHA_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setConsulta(Consulta c) {
        this.consulta = c;

        lblHeaderSubtitle.setText(String.format("C-%03d", c.getId()) + " — "
                + (c.getPaciente() != null ? c.getPaciente().getNombre() : "?"));

        String propietario = (c.getPaciente() != null && c.getPaciente().getPropietario() != null)
                ? "  /  " + c.getPaciente().getPropietario().getNombreCompleto() : "";
        lblPaciente.setText(c.getPaciente() != null
                ? c.getPaciente().getNombre() + propietario : "—");

        lblVeterinario.setText(c.getVeterinario() != null
                ? c.getVeterinario().getNombreCompleto() : "—");

        lblFecha.setText(c.getFechaHora() != null
                ? c.getFechaHora().format(FECHA_FMT) : "—");

        txtSintomas.setText(c.getSintomas() != null ? c.getSintomas() : "");
        txtDiagnostico.setText(c.getDiagnostico() != null ? c.getDiagnostico() : "");
        txtTratamiento.setText(c.getTratamiento() != null ? c.getTratamiento() : "");
        NumericFormatter.apply(txtCosto);
        txtCosto.setText(String.valueOf((long) c.getCosto()));
    }

    public void setOnGuardado(Runnable callback) {
        this.onGuardado = callback;
    }

    @FXML
    private void handleGuardar() {
        String sintomas    = txtSintomas.getText().trim();
        String diagnostico = txtDiagnostico.getText().trim();

        if (sintomas.isEmpty() || diagnostico.isEmpty()) {
            mostrarMensaje("Síntomas y diagnóstico son obligatorios.", "#D32F2F");
            return;
        }

        double costo = NumericFormatter.toDouble(txtCosto);

        consulta.setSintomas(sintomas);
        consulta.setDiagnostico(diagnostico);
        consulta.setTratamiento(txtTratamiento.getText().trim());
        consulta.setCosto(costo);

        try {
            new ConsultaService().actualizar(consulta);
            if (onGuardado != null) onGuardado.run();
            cerrar();
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txtSintomas.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}