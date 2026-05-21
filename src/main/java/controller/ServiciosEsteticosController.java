package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiciosEsteticosController implements Initializable {

    @FXML private TableView<?> tablaBanos;
    @FXML private TableView<?> tablaMotiladas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void handleNuevoBano() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoBano.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Servicio de Baño");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelarBano() {
        if (tablaBanos.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un baño para cancelar.");
            return;
        }
        System.out.println("Cancelar baño");
    }

    @FXML
    private void handleCompletarBano() {
        if (tablaBanos.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un baño para completar.");
            return;
        }
        System.out.println("Completar baño");
    }

    @FXML
    private void handleNuevaMotilada() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaMotilada.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Servicio de Motilada");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelarMotilada() {
        if (tablaMotiladas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una motilada para cancelar.");
            return;
        }
        System.out.println("Cancelar motilada");
    }

    @FXML
    private void handleCompletarMotilada() {
        if (tablaMotiladas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una motilada para completar.");
            return;
        }
        System.out.println("Completar motilada");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}