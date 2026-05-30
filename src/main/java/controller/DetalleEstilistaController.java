package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Estilista;

public class DetalleEstilistaController {

    @FXML private Label lblHeaderNombre;
    @FXML private Label lblNombreCompleto;
    @FXML private Label lblCedula;
    @FXML private Label lblTelefono;
    @FXML private Label lblEmail;
    @FXML private Label lblEspecialidad;

    public void setEstilista(Estilista e) {
        lblHeaderNombre.setText(e.getNombreCompleto());
        lblNombreCompleto.setText(e.getNombreCompleto());
        lblCedula.setText(orDash(e.getCedula()));
        lblTelefono.setText(orDash(e.getTelefono()));
        lblEmail.setText(orDash(e.getEmail()));
        lblEspecialidad.setText(orDash(e.getEspecialidadEstetica()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderNombre.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
