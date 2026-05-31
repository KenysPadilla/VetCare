package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Veterinario;

public class DetalleVeterinarioController {

    @FXML private Label lblHeaderNombre;
    @FXML private Label lblNombreCompleto;
    @FXML private Label lblCedula;
    @FXML private Label lblEspecialidad;
    @FXML private Label lblLicencia;
    @FXML private Label lblTelefono;
    @FXML private Label lblEmail;

    public void setVeterinario(Veterinario v) {
        String nombre = "Dr. " + v.getNombreCompleto();
        lblHeaderNombre.setText(nombre);
        lblNombreCompleto.setText(nombre);
        lblCedula.setText(orDash(v.getCedula()));
        lblEspecialidad.setText(orDash(v.getEspecialidad()));
        lblLicencia.setText(orDash(v.getNumeroLicencia()));
        lblTelefono.setText(orDash(v.getTelefono()));
        lblEmail.setText(orDash(v.getEmail()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderNombre.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}