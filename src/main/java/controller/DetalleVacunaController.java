package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Vacuna;
import ui.NumericFormatter;

import java.time.LocalDate;


public class DetalleVacunaController {

    @FXML private Label lblHeaderNombre;
    @FXML private Label lblNombre;
    @FXML private Label lblLaboratorio;
    @FXML private Label lblLote;
    @FXML private Label lblStock;
    @FXML private Label lblPrecio;
    @FXML private Label lblVencimiento;
    @FXML private Label lblEstado;

   
    public void setVacuna(Vacuna v) {
        lblHeaderNombre.setText(orDash(v.getNombre()));

        lblNombre.setText(orDash(v.getNombre()));
        lblLaboratorio.setText(orDash(v.getLaboratorio()));
        lblLote.setText(orDash(v.getLote()));
        lblStock.setText(v.getStockDisponible() + " unidades");
        lblPrecio.setText(v.getPrecio() > 0
                ? NumericFormatter.formatCurrency(v.getPrecio()) : "—");

        LocalDate venc = v.getFechaVencimiento();
        lblVencimiento.setText(venc != null ? venc.toString() : "—");

        
        String estado;
        String color;
        if (venc != null && venc.isBefore(LocalDate.now())) {
            estado = "Vencida";
            color  = "#D32F2F";
        } else if (v.getStockDisponible() == 0) {
            estado = "Agotada";
            color  = "#757575";
        } else {
            estado = "Disponible";
            color  = "#1B6B2F";
        }
        lblEstado.setText(estado);
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderNombre.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}