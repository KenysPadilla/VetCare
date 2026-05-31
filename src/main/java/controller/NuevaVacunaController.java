package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Vacuna;
import service.VacunaService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NuevaVacunaController implements Initializable {

    @FXML private Label      lblTitulo;
    @FXML private TextField  txtNombre;
    @FXML private TextField  txtLaboratorio;
    @FXML private TextField  txtLote;
    @FXML private TextField  txtStock;
    @FXML private TextField  txtPrecio;
    @FXML private DatePicker dpVencimiento;
    @FXML private Button     btnGuardar;
    @FXML private Label      lblMensaje;

    private Vacuna vacunaEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // nothing to pre-load
    }

    public void setModoEdicion(Vacuna v) {
        this.vacunaEnEdicion = v;
        lblTitulo.setText("Editar Vacuna");
        txtNombre.setText(v.getNombre());
        txtLaboratorio.setText(v.getLaboratorio() != null ? v.getLaboratorio() : "");
        txtLote.setText(v.getLote() != null ? v.getLote() : "");
        txtStock.setText(String.valueOf(v.getStockDisponible()));
        txtPrecio.setText(String.valueOf(v.getPrecio()));
        dpVencimiento.setValue(v.getFechaVencimiento());
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        String laboratorio = txtLaboratorio.getText().trim();
        if (nombre.isEmpty() || laboratorio.isEmpty()
                || txtStock.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()) {
            mostrarMensaje("Nombre, laboratorio, stock y precio son obligatorios.", "#D32F2F");
            return;
        }

        try {
            Vacuna v = new Vacuna();
            v.setNombre(nombre);
            v.setLaboratorio(laboratorio);
            String lote = txtLote.getText().trim();
            v.setLote(lote.isEmpty() ? null : lote);
            v.setStockDisponible(Integer.parseInt(txtStock.getText().trim()));
            v.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            v.setFechaVencimiento(dpVencimiento.getValue());

            VacunaService svc = new VacunaService();
            if (vacunaEnEdicion == null) {
                svc.guardar(v);
                mostrarMensaje("Vacuna guardada exitosamente.", "#1B6B2F");
            } else {
                v.setId(vacunaEnEdicion.getId());
                svc.actualizar(v);
                mostrarMensaje("Vacuna actualizada exitosamente.", "#1B6B2F");
            }
            ((Stage) lblMensaje.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            mostrarMensaje("Stock debe ser entero y precio debe ser número válido.", "#D32F2F");
        } catch (SQLException e) {
            mostrarMensaje("Error: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        ((Stage) lblMensaje.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}