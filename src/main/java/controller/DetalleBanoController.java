package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.ServicioEstetico;
import ui.NumericFormatter;


public class DetalleBanoController {

    @FXML private Label   lblHeaderPaciente;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblFecha;
    @FXML private Label   lblHora;
    @FXML private Label   lblEstilista;
    @FXML private Label   lblEstado;
    @FXML private Label   lblPrecio;
    @FXML private Label   lblTipoBano;
    @FXML private Label   lblSecado;
    @FXML private Label   lblPerfume;
    @FXML private TextArea taObservaciones;

    
    public void setServicio(ServicioEstetico s) {
        
        String nomPac = (s.getPaciente() != null && s.getPaciente().getNombre() != null)
                ? s.getPaciente().getNombre() : "—";
        lblHeaderPaciente.setText("Paciente: " + nomPac);

        
        lblNombrePac.setText(orDash(s.getPaciente() != null ? s.getPaciente().getNombre() : null));
        lblEspecie.setText(orDash(s.getPaciente() != null ? s.getPaciente().getEspecie() : null));

        
        lblFecha.setText(s.getFechaHora() != null
                ? s.getFechaHora().toLocalDate().toString() : "—");
        lblHora.setText(s.getFechaHora() != null
                ? s.getFechaHora().toLocalTime().toString() : "—");
        lblEstilista.setText(s.getEstilista() != null
                ? s.getEstilista().getNombreCompleto() : "—");

        
        String estado = orDash(s.getEstadoServicio());
        lblEstado.setText(estado);
        String colorEstado;
        switch (estado) {
            case "PROGRAMADO": colorEstado = "#2b87a0"; break;
            case "REALIZADO":  colorEstado = "#1B6B2F"; break;
            case "CANCELADO":  colorEstado = "#C62828"; break;
            default:           colorEstado = "#555555";
        }
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorEstado + ";");

        lblPrecio.setText(s.getPrecio() > 0
                ? NumericFormatter.formatCurrency(s.getPrecio()) : "—");

       
        lblTipoBano.setText(orDash(s.getTipoBano()));
        lblSecado.setText(s.isIncluyeSecado() ? "Sí" : "No");
        lblPerfume.setText(s.isIncluyePerfume() ? "Sí" : "No");

        
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