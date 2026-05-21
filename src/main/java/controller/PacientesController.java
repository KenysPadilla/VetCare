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

public class PacientesController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaPacientes;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colNombre;
    @FXML private TableColumn<?, ?> colEspecie;
    @FXML private TableColumn<?, ?> colRaza;
    @FXML private TableColumn<?, ?> colSexo;
    @FXML private TableColumn<?, ?> colPeso;
    @FXML private TableColumn<?, ?> colEdad;
    @FXML private TableColumn<?, ?> colMicrochip;
    @FXML private TableColumn<?, ?> colPropietario;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoPaciente.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Paciente");
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
        if (tablaPacientes.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un paciente para editar.");
            return;
        }
        System.out.println("Editar paciente");
    }

    @FXML
    private void handleEliminar() {
        if (tablaPacientes.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un paciente para eliminar.");
            return;
        }
        System.out.println("Eliminar paciente");
    }

   @FXML
    private void handleHistorial() {
        if (tablaPacientes.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un paciente para ver su historial.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historialClinico.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Historial Clínico");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
