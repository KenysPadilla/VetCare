package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Cirugia;
import model.Consulta;
import model.ExamenLab;
import model.Internacion;
import model.Paciente;
import model.Vacunacion;
import service.CirugiaService;
import service.ConsultaService;
import service.ExamenLabService;
import service.InternacionService;
import service.VacunacionService;
import ui.NumericFormatter;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HistorialClinicoController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label lblHeaderPaciente;
    @FXML private Label lblNombre;
    @FXML private Label lblEspecie;
    @FXML private Label lblRaza;
    @FXML private Label lblPropietario;
    @FXML private Label lblMicrochip;

    @FXML private TableView<Consulta> tablaConsultas;
    @FXML private TableColumn<Consulta, String> colConFecha;
    @FXML private TableColumn<Consulta, String> colConSintomas;
    @FXML private TableColumn<Consulta, String> colConDiagnostico;
    @FXML private TableColumn<Consulta, String> colConTratamiento;
    @FXML private TableColumn<Consulta, String> colConVeterinario;

    @FXML private TableView<Vacunacion> tablaVacunaciones;
    @FXML private TableColumn<Vacunacion, String> colVacVacuna;
    @FXML private TableColumn<Vacunacion, String> colVacFecha;
    @FXML private TableColumn<Vacunacion, String> colVacProxima;
    @FXML private TableColumn<Vacunacion, String> colVacVeterinario;
    @FXML private TableColumn<Vacunacion, String> colVacObservaciones;

    @FXML private TableView<Cirugia> tablaCirugias;
    @FXML private TableColumn<Cirugia, String> colCirTipo;
    @FXML private TableColumn<Cirugia, String> colCirFecha;
    @FXML private TableColumn<Cirugia, String> colCirAnestesia;
    @FXML private TableColumn<Cirugia, String> colCirResultado;
    @FXML private TableColumn<Cirugia, String> colCirVeterinario;
    @FXML private TableColumn<Cirugia, String> colCirCosto;

    @FXML private TableView<Internacion> tablaInternaciones;
    @FXML private TableColumn<Internacion, String> colIntIngreso;
    @FXML private TableColumn<Internacion, String> colIntEgreso;
    @FXML private TableColumn<Internacion, String> colIntEstado;
    @FXML private TableColumn<Internacion, String> colIntVeterinario;
    @FXML private TableColumn<Internacion, String> colIntCostoDia;
    @FXML private TableColumn<Internacion, String> colIntCostoTotal;

    @FXML private TableView<ExamenLab> tablaExamenes;
    @FXML private TableColumn<ExamenLab, String> colExaTipo;
    @FXML private TableColumn<ExamenLab, String> colExaSolicitud;
    @FXML private TableColumn<ExamenLab, String> colExaResultado;
    @FXML private TableColumn<ExamenLab, String> colExaResultados;
    @FXML private TableColumn<ExamenLab, String> colExaCosto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
    }

    private void configurarColumnas() {
        colConFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHora() != null ? d.getValue().getFechaHora().format(FECHA_FMT) : "—"));
        colConSintomas.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getSintomas() != null ? d.getValue().getSintomas() : "—"));
        colConDiagnostico.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDiagnostico() != null ? d.getValue().getDiagnostico() : "—"));
        colConTratamiento.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTratamiento() != null ? d.getValue().getTratamiento() : "—"));
        colConVeterinario.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getVeterinario() != null ? d.getValue().getVeterinario().getNombreCompleto() : "—"));

        colVacVacuna.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getVacuna() != null ? d.getValue().getVacuna().getNombre() : "—"));
        colVacFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHoraAplicacion() != null
                        ? d.getValue().getFechaHoraAplicacion().format(FECHA_FMT) : "—"));
        colVacProxima.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaProxima() != null ? d.getValue().getFechaProxima().format(FECHA_FMT) : "—"));
        colVacVeterinario.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getVeterinario() != null ? d.getValue().getVeterinario().getNombreCompleto() : "—"));
        colVacObservaciones.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getObservaciones() != null ? d.getValue().getObservaciones() : "—"));

        colCirTipo.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTipoCirugia() != null ? d.getValue().getTipoCirugia() : "—"));
        colCirFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHora() != null ? d.getValue().getFechaHora().format(FECHA_FMT) : "—"));
        colCirAnestesia.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAnestesia() != null ? d.getValue().getAnestesia() : "—"));
        colCirResultado.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getResultado() != null ? d.getValue().getResultado() : "—"));
        colCirVeterinario.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getVeterinario() != null ? d.getValue().getVeterinario().getNombreCompleto() : "—"));
        colCirCosto.setCellValueFactory(d -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(d.getValue().getCosto())));

        colIntIngreso.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHoraIngreso() != null
                        ? d.getValue().getFechaHoraIngreso().format(FECHA_FMT) : "—"));
        colIntEgreso.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHoraEgreso() != null
                        ? d.getValue().getFechaHoraEgreso().format(FECHA_FMT) : "—"));
        colIntEstado.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHoraEgreso() == null ? "Internado" : "Egresado"));
        colIntVeterinario.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getVeterinario() != null ? d.getValue().getVeterinario().getNombreCompleto() : "—"));
        colIntCostoDia.setCellValueFactory(d -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(d.getValue().getCostoDia())));
        colIntCostoTotal.setCellValueFactory(d -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(d.getValue().calcularCostoTotal())));

        colExaTipo.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTipoExamen() != null ? d.getValue().getTipoExamen() : "—"));
        colExaSolicitud.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHora() != null ? d.getValue().getFechaHora().format(FECHA_FMT) : "—"));
        colExaResultado.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getResultado() != null && !d.getValue().getResultado().isBlank() ? "Sí" : "Pendiente"));
        colExaResultados.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getResultado() != null ? d.getValue().getResultado() : "—"));
        colExaCosto.setCellValueFactory(d -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(d.getValue().getCosto())));
    }

    public void setPaciente(Paciente p) {
        lblHeaderPaciente.setText(p.getNombre() + " — " + (p.getEspecie() != null ? p.getEspecie() : ""));
        lblNombre.setText(p.getNombre());
        lblEspecie.setText(p.getEspecie() != null ? p.getEspecie() : "—");
        lblRaza.setText(p.getRaza() != null ? p.getRaza() : "—");
        lblPropietario.setText(p.getPropietario() != null ? p.getPropietario().getNombreCompleto() : "—");
        lblMicrochip.setText(p.getMicrochip() != null && !p.getMicrochip().isBlank() ? p.getMicrochip() : "—");

        try {
            tablaConsultas.setItems(FXCollections.observableArrayList(
                    new ConsultaService().listarPorPaciente(p.getId())));
            tablaVacunaciones.setItems(FXCollections.observableArrayList(
                    new VacunacionService().listarPorPaciente(p.getId())));
            tablaCirugias.setItems(FXCollections.observableArrayList(
                    new CirugiaService().listarPorPaciente(p.getId())));
            tablaInternaciones.setItems(FXCollections.observableArrayList(
                    new InternacionService().listarPorPaciente(p.getId())));
            tablaExamenes.setItems(FXCollections.observableArrayList(
                    new ExamenLabService().listarPorPaciente(p.getId())));
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al cargar historial: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
