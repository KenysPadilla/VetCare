package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Cirugia;
import ui.NumericFormatter;

public class DetalleCirugiaController {

    @FXML private Label   lblHeaderPaciente;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblPropietario;
    @FXML private Label   lblFechaHora;
    @FXML private Label   lblEstado;
    @FXML private Label   lblVeterinario;
    @FXML private Label   lblTipoCirugia;
    @FXML private Label   lblAnestesia;
    @FXML private Label   lblHoraInicio;
    @FXML private Label   lblHoraFin;
    @FXML private Label   lblDuracion;
    @FXML private Label   lblCosto;
    @FXML private TextArea taDescripcion;
    @FXML private Label   lblResultadoBadge;
    @FXML private Label   lblObsLabel;
    @FXML private javafx.scene.layout.VBox obsPanel;
    @FXML private TextArea taResultado;

    public void setDatos(Cirugia c) {
        String nomPac = (c.getPaciente() != null && c.getPaciente().getNombre() != null)
                        ? c.getPaciente().getNombre() : "—";
        lblHeaderPaciente.setText("Paciente: " + nomPac);
        lblNombrePac.setText(nomPac);

        if (c.getPaciente() != null) {
            lblEspecie.setText(orDash(c.getPaciente().getEspecie()));
            if (c.getPaciente().getPropietario() != null) {
                lblPropietario.setText(c.getPaciente().getPropietario().getNombreCompleto());
            } else {
                lblPropietario.setText("—");
            }
        } else {
            lblEspecie.setText("—");
            lblPropietario.setText("—");
        }

        lblFechaHora.setText(c.getFechaHora() != null ? c.getFechaHora().toString().replace("T", "  ") : "—");

        String est = c.getEstado() != null ? c.getEstado() : "Programada";
        lblEstado.setText(est);
        lblEstado.setStyle(switch (est) {
            case "En Curso"              -> "-fx-text-fill: #e67e22; -fx-font-weight: bold;";
            case "Finalizada","Realizada"-> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
            case "Cancelada"             -> "-fx-text-fill: #e53e3e; -fx-font-weight: bold;";
            default                      -> "-fx-text-fill: #2b87a0; -fx-font-weight: bold;";
        });

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");
        lblHoraInicio.setText(c.getHoraInicio() != null ? c.getHoraInicio().format(fmt) : "—");
        lblHoraFin.setText(c.getHoraFin() != null ? c.getHoraFin().format(fmt) : "—");

        if (c.getVeterinario() != null) {
            lblVeterinario.setText("Dr. " + c.getVeterinario().getNombreCompleto());
        } else {
            lblVeterinario.setText("—");
        }

        lblTipoCirugia.setText(orDash(c.getTipoCirugia()));
        lblAnestesia.setText(orDash(c.getAnestesia()));
        lblDuracion.setText(formatDuracion(c.getDuracion()));
        lblCosto.setText(NumericFormatter.formatCurrency(c.getCosto()));
        taDescripcion.setText(orDash(c.getDescripcion()));
        mostrarResultado(c.getResultado());
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderPaciente.getScene().getWindow()).close();
    }

    private void mostrarResultado(String resultado) {
        if (resultado == null || resultado.isBlank()) {
            lblResultadoBadge.setText("Sin registrar");
            lblResultadoBadge.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14 5 14; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-background-color: #f0f2f4; -fx-text-fill: #6b7f8e;");
            lblObsLabel.setVisible(false);
            lblObsLabel.setManaged(false);
            obsPanel.setVisible(false);
            obsPanel.setManaged(false);
            return;
        }

        String[] partes = resultado.split(" — ", 2);
        String base = partes[0].trim();
        String obs  = partes.length > 1 ? partes[1].trim() : "";

        String baseLower = base.toLowerCase();
        boolean cancelada = baseLower.contains("cancel");
        boolean exitosa   = !cancelada
                            && !baseLower.contains("complic")
                            && !baseLower.contains("fall");

        if (cancelada) {
            lblResultadoBadge.setText("✕  " + base);
            lblResultadoBadge.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14 5 14; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-background-color: #fff0f0; -fx-text-fill: #e53e3e; " +
                "-fx-border-color: #fcc; -fx-border-width: 1;");
        } else if (exitosa) {
            lblResultadoBadge.setText("✓  " + base);
            lblResultadoBadge.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14 5 14; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-background-color: #edf9f2; -fx-text-fill: #27ae60; " +
                "-fx-border-color: #b7eacb; -fx-border-width: 1;");
        } else {
            lblResultadoBadge.setText("△  " + base);
            lblResultadoBadge.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14 5 14; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-background-color: #fff4e8; -fx-text-fill: #e67e22; " +
                "-fx-border-color: #fad7ad; -fx-border-width: 1;");
        }

        boolean tieneObs = !obs.isBlank();
        lblObsLabel.setVisible(tieneObs);
        lblObsLabel.setManaged(tieneObs);
        obsPanel.setVisible(tieneObs);
        obsPanel.setManaged(tieneObs);
        if (tieneObs) taResultado.setText(obs);
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }

    private String formatDuracion(int minutos) {
        if (minutos <= 0) return "—";
        if (minutos < 60) return minutos + "min";
        int h = minutos / 60;
        int m = minutos % 60;
        return h + "h " + String.format("%02d", m) + "min";
    }
}
