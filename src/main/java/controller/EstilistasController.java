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

public class EstilistasController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaEstilistas;
    @FXML private TableColumn<?, ?> colCedula;
    @FXML private TableColumn<?, ?> colNombre;
    @FXML private TableColumn<?, ?> colApellido;
    @FXML private TableColumn<?, ?> colEspecialidad;
    @FXML private TableColumn<?, ?> colTelefono;
    @FXML private TableColumn<?, ?> colCorreo;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoEstilista.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Estilista");
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
        if (tablaEstilistas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un estilista para editar.");
            return;
        }
        System.out.println("Editar estilista");
    }

    @FXML
    private void handleEliminar() {
        if (tablaEstilistas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un estilista para eliminar.");
            return;
        }
        System.out.println("Eliminar estilista");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
