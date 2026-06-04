package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Medicamento;
import service.MedicamentoService;
import ui.NumericFormatter;

import util.ComboBoxFilter;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class NuevoMedicamentoController implements Initializable {

    @FXML private TextField        txtNombre;
    @FXML private TextField        txtPrincipio;
    @FXML private TextField        txtConcentracion;
    @FXML private ComboBox<String> cbPresentacion;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private TextField        txtDosis;
    @FXML private TextField        txtStock;
    @FXML private TextField        txtPrecio;
    @FXML private DatePicker       dpVencimiento;
    @FXML private Button           btnGuardar;
    @FXML private Label            lblMensaje;

    private Medicamento medicamentoEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ComboBoxFilter.apply(cbPresentacion, Arrays.asList(
                "Tableta", "Jarabe", "Inyectable", "Crema", "Gotas", "Polvo"));
        ComboBoxFilter.apply(cbCategoria, Arrays.asList(
                "Antibiótico", "Analgésico", "Antiinflamatorio", "Corticoide",
                "Antiparasitario", "Gastroprotector", "Diurético", "Anestésico",
                "Tranquilizante", "Antifúngico", "Antiviral", "Vitamina/Suplemento",
                "Antihistamínico", "Cardiotónico", "Otro"));
        NumericFormatter.apply(txtStock);
        NumericFormatter.apply(txtPrecio);
    }

    public void setModoEdicion(Medicamento m) {
        this.medicamentoEnEdicion = m;
        txtNombre.setText(m.getNombre());
        txtPrincipio.setText(m.getDescripcion() != null ? m.getDescripcion() : "");
        txtConcentracion.setText(m.getConcentracion() != null ? m.getConcentracion() : "");
        cbPresentacion.setValue(m.getFabricante());
        cbCategoria.setValue(m.getCategoria());
        txtStock.setText(String.valueOf(m.getStockDisponible()));
        txtPrecio.setText(String.valueOf((long) m.getPrecio()));
        dpVencimiento.setValue(m.getFechaVencimiento());
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        if (txtNombre.getText().trim().isEmpty() || txtPrincipio.getText().trim().isEmpty()
                || cbPresentacion.getValue() == null || txtStock.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()) {
            mostrarMensaje("Nombre, principio activo, presentación, stock y precio son obligatorios.", "#D32F2F");
            return;
        }

        try {
            Medicamento m = new Medicamento();
            m.setNombre(txtNombre.getText().trim());
            m.setDescripcion(txtPrincipio.getText().trim());
            String conc = txtConcentracion.getText().trim();
            m.setConcentracion(conc.isEmpty() ? null : conc);
            m.setFabricante(cbPresentacion.getValue());
            m.setCategoria(cbCategoria.getValue());
            m.setStockDisponible((int) NumericFormatter.toLong(txtStock));
            m.setPrecio(NumericFormatter.toDouble(txtPrecio));
            m.setFechaVencimiento(dpVencimiento.getValue());

            if (medicamentoEnEdicion == null) {
                new MedicamentoService().guardar(m);
                mostrarMensaje("Medicamento guardado exitosamente.", "#1B6B2F");
            } else {
                m.setId(medicamentoEnEdicion.getId());
                new MedicamentoService().actualizar(m);
                mostrarMensaje("Medicamento actualizado exitosamente.", "#1B6B2F");
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
