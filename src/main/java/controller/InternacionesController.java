package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InternacionesController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaInternaciones;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colPaciente;
    @FXML private TableColumn<?, ?> colVeterinario;
    @FXML private TableColumn<?, ?> colFechaIngreso;
    @FXML private TableColumn<?, ?> colFechaEgreso;
    @FXML private TableColumn<?, ?> colEstado;
    @FXML private TableColumn<?, ?> colCostoDia;
    @FXML private TableColumn<?, ?> colCostoTotal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void handleBuscar() {
        System.out.println("Buscando: " + txtBuscar.getText());
    }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaInternacion.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nueva Internación");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDarEgreso() {
        if (tablaInternaciones.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una internación para dar egreso.");
            return;
        }
        System.out.println("Dar egreso");
    }

    @FXML
    private void handleVerDetalle() {
        if (tablaInternaciones.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una internación para ver el detalle.");
            return;
        }
        System.out.println("Ver detalle internación");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
