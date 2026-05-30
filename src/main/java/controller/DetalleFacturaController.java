package controller;

import dao.impl.DetalleFacturaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.DetalleFactura;
import model.Factura;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DetalleFacturaController {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label lblNumeroFactura;
    @FXML private Label lblFecha;
    @FXML private Label lblPropietario;
    @FXML private Label lblPaciente;
    @FXML private Label lblEstado;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;
    @FXML private TableView<DetalleFactura> tablaDetalles;
    @FXML private TableColumn<DetalleFactura, String> colDescripcion;
    @FXML private TableColumn<DetalleFactura, String> colTipo;
    @FXML private TableColumn<DetalleFactura, String> colCantidad;
    @FXML private TableColumn<DetalleFactura, String> colPrecioUnitario;
    @FXML private TableColumn<DetalleFactura, String> colSubtotal;

    public void setFactura(Factura f) {
        lblNumeroFactura.setText("Factura #" + f.getId());
        lblFecha.setText(f.getFechaHora() != null ? f.getFechaHora().format(FECHA_FMT) : "—");
        lblPropietario.setText(f.getPropietario() != null
                ? f.getPropietario().getNombreCompleto() : "—");
        lblPaciente.setText(f.getPaciente() != null ? f.getPaciente().getNombre() : "—");
        lblEstado.setText(f.getEstadoFactura() != null ? f.getEstadoFactura() : "—");

        lblSubtotal.setText(String.format("$%.2f", f.getSubtotal()));
        lblIva.setText(String.format("$%.2f", f.getSubtotal() * Factura.TASA_IVA));
        lblTotal.setText(String.format("$%.2f", f.getTotal()));

        configurarColumnas();
        cargarDetalles(f.getId());
    }

    private void configurarColumnas() {
        colDescripcion.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDescripcion() != null ? d.getValue().getDescripcion() : "—"));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTipoConcepto() != null ? d.getValue().getTipoConcepto() : "—"));
        colCantidad.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getCantidad())));
        colPrecioUnitario.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%.2f", d.getValue().getPrecioUnitario())));
        colSubtotal.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%.2f", d.getValue().getSubtotal())));

        tablaDetalles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tablaDetalles.setRowFactory(tv -> {
            TableRow<DetalleFactura> row = new TableRow<>();
            row.setOnMouseClicked(e -> tablaDetalles.getSelectionModel().clearSelection());
            return row;
        });
        // fixedCellSize = 50px coincide con -fx-cell-size: 50 del CSS
        // el prefHeight se calcula en cargarDetalles() tras conocer la cantidad de filas
        tablaDetalles.setFixedCellSize(50);
    }

    private void cargarDetalles(int idFactura) {
        try {
            ArrayList<DetalleFactura> detalles = new DetalleFacturaDAO().listarPorFactura(idFactura);
            tablaDetalles.setItems(FXCollections.observableArrayList(detalles));
            tablaDetalles.getSelectionModel().clearSelection();
            // 44px header + 50px por fila + 8px de margen para bordes internos
            tablaDetalles.setPrefHeight(52 + detalles.size() * 50);
        } catch (SQLException e) {
            tablaDetalles.setPlaceholder(new Label("Error al cargar detalles: " + e.getMessage()));
            tablaDetalles.setPrefHeight(94);
        }
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblNumeroFactura.getScene().getWindow()).close();
    }
}