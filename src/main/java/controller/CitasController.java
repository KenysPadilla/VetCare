package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CitasController implements Initializable {

    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbVeterinario;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<?> tablaCitas;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colFechaHora;
    @FXML private TableColumn<?, ?> colPaciente;
    @FXML private TableColumn<?, ?> colPropietario;
    @FXML private TableColumn<?, ?> colVeterinario;
    @FXML private TableColumn<?, ?> colTipo;
    @FXML private TableColumn<?, ?> colMotivo;
    @FXML private TableColumn<?, ?> colEstado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEstado.getItems().addAll("Todos", "Programada", "En curso", "Completada", "Cancelada");
        cbEstado.setValue("Todos");

        cbVeterinario.getItems().addAll("Todos", "Dr. Pérez", "Dra. López", "Dr. Martínez");
        cbVeterinario.setValue("Todos");
    }

    @FXML
    private void handleBuscar() {
        System.out.println("Buscando citas con filtros: "
                + "Fecha=" + dpFecha.getValue()
                + " Vet=" + cbVeterinario.getValue()
                + " Estado=" + cbEstado.getValue());   
    }

    @FXML
    private void handleNuevaCita() {
       try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaCita.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nueva Cita Médica");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModificar() {
         if (tablaCitas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una cita", "Debe seleccionar una cita de la tabla para modificarla.");
            return;
        }
        System.out.println("Modificar cita seleccionada");
    }

    @FXML
    private void handleCancelarCita() {
         if (tablaCitas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una cita", "Debe seleccionar una cita de la tabla para cancelarla.");
            return;
        }
        System.out.println("Cancelar cita seleccionada");
        
    }

    @FXML
    private void handleVerHistorial() {
        if (tablaCitas.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Seleccione una cita", "Debe seleccionar una cita de la tabla para ver el historial.");
            return;
        }
        System.out.println("Ver historial del paciente");
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
