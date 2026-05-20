package controlador;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class HistorialClinicoControlador implements Initializable {

    @FXML private Label lblNombre;
    @FXML private Label lblEspecie;
    @FXML private Label lblRaza;
    @FXML private Label lblPropietario;
    @FXML private Label lblMicrochip;
    @FXML private TableView<?> tablaConsultas;
    @FXML private TableView<?> tablaVacunaciones;
    @FXML private TableView<?> tablaCirugias;
    @FXML private TableView<?> tablaInternaciones;
    @FXML private TableView<?> tablaExamenes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblNombre.setText("Max");
        lblEspecie.setText("Perro");
        lblRaza.setText("Golden Retriever");
        lblPropietario.setText("Juan Pérez");
        lblMicrochip.setText("MC-123456");
    }

    public void cargarPaciente(String nombre, String especie, String raza, String propietario, String microchip) {
        lblNombre.setText(nombre);
        lblEspecie.setText(especie);
        lblRaza.setText(raza);
        lblPropietario.setText(propietario);
        lblMicrochip.setText(microchip);
    }
}
