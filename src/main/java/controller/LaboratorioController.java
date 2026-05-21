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

public class LaboratorioController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaExamenes;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colPaciente;
    @FXML private TableColumn<?, ?> colTipoExamen;
    @FXML private TableColumn<?, ?> colFechaSolicitud;
    @FXML private TableColumn<?, ?> colFechaResultado;
    @FXML private TableColumn<?, ?> colResultados;
    @FXML private TableColumn<?, ?> colCosto;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoExamen.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Examen de Laboratorio");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistrarResultado() {
        if (tablaExamenes.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un examen para registrar resultado.");
            return;
        }
        System.out.println("Registrar resultado");
    }

    @FXML
    private void handleVerDetalle() {
        if (tablaExamenes.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un examen para ver el detalle.");
            return;
        }
        System.out.println("Ver detalle examen");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}