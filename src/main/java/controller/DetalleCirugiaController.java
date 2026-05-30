package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Cirugia;

public class DetalleCirugiaController {

    @FXML private Label   lblHeaderPaciente;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblPropietario;
    @FXML private Label   lblFechaHora;
    @FXML private Label   lblVeterinario;
    @FXML private Label   lblTipoCirugia;
    @FXML private Label   lblAnestesia;
    @FXML private Label   lblCosto;
    @FXML private TextArea taDescripcion;
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

        if (c.getVeterinario() != null) {
            lblVeterinario.setText("Dr. " + c.getVeterinario().getNombreCompleto());
        } else {
            lblVeterinario.setText("—");
        }

        lblTipoCirugia.setText(orDash(c.getTipoCirugia()));
        lblAnestesia.setText(orDash(c.getAnestesia()));
        lblCosto.setText("$" + String.format("%.2f", c.getCosto()));
        taDescripcion.setText(orDash(c.getDescripcion()));
        taResultado.setText(orDash(c.getResultado()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderPaciente.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
