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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Medicamento;
import service.MedicamentoService;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MedicamentosController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatDisponibles;
    @FXML private Label lblStatAgotados;
    @FXML private Label lblStatPorVencer;
    @FXML private TableView<Medicamento> tablaMedicamentos;
    @FXML private TableColumn<Medicamento, String>      colId;
    @FXML private TableColumn<Medicamento, String>      colNombre;
    @FXML private TableColumn<Medicamento, String>      colPrincipio;
    @FXML private TableColumn<Medicamento, String>      colConcentracion;
    @FXML private TableColumn<Medicamento, String>      colStock;
    @FXML private TableColumn<Medicamento, String>      colPrecio;
    @FXML private TableColumn<Medicamento, String>      colVencimiento;
    @FXML private TableColumn<Medicamento, String>      colEstado;
    @FXML private TableColumn<Medicamento, Medicamento> colAcciones;
    @FXML private Button btnEditar;
    @FXML private Button btnStock;

    private final MedicamentoService service = new MedicamentoService();
    private final List<Medicamento> todosLosMedicamentos = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEstado.getItems().addAll("Todos los estados", "Disponible", "Stock bajo", "Sin stock", "Vencido");
        cbEstado.getSelectionModel().selectFirst();
        cbEstado.setOnAction(e -> aplicarFiltros());
        configurarColumnas();
        cargarDatos();
        tablaMedicamentos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            btnEditar.setDisable(sel == null);
            btnStock.setDisable(sel == null);
        });
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("M-%03d", data.getValue().getId())));
        colId.setCellFactory(col -> new TableCell<>() {
            { selectedProperty().addListener((obs, was, now) -> applyIdStyle()); }
            private void applyIdStyle() {
                if (isEmpty() || getItem() == null) { setStyle(""); return; }
                setStyle("-fx-font-family: 'Consolas'; -fx-font-weight: bold; -fx-text-fill: "
                        + (isSelected() ? "#1a2e3b" : "#2b87a0") + ";");
            }
            @Override protected void updateItem(String id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : id);
                applyIdStyle();
            }
        });

        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String n, boolean empty) {
                super.updateItem(n, empty);
                setText(empty || n == null ? null : n);
                setStyle(empty || n == null ? "" : "-fx-font-weight: bold;");
            }
        });

        colPrincipio.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDescripcion() != null ? data.getValue().getDescripcion() : "—"));
        colConcentracion.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getConcentracion() != null ? data.getValue().getConcentracion() : "—"));

        colStock.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().getStockDisponible())));
        colStock.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s);
                if (empty || s == null) return;
                int stock = Integer.parseInt(s);
                String color = stock == 0 ? "#e53e3e" : (stock <= 5 ? "#e67e22" : "#27ae60");
                setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
            }
        });

        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("$%.2f", data.getValue().getPrecio())));

        colVencimiento.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaVencimiento() != null
                        ? data.getValue().getFechaVencimiento().format(FECHA_FMT) : "—"));

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().calcularEstado()));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) { setGraphic(null); return; }
                Label badge = new Label(estado);
                badge.getStyleClass().add(switch (estado) {
                    case "Disponible" -> "badge-confirmado";
                    case "Stock bajo" -> "badge-pendiente";
                    case "Sin stock"  -> "badge-critico";
                    default           -> "badge-inactivo";   // Vencido
                });
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        // ── Columna "Detalles" — botón Ver ──
        colAcciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = crearChip("Ver", "action-chip action-chip-ver");
            { btnVer.setOnAction(e -> { Medicamento m = getTableRow().getItem(); if (m != null) verDetalle(m); }); }
            @Override protected void updateItem(Medicamento item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : btnVer);
            }
        });

    }

    private Button crearChip(String texto, String cssClass) {
        Button btn = new Button(texto);
        String inlineStyle = ui.StyleManager.chipStyle(cssClass);
        if (inlineStyle != null) btn.setStyle(inlineStyle);
        else btn.getStyleClass().add(cssClass);
        btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        return btn;
    }

    @FXML private void handleStock() {
        Medicamento sel = tablaMedicamentos.getSelectionModel().getSelectedItem();
        if (sel != null) abrirDialogoStock(sel);
    }

    private void abrirDialogoStock(Medicamento m) {
        Stage dialog = new Stage();
        dialog.setTitle("Agregar Stock");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setMaxHeight(675);

        Label lblTitulo = new Label("Agregar Stock");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a2e3b;");

        Label lblNombre = new Label(m.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: #2b87a0; -fx-font-weight: bold;");

        Label lblActualTxt = new Label("Stock actual:");
        lblActualTxt.setStyle("-fx-text-fill: #6b7f8e; -fx-font-size: 12px;");
        Label lblActual = new Label(String.valueOf(m.getStockDisponible()));
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
                    lblNuevo.setText(String.valueOf(m.getStockDisponible() + cant));
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
        btnConfirmar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        btnConfirmar.setOnAction(ev -> {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad <= 0) throw new NumberFormatException();
                service.agregarStock(m.getId(), cantidad);
                dialog.close();
                cargarDatos();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Ingrese un número entero positivo mayor a 0.");
            } catch (SQLException ex) {
                mostrarAlerta("Error: " + ex.getMessage());
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-border-color: #9e9e9e; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-background-radius: 8; -fx-text-fill: #6b7f8e; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        btnCancelar.setOnAction(ev -> dialog.close());

        HBox btnRow = new HBox(10, btnCancelar, btnConfirmar);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(14, lblTitulo, lblNombre, new javafx.scene.control.Separator(),
                rowActual, lblCantTxt, txtCantidad, rowNuevo, btnRow);
        layout.setPadding(new Insets(24));
        layout.setPrefWidth(340);

        javafx.scene.Scene scene = new javafx.scene.Scene(layout);
        StyleManager.apply(scene);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void cargarDatos() {
        try {
            todosLosMedicamentos.clear();
            todosLosMedicamentos.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar medicamentos: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estadoFiltro = cbEstado.getValue();

        List<Medicamento> filtrados = new ArrayList<>();
        for (Medicamento m : todosLosMedicamentos) {
            if (estadoFiltro != null && !"Todos los estados".equals(estadoFiltro)
                    && !estadoFiltro.equals(m.calcularEstado())) continue;
            if (!texto.isEmpty()) {
                String busqueda = (m.getNombre() + " "
                        + (m.getDescripcion() != null ? m.getDescripcion() : "")).toLowerCase();
                if (!busqueda.contains(texto)) continue;
            }
            filtrados.add(m);
        }
        tablaMedicamentos.setItems(FXCollections.observableArrayList(filtrados));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int disponibles = 0, sinStock = 0, vencidos = 0;
        for (Medicamento m : todosLosMedicamentos) {
            switch (m.calcularEstado()) {
                case "Disponible", "Stock bajo" -> disponibles++;
                case "Sin stock"               -> sinStock++;
                case "Vencido"                 -> vencidos++;
            }
        }
        lblStatTotal.setText(String.valueOf(todosLosMedicamentos.size()));
        lblStatDisponibles.setText(String.valueOf(disponibles));
        lblStatAgotados.setText(String.valueOf(sinStock));
        lblStatPorVencer.setText(String.valueOf(vencidos));
    }

    @FXML private void handleBuscar() { aplicarFiltros(); }

    @FXML private void handleNuevo() { abrirFormulario(null); }

    private void abrirFormulario(Medicamento sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoMedicamento.fxml"));
            Parent root = loader.load();
            NuevoMedicamentoController ctrl = loader.getController();
            if (sel != null) ctrl.setModoEdicion(sel);
            Stage stage = new Stage();
            stage.setTitle(sel == null ? "Nuevo Medicamento" : "Editar Medicamento");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void verDetalle(Medicamento sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleMedicamento.fxml"));
            Parent root = loader.load();
            DetalleMedicamentoController detCtrl = loader.getController();
            detCtrl.setMedicamento(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle — " + sel.getNombre());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleEditar() {
        Medicamento sel = tablaMedicamentos.getSelectionModel().getSelectedItem();
        if (sel != null) abrirFormulario(sel);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}