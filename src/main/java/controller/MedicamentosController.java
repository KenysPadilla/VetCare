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

public class MedicamentosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaMedicamentos;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colNombre;
    @FXML private TableColumn<?, ?> colPrincipio;
    @FXML private TableColumn<?, ?> colPresentacion;
    @FXML private TableColumn<?, ?> colStock;
    @FXML private TableColumn<?, ?> colPrecio;
    @FXML private TableColumn<?, ?> colVencimiento;
    @FXML private TableColumn<?, ?> colEstado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void handleBuscar() {
        System.out.println("Buscando: " + txtBuscar.getText());
    }

    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoMedicamento.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Medicamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditar() {
        if (tablaMedicamentos.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un medicamento para editar.");
            return;
        }
        System.out.println("Editar medicamento");
    }

    @FXML
    private void handleEliminar() {
        if (tablaMedicamentos.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un medicamento para eliminar.");
            return;
        }
        System.out.println("Eliminar medicamento");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
