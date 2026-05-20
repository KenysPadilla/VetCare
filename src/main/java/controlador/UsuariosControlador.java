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

public class UsuariosControlador implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaUsuarios;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colUsername;
    @FXML private TableColumn<?, ?> colRol;
    @FXML private TableColumn<?, ?> colActivo;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Usuario");
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
        if (tablaUsuarios.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un usuario para editar.");
            return;
        }
        System.out.println("Editar usuario");
    }

    @FXML
    private void handleDesactivar() {
        if (tablaUsuarios.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione un usuario para desactivar.");
            return;
        }
        System.out.println("Desactivar usuario");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
