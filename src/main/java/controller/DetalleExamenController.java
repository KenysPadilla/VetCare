package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.ExamenLab;

import java.time.format.DateTimeFormatter;

public class DetalleExamenController {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label   lblHeaderSub;
    @FXML private Label   lblIdExamen;
    @FXML private Label   lblEstadoBadge;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblPropietario;
    @FXML private Label   lblTipoExamen;
    @FXML private Label   lblFecha;
    @FXML private Label   lblVeterinario;
    @FXML private Label   lblCosto;
    @FXML private TextArea taResultado;
    @FXML private TextArea taObservaciones;

    public void setDatos(ExamenLab e) {
        String nomPac = (e.getPaciente() != null && e.getPaciente().getNombre() != null)
                        ? e.getPaciente().getNombre() : "—";
        lblHeaderSub.setText("Paciente: " + nomPac + "  ·  " + orDash(e.getTipoExamen()));
        lblIdExamen.setText("LAB-" + String.format("%03d", e.getId()));

        boolean tieneResultado = e.getResultado() != null && !e.getResultado().isBlank();
        if (tieneResultado) {
            lblEstadoBadge.setText("Con resultado");
            lblEstadoBadge.setStyle(
                "-fx-background-color: #edf9f2; -fx-text-fill: #27ae60; " +
                "-fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-background-radius: 12; -fx-padding: 4 12 4 12;");
        } else {
            lblEstadoBadge.setText("Pendiente");
            lblEstadoBadge.setStyle(
                "-fx-background-color: #fff8ed; -fx-text-fill: #f5a623; " +
                "-fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-background-radius: 12; -fx-padding: 4 12 4 12;");
        }

        lblNombrePac.setText(nomPac);
        if (e.getPaciente() != null) {
            String especie = orDash(e.getPaciente().getEspecie());
            String raza    = (e.getPaciente().getRaza() != null && !e.getPaciente().getRaza().isBlank())
                             ? " / " + e.getPaciente().getRaza() : "";
            lblEspecie.setText(especie + raza);
            lblPropietario.setText(e.getPaciente().getPropietario() != null
                    ? e.getPaciente().getPropietario().getNombreCompleto() : "—");
        } else {
            lblEspecie.setText("—");
            lblPropietario.setText("—");
        }

        lblTipoExamen.setText(orDash(e.getTipoExamen()));
        lblFecha.setText(e.getFechaHora() != null
                ? e.getFechaHora().toLocalDate().format(FECHA_FMT) : "—");
        lblVeterinario.setText(e.getVeterinario() != null
                ? "Dr. " + e.getVeterinario().getNombreCompleto() : "—");
        lblCosto.setText("$" + String.format("%.2f", e.getCosto()));

        taResultado.setText(tieneResultado ? e.getResultado() : "Sin resultado registrado.");
        taObservaciones.setText(orDash(e.getObservaciones()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblIdExamen.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
