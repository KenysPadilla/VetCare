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

public class PropietariosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaPropietarios;
    @FXML private TableColumn<?, ?> colCedula;
    @FXML private TableColumn<?, ?> colNombre;
    @FXML private TableColumn<?, ?> colApellido;
    @FXML private TableColumn<?, ?> colTelefono;
    @FXML private TableColumn<?, ?> colCorreo;
    @FXML private TableColumn<?, ?> colDireccion;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoPropietario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Propietario");
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
        if (tablaPropietarios.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un propietario para editar.");
            return;
        }
        System.out.println("Editar propietario");
    }

    @FXML
    private void handleEliminar() {
        if (tablaPropietarios.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un propietario para eliminar.");
            return;
        }
        System.out.println("Eliminar propietario");
    }

    @FXML
    private void handleVerMascotas() {
        if (tablaPropietarios.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un propietario para ver sus mascotas.");
            return;
        }
        System.out.println("Ver mascotas del propietario");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
