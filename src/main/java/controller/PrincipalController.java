package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    @FXML private Label lblTituloPagina;
    @FXML private Label lblUsuarioActual;
    @FXML private Label lblRolActual;
    @FXML private StackPane contenedorPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblUsuarioActual.setText("Usuario: admin");
        lblRolActual.setText("ADMIN");
    }

    private void cargarVista(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
            Parent vista = loader.load();
            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(vista);
            lblTituloPagina.setText(titulo);
        } catch (Exception e) {
            lblTituloPagina.setText("Error al cargar: " + titulo);
            e.printStackTrace();
        }
    }

    @FXML private void handlePropietarios() { cargarVista("propietarios.fxml", "Gestión de Propietarios"); }
    @FXML private void handlePacientes() { cargarVista("pacientes.fxml", "Gestión de Pacientes"); }
    @FXML private void handleVeterinarios() { cargarVista("veterinarios.fxml", "Gestión de Veterinarios"); }
    @FXML private void handleEstilistas() { cargarVista("estilistas.fxml", "Gestión de Estilistas"); }
    @FXML private void handleCitas() { cargarVista("citas.fxml", "Gestión de Citas Médicas"); }
    @FXML private void handleConsultas() { cargarVista("consultas.fxml", "Gestión de Consultas"); }
    @FXML private void handleVacunaciones() { cargarVista("vacunaciones.fxml", "Control de Vacunaciones"); }
    @FXML private void handleCirugias() { cargarVista("cirugias.fxml", "Gestión de Cirugías"); }
    @FXML private void handleInternaciones() { cargarVista("internaciones.fxml", "Gestión de Internaciones"); }
    @FXML private void handleLaboratorio() { cargarVista("laboratorio.fxml", "Exámenes de Laboratorio"); }
    @FXML private void handleServicios() { cargarVista("serviciosEsteticos.fxml", "Servicios de Baño y Motilada"); }
    @FXML private void handleFacturacion() { cargarVista("facturacion.fxml", "Facturación"); }
    @FXML private void handleUsuarios() { cargarVista("usuarios.fxml", "Gestión de Usuarios"); }
    @FXML private void handleMedicamentos() { cargarVista("medicamentos.fxml", "Gestión de Medicamentos"); }
    
    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stageLogin = new Stage();
            stageLogin.setTitle("VetCare System - Login");
            stageLogin.setScene(new Scene(root));
            stageLogin.show();

            Stage stageActual = (Stage) contenedorPrincipal.getScene().getWindow();
            stageActual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
