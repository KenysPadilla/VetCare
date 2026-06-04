package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Medicamento;
import ui.NumericFormatter;

import java.time.LocalDate;


public class DetalleMedicamentoController {

    @FXML private Label lblHeaderNombre;
    @FXML private Label lblNombre;
    @FXML private Label lblFabricante;
    @FXML private Label lblConcentracion;
    @FXML private Label lblPrecio;
    @FXML private Label lblStock;
    @FXML private Label lblVencimiento;
    @FXML private Label lblEstado;
    @FXML private TextArea taDescripcion;

    
    public void setMedicamento(Medicamento m) {
        lblHeaderNombre.setText(m.getNombre() != null ? m.getNombre() : "—");

        lblNombre.setText(orDash(m.getNombre()));
        lblFabricante.setText(orDash(m.getFabricante()));
        lblConcentracion.setText(orDash(m.getConcentracion()));
        lblPrecio.setText(m.getPrecio() > 0
                ? NumericFormatter.formatCurrency(m.getPrecio()) : "—");

        lblStock.setText(m.getStockDisponible() + " unidades");

        
        LocalDate venc = m.getFechaVencimiento();
        lblVencimiento.setText(venc != null ? venc.toString() : "—");

        
        String estado;
        String color;
        if (venc != null && venc.isBefore(LocalDate.now())) {
            estado = "Vencido";
            color  = "#D32F2F";
        } else if (m.getStockDisponible() == 0) {
            estado = "Agotado";
            color  = "#757575";
        } else {
            estado = "Disponible";
            color  = "#1B6B2F";
        }
        lblEstado.setText(estado);
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");

        taDescripcion.setText(orDash(m.getDescripcion()));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderNombre.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}