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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Factura;
import org.kordamp.ikonli.javafx.FontIcon;
import service.FacturaService;
import ui.ConfirmDialog;
import ui.NumericFormatter;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
    @FXML private HBox contenedorBarras;
    @FXML private Label lblEje1;
    @FXML private Label lblEje15;
    @FXML private Label lblEjeFin;
    @FXML private FontIcon iconTendencia;
    @FXML private Label lblPorcentaje;
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
                NumericFormatter.formatCurrency(data.getValue().getSubtotal())));
        colImpuesto.setCellValueFactory(data -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(data.getValue().getSubtotal() * Factura.TASA_IVA)));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(data.getValue().getTotal())));
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
            ui.StyleManager.applyHover(btn, cssClass);
        } else {
            btn.getStyleClass().add(cssClass);
        }
        btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        return btn;
    }

    private String etiquetaEstado(String estado) {
        if (estado == null) return "—";
        return switch (estado) {
            case "PENDIENTE" -> "Pendiente";
            case "PAGADA"    -> "Pagada";
            case "ANULADA"   -> "Anulada";
            default          -> estado;
        };
    }

    private String claseEstado(String etiqueta) {
        return switch (etiqueta) {
            case "Pagada"  -> "badge-confirmado";
            case "Anulada" -> "badge-critico";
            default        -> "badge-pendiente";
        };
    }

    private void cargarDatos() {
        try {
            todasLasFacturas.clear();
            todasLasFacturas.addAll(service.listarTodos());
            actualizarComboPropietarios();
            aplicarFiltros();
            cargarGraficaIngresos();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar facturas: " + e.getMessage());
        }
    }

    private void cargarGraficaIngresos() {
        YearMonth mesActual  = YearMonth.now();
        YearMonth mesAnterior = mesActual.minusMonths(1);
        int diasEnMes = mesActual.lengthOfMonth();
        int mesNum    = mesActual.getMonthValue();
        int anioNum   = mesActual.getYear();
        String mesCorto = mesActual.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        double[] ingresosPorDia = new double[diasEnMes + 1];
        double totalMesAnterior = 0;

        for (Factura f : todasLasFacturas) {
            if (!"PAGADA".equals(f.getEstadoFactura()) || f.getFechaHora() == null) continue;
            LocalDate fecha = f.getFechaHora().toLocalDate();
            if (fecha.getYear() == anioNum && fecha.getMonthValue() == mesNum) {
                ingresosPorDia[fecha.getDayOfMonth()] += f.getTotal();
            } else if (fecha.getYear() == mesAnterior.getYear()
                    && fecha.getMonthValue() == mesAnterior.getMonthValue()) {
                totalMesAnterior += f.getTotal();
            }
        }

        double maxDia = 0;
        int diaMax = 1;
        for (int d = 1; d <= diasEnMes; d++) {
            if (ingresosPorDia[d] > maxDia) { maxDia = ingresosPorDia[d]; diaMax = d; }
        }

        contenedorBarras.getChildren().clear();
        final double ALTURA_MAX = 80.0;
        int diaHoy = LocalDate.now().getDayOfMonth();
        for (int d = 1; d <= diasEnMes; d++) {
            Region barra = new Region();
            HBox.setHgrow(barra, Priority.ALWAYS);
            double altura = (maxDia > 0) ? (ingresosPorDia[d] / maxDia) * ALTURA_MAX : 2.0;
            double alturaFinal = Math.max(altura, 2.0);
            barra.setPrefHeight(alturaFinal);
            barra.setMaxHeight(alturaFinal);
            String color = (d == diaHoy) ? "#2b87a0" : "#e8f4f8";
            barra.setStyle("-fx-background-color: " + color
                    + "; -fx-background-radius: 3 3 0 0; -fx-background-insets: 0;");
            contenedorBarras.getChildren().add(barra);
        }

        lblEje1.setText("1 " + mesCorto);
        lblEje15.setText("15 " + mesCorto);
        lblEjeFin.setText(diasEnMes + " " + mesCorto);

        double totalActual = 0;
        for (double v : ingresosPorDia) totalActual += v;
        actualizarTendencia(totalActual, totalMesAnterior);
    }

    private void actualizarTendencia(double totalActual, double totalAnterior) {
        if (totalAnterior == 0 && totalActual == 0) {
            iconTendencia.setIconLiteral("fas-chart-line");
            iconTendencia.setStyle("-fx-icon-color: #8a9fad;");
            lblPorcentaje.setText("—");
            lblPorcentaje.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #8a9fad;");
            return;
        }
        if (totalAnterior == 0) {
            iconTendencia.setIconLiteral("fas-arrow-up");
            iconTendencia.setStyle("-fx-icon-color: #27ae60;");
            lblPorcentaje.setText("Nuevo");
            lblPorcentaje.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            return;
        }
        double pct = ((totalActual - totalAnterior) / totalAnterior) * 100.0;
        boolean sube = pct >= 0;
        String color = sube ? "#27ae60" : "#e53e3e";
        iconTendencia.setIconLiteral(sube ? "fas-arrow-up" : "fas-arrow-down");
        iconTendencia.setStyle("-fx-icon-color: " + color + ";");
        lblPorcentaje.setText(String.format("%+.1f%%", pct));
        lblPorcentaje.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
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
        String prop  = cbPropietario.getValue();
        String estado = cbEstadoPago.getValue();

        List<Factura> filtradas = new ArrayList<>();
        for (Factura f : todasLasFacturas) {
            if (prop != null && !"Todos".equals(prop)) {
                String nombre = f.getPropietario() != null ? f.getPropietario().getNombreCompleto() : "";
                if (!prop.equals(nombre)) continue;
            }
            if (estado != null && !"Todos".equals(estado) && !estado.equals(f.getEstadoFactura())) continue;
            if (!texto.isEmpty()) {
                String busqueda = (
                        (f.getPropietario() != null ? f.getPropietario().getNombreCompleto() : "") + " "
                        + (f.getPaciente() != null ? f.getPaciente().getNombre() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) continue;
            }
            filtradas.add(f);
        }

        tablaFacturas.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int pendientes = 0, pagadas = 0;
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
        lblStatIngresos.setText(NumericFormatter.formatCurrency(ingresos));
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
        ChoiceDialog<String> dlgMetodo = new ChoiceDialog<>("EFECTIVO",
                java.util.List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA", "OTRO"));
        dlgMetodo.setTitle("Registrar Pago");
        dlgMetodo.setHeaderText("Factura #" + sel.getId()
                + " — " + NumericFormatter.formatCurrency(sel.getTotal()));
        dlgMetodo.setContentText("Método de pago:");
        Optional<String> metodo = dlgMetodo.showAndWait();
        if (metodo.isEmpty()) return;
        try {
            service.pagar(sel.getId(), metodo.get());
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error: " + e.getMessage());
        }
    }

    private void anular(Factura sel) {
        if (ConfirmDialog.mostrar(
                "Anular Factura", "🗑",
                "¿Anular la Factura #" + sel.getId() + "?\n"
                        + "Esta acción no se puede deshacer.",
                "Anular", "#e53e3e")) {
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