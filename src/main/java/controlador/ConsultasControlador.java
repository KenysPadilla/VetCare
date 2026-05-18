package controlador;

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

public class ConsultasControlador implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaConsultas;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colFechaHora;
    @FXML private TableColumn<?, ?> colPaciente;
    @FXML private TableColumn<?, ?> colVeterinario;
    @FXML private TableColumn<?, ?> colSintomas;
    @FXML private TableColumn<?, ?> colDiagnostico;
    @FXML private TableColumn<?, ?> colTratamiento;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaConsulta.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nueva Consulta");
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
        if (tablaConsultas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una consulta para ver el detalle.");
            return;
        }
        System.out.println("Ver detalle consulta");
    }

    @FXML
    private void handlePrescripcion() {
        if (tablaConsultas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una consulta para generar prescripción.");
            return;
        }
        System.out.println("Generar prescripción");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
