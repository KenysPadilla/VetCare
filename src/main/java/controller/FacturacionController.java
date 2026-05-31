package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Factura;
import service.FacturaService;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class FacturacionController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbPropietario;
    @FXML private ComboBox<String> cbEstadoPago;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatPendientes;
    @FXML private Label lblStatPagadas;
    @FXML private Label lblStatIngresos;
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, String> colId;
    @FXML private TableColumn<Factura, String> colPropietario;
    @FXML private TableColumn<Factura, Factura> colPaciente;
    @FXML private TableColumn<Factura, String> colFecha;
    @FXML private TableColumn<Factura, String> colSubtotal;
    @FXML private TableColumn<Factura, String> colImpuesto;
    @FXML private TableColumn<Factura, String> colTotal;
    @FXML private TableColumn<Factura, String> colEstado;
    @FXML private TableColumn<Factura, String> colMetodoPago;
    @FXML private TableColumn<Factura, Factura> colAcciones;
    @FXML private TableColumn<Factura, Factura> colOperaciones;

    private final FacturaService service = new FacturaService();
    private final List<Factura> todasLasFacturas = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEstadoPago.getItems().addAll("Todos", "PENDIENTE", "PAGADA", "ANULADA");
        cbEstadoPago.getSelectionModel().selectFirst();
        cbEstadoPago.setOnAction(e -> aplicarFiltros());
        cbPropietario.setOnAction(e -> aplicarFiltros());
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("F-%03d", data.getValue().getId())));
        colId.setCellFactory(col -> new TableCell<>() {
            { selectedProperty().addListener((obs, was, now) -> applyIdStyle()); }
            private void applyIdStyle() {
                if (isEmpty() || getItem() == null) { setStyle(""); return; }
                setStyle("-fx-font-family: 'Consolas'; -fx-font-weight: bold; -fx-text-fill: "
                        + (isSelected() ? "#1a2e3b" : "#2b87a0") + ";");
            }
            @Override
            protected void updateItem(String id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : id);
                applyIdStyle();
            }
        });

        colPropietario.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPropietario() != null
                        ? data.getValue().getPropietario().getNombreCompleto() : "—"));

        colPaciente.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colPaciente.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Factura f, boolean empty) {
                super.updateItem(f, empty);
                if (empty || f == null) {
                    setGraphic(null);
                    return;
                }
                String pet = f.getPaciente() != null ? f.getPaciente().getNombre() : "—";
                Label l = new Label(pet);
                l.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
                setGraphic(l);
            }
        });

        colFecha.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHora() != null
                        ? data.getValue().getFechaHora().format(FECHA_FMT) : "—"));

        colSubtotal.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("$%.2f", data.getValue().getSubtotal())));
        colImpuesto.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("$%.2f", data.getValue().getSubtotal() * Factura.TASA_IVA)));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("$%.2f", data.getValue().getTotal())));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : t);
                setStyle(empty || t == null ? "" : "-fx-font-weight: bold; -fx-text-fill: #27ae60;");
            }
        });

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(
                etiquetaEstado(data.getValue().getEstadoFactura())));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(estado);
                badge.getStyleClass().add(claseEstado(estado));
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        colMetodoPago.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMetodoPago() != null ? data.getValue().getMetodoPago() : "—"));

        colAcciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = crearChip("Ver", "action-chip action-chip-ver");
            {
                btnVer.setOnAction(e -> {
                    Factura f = getTableRow().getItem();
                    if (f != null) verDetalle(f);
                });
            }
            @Override
            protected void updateItem(Factura item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : btnVer);
            }
        });

        colOperaciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colOperaciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnPagar = crearChip("Pagar", "action-chip action-chip-editar");
            private final Button btnAnular = crearChip("Anular", "action-chip action-chip-eliminar");
            private final HBox ops = new HBox(6, btnPagar, btnAnular);
            {
                ops.setAlignment(Pos.CENTER);
                ops.setMaxWidth(Double.MAX_VALUE);
                btnPagar.setOnAction(e -> {
                    Factura f = getTableRow().getItem();
                    if (f != null) marcarPagada(f);
                });
                btnAnular.setOnAction(e -> {
                    Factura f = getTableRow().getItem();
                    if (f != null) anular(f);
                });
            }
            @Override
            protected void updateItem(Factura item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                setGraphic("PENDIENTE".equals(item.getEstadoFactura()) ? ops : null);
            }
        });
    }

    private Button crearChip(String texto, String cssClass) {
        Button btn = new Button(texto);
        String inlineStyle = ui.StyleManager.chipStyle(cssClass);
        if (inlineStyle != null) {
            btn.setStyle(inlineStyle);
        } else {
            btn.getStyleClass().add(cssClass);
        }
        btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        return btn;
    }

    private String etiquetaEstado(String estado) {
        if (estado == null) {
            return "—";
        }
        return switch (estado) {
            case "PENDIENTE" -> "Pendiente";
            case "PAGADA" -> "Pagada";
            case "ANULADA" -> "Anulada";
            default -> estado;
        };
    }

    private String claseEstado(String etiqueta) {
        return switch (etiqueta) {
            case "Pagada" -> "badge-confirmado";
            case "Anulada" -> "badge-critico";
            default -> "badge-pendiente";
        };
    }

    private void cargarDatos() {
        try {
            todasLasFacturas.clear();
            todasLasFacturas.addAll(service.listarTodos());
            actualizarComboPropietarios();
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar facturas: " + e.getMessage());
        }
    }

    private void actualizarComboPropietarios() {
        String actual = cbPropietario.getValue();
        Set<String> nombres = new LinkedHashSet<>();
        nombres.add("Todos");
        for (Factura f : todasLasFacturas) {
            if (f.getPropietario() != null) {
                nombres.add(f.getPropietario().getNombreCompleto());
            }
        }
        cbPropietario.getItems().setAll(nombres);
        cbPropietario.setValue(nombres.contains(actual) ? actual : "Todos");
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String prop = cbPropietario.getValue();
        String estado = cbEstadoPago.getValue();

        List<Factura> filtradas = new ArrayList<>();
        for (Factura f : todasLasFacturas) {
            if (prop != null && !"Todos".equals(prop)) {
                String nombre = f.getPropietario() != null ? f.getPropietario().getNombreCompleto() : "";
                if (!prop.equals(nombre)) {
                    continue;
                }
            }
            if (estado != null && !"Todos".equals(estado) && !estado.equals(f.getEstadoFactura())) {
                continue;
            }
            if (!texto.isEmpty()) {
                String busqueda = (
                        (f.getPropietario() != null ? f.getPropietario().getNombreCompleto() : "") + " "
                                + (f.getPaciente() != null ? f.getPaciente().getNombre() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtradas.add(f);
        }

        tablaFacturas.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int pendientes = 0;
        int pagadas = 0;
        double ingresos = 0;
        for (Factura f : todasLasFacturas) {
            if ("PENDIENTE".equals(f.getEstadoFactura())) {
                pendientes++;
            } else if ("PAGADA".equals(f.getEstadoFactura())) {
                pagadas++;
                ingresos += f.getTotal();
            }
        }
        lblStatTotal.setText(String.valueOf(todasLasFacturas.size()));
        lblStatPendientes.setText(String.valueOf(pendientes));
        lblStatPagadas.setText(String.valueOf(pagadas));
        lblStatIngresos.setText(String.format("$%,.0f", ingresos));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaFactura.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Factura");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verDetalle(Factura sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleFactura.fxml"));
            Parent root = loader.load();
            DetalleFacturaController ctrl = loader.getController();
            ctrl.setFactura(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle — Factura #" + sel.getId());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void marcarPagada(Factura sel) {
        if (!"PENDIENTE".equals(sel.getEstadoFactura())) {
            mostrarAlerta("Solo las facturas pendientes pueden marcarse como pagadas.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Pago");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Confirmar pago de Factura #" + sel.getId()
                + " por $" + String.format("%.2f", sel.getTotal()) + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                service.actualizarEstado(sel.getId(), "PAGADA");
                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error: " + e.getMessage());
            }
        }
    }

    private void anular(Factura sel) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Anulación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Anular la Factura #" + sel.getId() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                service.anular(sel.getId());
                cargarDatos();
            } catch (IllegalStateException e) {
                mostrarAlerta(e.getMessage());
            } catch (SQLException e) {
                mostrarAlerta("Error: " + e.getMessage());
            }
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