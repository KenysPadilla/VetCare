package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Consulta;
import ui.NumericFormatter;

public class DetalleConsultaController {

    @FXML private Label   lblHeaderPaciente;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblPropietario;
    @FXML private Label   lblFechaHora;
    @FXML private Label   lblVeterinario;
    @FXML private Label   lblEspecialidad;
    @FXML private Label   lblCosto;
    @FXML private TextArea taSintomas;
    @FXML private TextArea taDiagnostico;
    @FXML private TextArea taTratamiento;

    public void setDatos(Consulta c) {
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

        if (c.getVeterinario() != null) {
            lblVeterinario.setText("Dr. " + c.getVeterinario().getNombreCompleto());
            lblEspecialidad.setText(orDash(c.getVeterinario().getEspecialidad()));
        } else {
            lblVeterinario.setText("—");
            lblEspecialidad.setText("—");
        }

        lblCosto.setText(NumericFormatter.formatCurrency(c.getCosto()));
        taSintomas.setText(orDash(c.getSintomas()));
        taDiagnostico.setText(orDash(c.getDiagnostico()));
        taTratamiento.setText(orDash(c.getTratamiento()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderPaciente.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
