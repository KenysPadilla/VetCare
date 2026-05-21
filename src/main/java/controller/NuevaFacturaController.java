package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevaFacturaController implements Initializable {

    @FXML private ComboBox<String> cbPropietario;
    @FXML private ComboBox<String> cbPaciente;
    @FXML private ComboBox<String> cbMetodoPago;
    @FXML private TableView<?> tablaDetalles;
    @FXML private TableColumn<?, ?> colConcepto;
    @FXML private TableColumn<?, ?> colTipo;
    @FXML private TableColumn<?, ?> colCantidad;
    @FXML private TableColumn<?, ?> colPrecio;
    @FXML private TableColumn<?, ?> colSubtotal;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPropietario.getItems().addAll("Juan Pérez", "María López", "Carlos Ruiz");
        cbPaciente.getItems().addAll("Max - Perro", "Luna - Gato", "Rocky - Perro");
        cbMetodoPago.getItems().addAll("Efectivo", "Tarjeta", "Transferencia");
    }

    @FXML
    private void handleGenerarFactura() {
        if (cbPropietario.getValue() == null || cbPaciente.getValue() == null
                || cbMetodoPago.getValue() == null) {
            mostrarMensaje("Propietario, paciente y método de pago son obligatorios.", "#D32F2F");
            return;
        }

        mostrarMensaje("Factura generada exitosamente.", "#1B6B2F");
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) lblMensaje.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}