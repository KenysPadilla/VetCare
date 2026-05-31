package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.ServicioEstetico;

/**
 * Controlador de la ventana de detalle de un servicio de motilada.
 *
 * <p>GRASP Indireccion: recibe el objeto {@link ServicioEstetico} ya cargado
 * desde {@link ServiciosEsteticosController}; no accede directamente a
 * ningun DAO ni servicio.</p>
 */
public class DetalleMotiladadController {

    @FXML private Label   lblHeaderPaciente;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblFecha;
    @FXML private Label   lblHora;
    @FXML private Label   lblEstilista;
    @FXML private Label   lblEstado;
    @FXML private Label   lblPrecio;
    @FXML private Label   lblEstiloCorte;
    @FXML private Label   lblLargoCorte;
    @FXML private Label   lblUnas;
    @FXML private Label   lblLimpieza;
    @FXML private TextArea taObservaciones;

    /**
     * Rellena todos los controles con los datos del servicio de motilada recibido.
     *
     * @param s servicio estetico de tipo MOTILADA; no debe ser {@code null}
     */
    public void setServicio(ServicioEstetico s) {
        // Header
        String nomPac = (s.getPaciente() != null && s.getPaciente().getNombre() != null)
                ? s.getPaciente().getNombre() : "—";
        lblHeaderPaciente.setText("Paciente: " + nomPac);

        // Paciente
        lblNombrePac.setText(orDash(s.getPaciente() != null ? s.getPaciente().getNombre() : null));
        lblEspecie.setText(orDash(s.getPaciente() != null ? s.getPaciente().getEspecie() : null));

        // Servicio
        lblFecha.setText(s.getFechaHora() != null
                ? s.getFechaHora().toLocalDate().toString() : "—");
        lblHora.setText(s.getFechaHora() != null
                ? s.getFechaHora().toLocalTime().toString() : "—");
        lblEstilista.setText(s.getEstilista() != null
                ? s.getEstilista().getNombreCompleto() : "—");

        // Estado con color
        String estado = orDash(s.getEstadoServicio());
        lblEstado.setText(estado);
        String colorEstado;
        switch (estado) {
            case "PROGRAMADO": colorEstado = "#1976D2"; break;
            case "REALIZADO":  colorEstado = "#1B6B2F"; break;
            case "CANCELADO":  colorEstado = "#C62828"; break;
            default:           colorEstado = "#555555";
        }
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorEstado + ";");

        lblPrecio.setText(s.getPrecio() > 0
                ? String.format("$ %.0f", s.getPrecio()) : "—");

        // Detalles de la motilada
        lblEstiloCorte.setText(orDash(s.getEstiloCorte()));
        lblLargoCorte.setText(orDash(s.getLargoCorte()));
        lblUnas.setText(s.isIncluyeUnas() ? "Sí" : "No");
        lblLimpieza.setText(s.isIncluyeLimpieza() ? "Sí" : "No");

        // Observaciones
        taObservaciones.setText(orDash(s.getObservaciones()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderPaciente.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
