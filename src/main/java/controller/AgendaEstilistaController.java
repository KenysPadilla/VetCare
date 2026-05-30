package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Estilista;
import model.ServicioEstetico;
import service.ServicioEsteticoService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AgendaEstilistaController {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA_FMT  = DateTimeFormatter.ofPattern("HH:mm");

    @FXML private Label lblNombreEstilista;
    @FXML private TableView<ServicioEstetico> tablaAgenda;
    @FXML private TableColumn<ServicioEstetico, String> colFecha;
    @FXML private TableColumn<ServicioEstetico, String> colHora;
    @FXML private TableColumn<ServicioEstetico, String> colPaciente;
    @FXML private TableColumn<ServicioEstetico, String> colTipoServicio;
    @FXML private TableColumn<ServicioEstetico, String> colEstado;

    private Estilista estilista;

    public void setEstilista(Estilista e) {
        this.estilista = e;
        lblNombreEstilista.setText("Agenda de: " + e.getNombreCompleto());
        configurarColumnas();
        cargarAgenda();
    }

    private void configurarColumnas() {
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHora() != null
                        ? d.getValue().getFechaHora().format(FECHA_FMT) : "—"));
        colHora.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHora() != null
                        ? d.getValue().getFechaHora().format(HORA_FMT) : "—"));
        colPaciente.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPaciente() != null ? d.getValue().getPaciente().getNombre() : "—"));
        colTipoServicio.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTipoServicio() != null ? d.getValue().getTipoServicio() : "—"));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEstadoServicio() != null ? d.getValue().getEstadoServicio() : "—"));
    }

    private void cargarAgenda() {
        try {
            List<ServicioEstetico> todos = new ServicioEsteticoService().listarTodos();
            List<ServicioEstetico> delEstilista = new ArrayList<>();
            for (ServicioEstetico s : todos) {
                if (s.getEstilista() != null
                        && estilista.getCedula().equals(s.getEstilista().getCedula())) {
                    delEstilista.add(s);
                }
            }
            delEstilista.sort(Comparator.comparing(
                    ServicioEstetico::getFechaHora,
                    Comparator.nullsLast(Comparator.reverseOrder())));
            tablaAgenda.setItems(FXCollections.observableArrayList(delEstilista));
        } catch (SQLException ex) {
            tablaAgenda.setPlaceholder(
                    new javafx.scene.control.Label("Error al cargar agenda: " + ex.getMessage()));
        }
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblNombreEstilista.getScene().getWindow()).close();
    }
}

