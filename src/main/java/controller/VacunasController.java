package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Vacuna;
import org.kordamp.ikonli.javafx.FontIcon;
import service.VacunaService;
import ui.NumericFormatter;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VacunasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final int STOCK_MINIMO = 10;

    @FXML private TextField      txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Label          lblStatTotal;
    @FXML private Label          lblStatDisponibles;
    @FXML private Label          lblStatAgotadas;
    @FXML private Label          lblStatVencidas;
    @FXML private GridPane       gridTarjetas;
    @FXML private ScrollPane     scrollTarjetas;
    @FXML private javafx.scene.layout.HBox bannerStockCritico;
    @FXML private Label          lblBannerNum;

    private final VacunaService service = new VacunaService();
    private final List<Vacuna>  todasLasVacunas = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEstado.getItems().addAll("Todos los estados", "Disponible", "Stock bajo", "Sin stock", "Vencido");
        cbEstado.getSelectionModel().selectFirst();
        cbEstado.setOnAction(e -> aplicarFiltros());

        // Configurar 2 columnas de igual ancho con hgap/vgap
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(33.33);
        col.setHgrow(Priority.ALWAYS);

        gridTarjetas.getColumnConstraints().addAll(col, col, col);
        gridTarjetas.setHgap(16);
        gridTarjetas.setVgap(16);

        cargarDatos();
    }

    // ─────────────────────────────────────────────────────────────────
    //  Grid de tarjetas
    // ─────────────────────────────────────────────────────────────────

    private void construirTarjetas(List<Vacuna> lista) {
        gridTarjetas.getChildren().clear();
        for (int i = 0; i < lista.size(); i++) {
            VBox card = construirTarjeta(lista.get(i));
            GridPane.setHgrow(card, Priority.ALWAYS);
            gridTarjetas.add(card, i % 3, i / 3);
        }
    }

    private VBox construirTarjeta(Vacuna v) {
        VBox card = new VBox();
        card.getStyleClass().add("vacuna-card");
        card.setSpacing(0);
        card.setMaxWidth(Double.MAX_VALUE);

        String estado = v.calcularEstado();

        // ── Header ─────────────────────────────────────────────────
        // Ícono circular
        FontIcon icon = new FontIcon("fas-shield-alt");
        icon.setIconSize(18);
        icon.setStyle("-fx-icon-color: #2b87a0;");

        StackPane iconWrap = new StackPane(icon);
        iconWrap.setMinSize(40, 40);
        iconWrap.setMaxSize(40, 40);
        iconWrap.setStyle("-fx-background-color: #e8f4f8; -fx-background-radius: 20;");

        // Nombre + laboratorio
        Label lblNombre = new Label(v.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
        lblNombre.setWrapText(true);

        String labText = (v.getLaboratorio() != null && !v.getLaboratorio().isBlank())
                ? v.getLaboratorio() : "—";
        Label lblLab = new Label(labText);
        lblLab.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7f8e;");

        VBox nameBox = new VBox(2, lblNombre, lblLab);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        // Badge de estado
        Label badge = new Label(estadoBadgeLabel(estado));
        badge.getStyleClass().add(estadoBadgeClass(estado));

        HBox header = new HBox(12, iconWrap, nameBox, badge);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(header, new Insets(0, 0, 16, 0));

        // ── Progress bar section ────────────────────────────────────
        int stock = v.getStockDisponible();
        double progress = Math.min(stock / 40.0, 1.0);

        Label lblStockTitulo = new Label("Stock actual");
        lblStockTitulo.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7f8e;");

        Label lblStockValor = new Label(stock + " unidades");
        lblStockValor.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + stockColor(estado) + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox stockRow = new HBox(0, lblStockTitulo, spacer, lblStockValor);
        stockRow.setAlignment(Pos.CENTER_LEFT);

        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().addAll("vacuna-progress", "vacuna-progress-" + estadoKey(estado));
        VBox.setMargin(progressBar, new Insets(6, 0, 4, 0));

        Label lblMinimo = new Label("Mínimo requerido: " + STOCK_MINIMO + " unidades");
        lblMinimo.setStyle("-fx-font-size: 10px; -fx-text-fill: #8a9fad;");

        VBox progressSection = new VBox(0, stockRow, progressBar, lblMinimo);
        VBox.setMargin(progressSection, new Insets(0, 0, 16, 0));

        // ── Info cells (3 columnas) ─────────────────────────────────
        String loteVal = (v.getLote() != null && !v.getLote().isBlank()) ? v.getLote() : "—";
        String precioVal = NumericFormatter.formatCurrency(v.getPrecio());
        String vencimientoVal = (v.getFechaVencimiento() != null)
                ? v.getFechaVencimiento().format(FECHA_FMT) : "—";

        VBox cellLote       = crearInfoCell("LOTE",        loteVal);
        VBox cellPrecio     = crearInfoCell("PRECIO",      precioVal);
        VBox cellVencimiento = crearInfoCell("VENCIMIENTO", vencimientoVal);

        HBox.setHgrow(cellLote, Priority.ALWAYS);
        HBox.setHgrow(cellPrecio, Priority.ALWAYS);
        HBox.setHgrow(cellVencimiento, Priority.ALWAYS);

        HBox infoCells = new HBox(8, cellLote, cellPrecio, cellVencimiento);
        infoCells.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(infoCells, new Insets(0, 0, 16, 0));

        // ── Footer ─────────────────────────────────────────────────
        Label lblId = new Label(String.format("VAC-%03d", v.getId()));
        lblId.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; " +
                       "-fx-font-size: 12px; -fx-text-fill: #8a9fad;");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button btnVer = new Button("Ver detalle");
        btnVer.setStyle("-fx-background-color: #2b87a0; -fx-text-fill: white; " +
                        "-fx-background-radius: 7; -fx-font-size: 12px; -fx-font-weight: bold; " +
                        "-fx-padding: 4 14 4 14; -fx-cursor: hand; -fx-border-color: transparent;");
        btnVer.setPrefHeight(28);
        btnVer.setMinHeight(28);
        btnVer.setMaxHeight(28);
        btnVer.setOnAction(e -> verDetalle(v));

        Button btnStock = new Button("+ Reabastecer");
        btnStock.setStyle("-fx-background-color: transparent; -fx-text-fill: #27ae60; " +
                          "-fx-border-color: #27ae60; -fx-border-radius: 7; -fx-border-width: 1.5; " +
                          "-fx-background-radius: 7; -fx-font-size: 12px; -fx-font-weight: bold; " +
                          "-fx-padding: 4 14 4 14; -fx-cursor: hand; -fx-background-insets: 0;");
        btnStock.setPrefHeight(28);
        btnStock.setMinHeight(28);
        btnStock.setMaxHeight(28);
        btnStock.setOnAction(e -> abrirDialogoStock(v));

        HBox footer = new HBox(8, lblId, footerSpacer, btnVer, btnStock);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(header, progressSection, infoCells, footer);
        return card;
    }

    private VBox crearInfoCell(String titulo, String valor) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #8a9fad; " +
                           "-fx-text-transform: uppercase;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 12px; -fx-text-fill: #1a2e3b;");
        lblValor.setWrapText(true);

        VBox cell = new VBox(3, lblTitulo, lblValor);
        cell.setAlignment(Pos.TOP_LEFT);
        cell.setStyle("-fx-background-color: #f8fafb; -fx-background-radius: 8; -fx-padding: 8 10 8 10;");
        cell.setMaxWidth(Double.MAX_VALUE);
        return cell;
    }

    // ─────────────────────────────────────────────────────────────────
    //  Helpers de estado
    // ─────────────────────────────────────────────────────────────────

    private String stockColor(String estado) {
        return switch (estado) {
            case "Disponible" -> "#27ae60";
            case "Stock bajo" -> "#e67e22";
            default           -> "#e53e3e";   // Sin stock, Vencido
        };
    }

    private String estadoBadgeLabel(String estado) {
        return switch (estado) {
            case "Stock bajo" -> "Stock Bajo";
            case "Sin stock"  -> "Sin Stock";
            case "Vencido"    -> "Vencido";
            default           -> "Disponible";
        };
    }

    private String estadoBadgeClass(String estado) {
        return switch (estado) {
            case "Disponible" -> "badge-confirmado";
            case "Stock bajo" -> "badge-pendiente";
            case "Sin stock"  -> "badge-critico";
            default           -> "badge-inactivo";   // Vencido
        };
    }

    /** Sufijo CSS para la clase de color de la barra de progreso. */
    private String estadoKey(String estado) {
        return switch (estado) {
            case "Disponible" -> "green";
            case "Stock bajo" -> "orange";
            default           -> "red";   // Sin stock, Vencido
        };
    }

    // ─────────────────────────────────────────────────────────────────
    //  Datos y filtros
    // ─────────────────────────────────────────────────────────────────

    private void cargarDatos() {
        try {
            todasLasVacunas.clear();
            todasLasVacunas.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar vacunas: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estadoFiltro = cbEstado.getValue();

        List<Vacuna> filtradas = new ArrayList<>();
        for (Vacuna v : todasLasVacunas) {
            if (estadoFiltro != null && !"Todos los estados".equals(estadoFiltro)
                    && !estadoFiltro.equals(v.calcularEstado())) continue;
            if (!texto.isEmpty()) {
                String busqueda = (v.getNombre() + " "
                        + (v.getLaboratorio() != null ? v.getLaboratorio() : "")).toLowerCase();
                if (!busqueda.contains(texto)) continue;
            }
            filtradas.add(v);
        }
        construirTarjetas(filtradas);
        actualizarEstadisticas();
    }



    private void actualizarEstadisticas() {
        int disponibles = 0, sinStock = 0, vencidas = 0, stockBajo = 0;
        for (Vacuna v : todasLasVacunas) {
            switch (v.calcularEstado()) {
                case "Disponible" -> disponibles++;
                case "Stock bajo" -> { disponibles++; stockBajo++; }
                case "Sin stock"  -> { sinStock++;    stockBajo++; }
                case "Vencido"    -> vencidas++;
            }
        }
        lblStatTotal.setText(String.valueOf(todasLasVacunas.size()));
        lblStatDisponibles.setText(String.valueOf(disponibles));
        lblStatAgotadas.setText(String.valueOf(sinStock));
        lblStatVencidas.setText(String.valueOf(vencidas));

        // Banner de stock crítico
        boolean hayProblema = stockBajo > 0;
        bannerStockCritico.setVisible(hayProblema);
        bannerStockCritico.setManaged(hayProblema);
        if (hayProblema) {
            lblBannerNum.setText(stockBajo + (stockBajo == 1 ? " vacuna" : " vacunas"));
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Handlers FXML
    // ─────────────────────────────────────────────────────────────────

    @FXML private void handleBuscar() { aplicarFiltros(); }

    @FXML private void handleNuevo() { abrirFormulario(null); }

    // ─────────────────────────────────────────────────────────────────
    //  Diálogos y ventanas
    // ─────────────────────────────────────────────────────────────────

    private void abrirFormulario(Vacuna sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaVacuna.fxml"));
            Parent root = loader.load();
            NuevaVacunaController ctrl = loader.getController();
            if (sel != null) ctrl.setModoEdicion(sel);
            Stage stage = new Stage();
            stage.setTitle(sel == null ? "Nueva Vacuna" : "Editar Vacuna");
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

    private void verDetalle(Vacuna sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleVacuna.fxml"));
            Parent root = loader.load();
            DetalleVacunaController ctrl = loader.getController();
            ctrl.setVacuna(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle — " + sel.getNombre());
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

    private void abrirDialogoStock(Vacuna v) {
        Stage dialog = new Stage();
        dialog.setTitle("Agregar Stock");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setMaxHeight(675);

        Label lblTitulo = new Label("Agregar Stock");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a2e3b;");

        Label lblNombre = new Label(v.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: #2b87a0; -fx-font-weight: bold;");

        Label lblActualTxt = new Label("Stock actual:");
        lblActualTxt.setStyle("-fx-text-fill: #6b7f8e; -fx-font-size: 12px;");
        Label lblActual = new Label(String.valueOf(v.getStockDisponible()));
        lblActual.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        HBox rowActual = new HBox(8, lblActualTxt, lblActual);
        rowActual.setAlignment(Pos.CENTER_LEFT);

        Label lblCantTxt = new Label("Cantidad a agregar:");
        lblCantTxt.setStyle("-fx-font-size: 12px;");
        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("Ej: 10");
        txtCantidad.setPrefWidth(200);

        Label lblNuevoTxt = new Label("Nuevo stock:");
        lblNuevoTxt.setStyle("-fx-text-fill: #6b7f8e; -fx-font-size: 12px;");
        Label lblNuevo = new Label("—");
        lblNuevo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #9e9e9e;");
        HBox rowNuevo = new HBox(8, lblNuevoTxt, lblNuevo);
        rowNuevo.setAlignment(Pos.CENTER_LEFT);

        txtCantidad.textProperty().addListener((obs, old, val) -> {
            try {
                int cant = Integer.parseInt(val.trim());
                if (cant > 0) {
                    lblNuevo.setText(String.valueOf(v.getStockDisponible() + cant));
                    lblNuevo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                } else {
                    lblNuevo.setText("—");
                    lblNuevo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e53e3e;");
                }
            } catch (NumberFormatException e) {
                lblNuevo.setText("—");
                lblNuevo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #9e9e9e;");
            }
        });

        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                              "-fx-background-radius: 8; -fx-padding: 3 20 3 20; -fx-cursor: hand;");
        btnConfirmar.setPrefHeight(28);
        btnConfirmar.setMinHeight(28);
        btnConfirmar.setOnAction(ev -> {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad <= 0) throw new NumberFormatException();
                service.agregarStock(v.getId(), cantidad);
                dialog.close();
                cargarDatos();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Ingrese un número entero positivo mayor a 0.");
            } catch (SQLException ex) {
                mostrarAlerta("Error: " + ex.getMessage());
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-border-color: #9e9e9e; " +
                             "-fx-border-radius: 8; -fx-border-width: 1.5; -fx-background-radius: 8; " +
                             "-fx-text-fill: #6b7f8e; -fx-font-weight: bold; -fx-padding: 3 20 3 20; -fx-cursor: hand;");
        btnCancelar.setPrefHeight(28);
        btnCancelar.setMinHeight(28);
        btnCancelar.setOnAction(ev -> dialog.close());

        HBox btnRow = new HBox(10, btnCancelar, btnConfirmar);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(14, lblTitulo, lblNombre, new Separator(),
                rowActual, lblCantTxt, txtCantidad, rowNuevo, btnRow);
        layout.setPadding(new Insets(24));
        layout.setPrefWidth(340);

        Scene scene = new Scene(layout);
        StyleManager.apply(scene);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────
    //  Utilidades
    // ─────────────────────────────────────────────────────────────────

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}