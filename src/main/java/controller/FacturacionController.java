package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FacturacionController implements Initializable {

    @FXML private ComboBox<String> cbPropietario;
    @FXML private ComboBox<String> cbEstadoPago;
    @FXML private TableView<?> tablaFacturas;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colPropietario;
    @FXML private TableColumn<?, ?> colPaciente;
    @FXML private TableColumn<?, ?> colFecha;
    @FXML private TableColumn<?, ?> colSubtotal;
    @FXML private TableColumn<?, ?> colImpuesto;
    @FXML private TableColumn<?, ?> colTotal;
    @FXML private TableColumn<?, ?> colEstado;
    @FXML private TableColumn<?, ?> colMetodoPago;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPropietario.getItems().addAll("Todos", "Juan Pérez", "María López", "Carlos Ruiz");
        cbPropietario.setValue("Todos");
        cbEstadoPago.getItems().addAll("Todos", "Pendiente", "Pagada", "Anulada");
        cbEstadoPago.setValue("Todos");
    }

    @FXML
    private void handleBuscar() {
        System.out.println("Buscando facturas");
    }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaFactura.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nueva Factura");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVerDetalle() {
        if (tablaFacturas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una factura para ver el detalle.");
            return;
        }
        System.out.println("Ver detalle factura");
    }

    @FXML
    private void handleAnular() {
        if (tablaFacturas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una factura para anular.");
            return;
        }
        System.out.println("Anular factura");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
