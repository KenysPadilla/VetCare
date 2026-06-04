package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.SolicitudCita;

import java.time.format.DateTimeFormatter;

public class DetalleSolicitudController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label lblHeaderSub;
    @FXML private Label lblNombre;
    @FXML private Label lblTelefono;
    @FXML private Label lblCorreo;
    @FXML private Label lblMascota;
    @FXML private Label lblEspecie;
    @FXML private Label lblRaza;
    @FXML private Label lblMotivo;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblRecibida;

    public void setSolicitud(SolicitudCita s) {
        lblHeaderSub.setText(nvl(s.getNombrePropietario()));
        lblNombre.setText(nvl(s.getNombrePropietario()));
        lblTelefono.setText(nvl(s.getTelefono()));
        lblCorreo.setText(nvl(s.getCorreo()));
        lblMascota.setText(nvl(s.getNombreMascota()));
        lblEspecie.setText(nvl(s.getEspecie()));
        lblRaza.setText(nvl(s.getRaza()));
        lblMotivo.setText(nvl(s.getMotivo()));
        lblFecha.setText(s.getFecha() != null ? s.getFecha().format(FMT) : "—");
        lblHora.setText(nvl(s.getHora()));
        lblRecibida.setText(s.getFechaSolicitud() != null
                ? s.getFechaSolicitud().toLocalDate().format(FMT) : "—");
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblNombre.getScene().getWindow()).close();
    }

    private String nvl(String v) { return v != null && !v.isBlank() ? v : "—"; }
}
