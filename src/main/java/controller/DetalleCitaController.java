package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Cita;

import java.time.format.DateTimeFormatter;

public class DetalleCitaController {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA_FMT  = DateTimeFormatter.ofPattern("HH:mm");

    @FXML private Label lblHeaderPaciente;
    @FXML private Label lblId;
    @FXML private Label lblPaciente;
    @FXML private Label lblPropietario;
    @FXML private Label lblEspecie;
    @FXML private Label lblRaza;
    @FXML private Label lblVeterinario;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblTipo;
    @FXML private Label lblEstado;
    @FXML private Label lblMotivo;
    @FXML private Label lblObservaciones;

    public void setCita(Cita c) {
        String nomPac = c.getPaciente() != null ? c.getPaciente().getNombre() : "—";
        lblHeaderPaciente.setText("Paciente: " + nomPac);
        lblId.setText("Cita #" + c.getId());
        lblPaciente.setText(nomPac);

        if (c.getPaciente() != null && c.getPaciente().getPropietario() != null) {
            lblPropietario.setText(c.getPaciente().getPropietario().getNombreCompleto());
        } else {
            lblPropietario.setText("—");
        }

        if (c.getPaciente() != null) {
            lblEspecie.setText(orDash(c.getPaciente().getEspecie()));
            lblRaza.setText(orDash(c.getPaciente().getRaza()));
        } else {
            lblEspecie.setText("—");
            lblRaza.setText("—");
        }

        lblVeterinario.setText(c.getVeterinario() != null
                ? "Dr. " + c.getVeterinario().getNombreCompleto() : "—");

        if (c.getFechaHora() != null) {
            lblFecha.setText(c.getFechaHora().format(FECHA_FMT));
            lblHora.setText(c.getFechaHora().format(HORA_FMT));
        } else {
            lblFecha.setText("—");
            lblHora.setText("—");
        }

        lblTipo.setText(orDash(c.getTipoCita()));
        lblEstado.setText(orDash(c.getEstadoCita()));
        lblMotivo.setText(orDash(c.getMotivo()));
        lblObservaciones.setText(orDash(c.getObservaciones()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderPaciente.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
