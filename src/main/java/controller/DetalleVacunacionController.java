package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Vacunacion;
import ui.NumericFormatter;

public class DetalleVacunacionController {

    @FXML private Label   lblHeaderPaciente;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblPropietario;
    @FXML private Label   lblVacuna;
    @FXML private Label   lblLaboratorio;
    @FXML private Label   lblPrecio;
    @FXML private Label   lblVeterinario;
    @FXML private Label   lblFechaAplicacion;
    @FXML private Label   lblFechaProxima;
    @FXML private Label   lblEstado;
    @FXML private TextArea taObservaciones;

    public void setDatos(Vacunacion v) {
        String nomPac = (v.getPaciente() != null && v.getPaciente().getNombre() != null)
                        ? v.getPaciente().getNombre() : "—";
        lblHeaderPaciente.setText("Paciente: " + nomPac);
        lblNombrePac.setText(nomPac);

        if (v.getPaciente() != null) {
            lblEspecie.setText(orDash(v.getPaciente().getEspecie()));
            if (v.getPaciente().getPropietario() != null) {
                lblPropietario.setText(v.getPaciente().getPropietario().getNombreCompleto());
            } else {
                lblPropietario.setText("—");
            }
        } else {
            lblEspecie.setText("—");
            lblPropietario.setText("—");
        }

        if (v.getVacuna() != null) {
            lblVacuna.setText(orDash(v.getVacuna().getNombre()));
            lblLaboratorio.setText(orDash(v.getVacuna().getLaboratorio()));
            lblPrecio.setText(NumericFormatter.formatCurrency(v.getVacuna().getPrecio()));
        } else {
            lblVacuna.setText("—");
            lblLaboratorio.setText("—");
            lblPrecio.setText("—");
        }

        if (v.getVeterinario() != null) {
            lblVeterinario.setText("Dr. " + v.getVeterinario().getNombreCompleto());
        } else {
            lblVeterinario.setText("—");
        }

        lblFechaAplicacion.setText(v.getFechaHoraAplicacion() != null
                ? v.getFechaHoraAplicacion().toString().replace("T", "  ") : "—");
        lblFechaProxima.setText(v.getFechaProxima() != null
                ? v.getFechaProxima().toString() : "No programada");

        String estadoDisplay = VacunacionesController.calcularEstadoDisplay(v);
        lblEstado.setText(estadoDisplay);
        String color = switch (estadoDisplay) {
            case "Aplicada"   -> "#27ae60";
            case "Pendiente"  -> "#e67e22";
            case "Programada" -> "#2b87a0";
            default           -> "#6b7f8e";
        };
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");

        taObservaciones.setText(orDash(v.getObservaciones()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderPaciente.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
