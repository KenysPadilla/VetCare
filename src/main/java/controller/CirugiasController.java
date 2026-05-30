package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Cirugia;
import service.CirugiaService;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CirugiasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbResultado;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatExitosas;
    @FXML private Label lblStatComplicadas;
    @FXML private Label lblStatMes;
    @FXML private TableView<Cirugia> tablaCirugias;
    @FXML private TableColumn<Cirugia, String> colId;
    @FXML private TableColumn<Cirugia, Cirugia> colPacienteProp;
    @FXML private TableColumn<Cirugia, String> colTipo;
    @FXML private TableColumn<Cirugia, String> colVeterinario;
    @FXML private TableColumn<Cirugia, String> colFechaHora;
    @FXML private TableColumn<Cirugia, String> colAnestesia;
    @FXML private TableColumn<Cirugia, String> colEstado;
    @FXML private TableColumn<Cirugia, Cirugia> colResultado;
    @FXML private TableColumn<Cirugia, Cirugia> colOperaciones;
    @FXML private TableColumn<Cirugia, String> colCosto;

    private final CirugiaService service = new CirugiaService();
    private final List<Cirugia> todasLasCirugias = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbResultado.getItems().addAll("Todos los resultados", "Exitoso", "Complicación", "Sin registrar");
        cbResultado.getSelectionModel().selectFirst();
        cbResultado.setOnAction(e -> aplicarFiltros());
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("Q-%03d", data.getValue().getId())));
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

        colPacienteProp.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colPacienteProp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Cirugia c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setGraphic(null);
                    return;
                }
                String pet = c.getPaciente() != null ? c.getPaciente().getNombre() : "—";
                String owner = c.getPaciente() != null && c.getPaciente().getPropietario() != null
                        ? c.getPaciente().getPropietario().getNombreCompleto() : "—";
                Label l1 = new Label(pet);
                l1.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
                Label l2 = new Label(owner);
                l2.getStyleClass().add("cell-subtext");
                setGraphic(new VBox(2, l1, l2));
            }
        });

        colTipo.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTipoCirugia() != null ? data.getValue().getTipoCirugia() : "—"));
        colTipo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String tipo, boolean empty) {
                super.updateItem(tipo, empty);
                setText(empty || tipo == null ? null : tipo);
                setStyle(empty || tipo == null ? "" : "-fx-font-weight: bold;");
            }
        });

        colVeterinario.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getVeterinario() != null
                        ? data.getValue().getVeterinario().getNombreCompleto() : "—"));

        colFechaHora.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHora() != null
                        ? data.getValue().getFechaHora().format(FECHA_FMT) : "—"));
        colFechaHora.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : fecha);
                setStyle(empty || fecha == null ? "" : "-fx-text-fill: #6b7f8e;");
            }
        });

        colAnestesia.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAnestesia() != null ? data.getValue().getAnestesia() : "—"));
        colAnestesia.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a);
                setStyle(empty || a == null ? "" : "-fx-text-fill: #6b7f8e; -fx-font-size: 11px;");
            }
        });

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(categoriaResultado(data.getValue())));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) { setGraphic(null); return; }
                Label badge = new Label(estado);
                badge.getStyleClass().add(claseBadgeResultado(estado));
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        colResultado.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colResultado.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = crearChip("Ver", "action-chip action-chip-ver");
            {
                btnVer.setOnAction(e -> {
                    Cirugia c = getTableRow().getItem();
                    if (c != null) verDetalle(c);
                });
            }
            @Override
            protected void updateItem(Cirugia item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : btnVer);
            }
        });

        colOperaciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colOperaciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnRegistrar = crearChip("Registrar", "action-chip action-chip-editar");
            {
                btnRegistrar.setOnAction(e -> {
                    Cirugia c = getTableRow().getItem();
                    if (c != null) registrarResultado(c);
                });
            }
            @Override
            protected void updateItem(Cirugia item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                boolean sinResultado = item.getResultado() == null || item.getResultado().isBlank();
                setGraphic(sinResultado ? btnRegistrar : null);
            }
        });

        colCosto.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("$%.2f", data.getValue().getCosto())));
        colCosto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String costo, boolean empty) {
                super.updateItem(costo, empty);
                setText(empty || costo == null ? null : costo);
                setStyle(empty || costo == null ? "" : "-fx-font-weight: bold; -fx-text-fill: #27ae60;");
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

    private String categoriaResultado(Cirugia c) {
        String r = c.getResultado();
        if (r == null || r.isBlank()) {
            return "Sin registrar";
        }
        String lower = r.toLowerCase(Locale.ROOT);
        if (lower.contains("complic") || lower.contains("fall")) {
            return "Complicación";
        }
        if (lower.contains("exit") || lower.contains("éxit") || lower.contains("satisf")) {
            return "Exitoso";
        }
        return "Exitoso";
    }

    private String etiquetaResultado(Cirugia c) {
        String cat = categoriaResultado(c);
        if ("Sin registrar".equals(cat)) {
            return cat;
        }
        String r = c.getResultado();
        return r != null && r.length() > 24 ? r.substring(0, 22) + "…" : r;
    }

    private String claseBadgeResultado(String etiqueta) {
        if (etiqueta == null) {
            return "badge-pendiente";
        }
        String cat = categoriaResultadoPorEtiqueta(etiqueta);
        return switch (cat) {
            case "Complicación" -> "badge-critico";
            case "Exitoso" -> "badge-confirmado";
            default -> "badge-pendiente";
        };
    }

    private String categoriaResultadoPorEtiqueta(String etiqueta) {
        if ("Sin registrar".equals(etiqueta)) {
            return "Sin registrar";
        }
        String lower = etiqueta.toLowerCase(Locale.ROOT);
        if (lower.contains("complic") || lower.contains("fall")) {
            return "Complicación";
        }
        return "Exitoso";
    }

    private void cargarDatos() {
        try {
            todasLasCirugias.clear();
            todasLasCirugias.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar cirugías: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String resultadoFiltro = cbResultado.getValue();

        List<Cirugia> filtradas = new ArrayList<>();
        for (Cirugia c : todasLasCirugias) {
            String cat = categoriaResultado(c);
            if (resultadoFiltro != null && !"Todos los resultados".equals(resultadoFiltro)
                    && !resultadoFiltro.equals(cat)) {
                continue;
            }
            if (!texto.isEmpty()) {
                String busqueda = (
                        (c.getPaciente() != null ? c.getPaciente().getNombre() : "") + " "
                                + (c.getTipoCirugia() != null ? c.getTipoCirugia() : "") + " "
                                + (c.getVeterinario() != null ? c.getVeterinario().getNombreCompleto() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtradas.add(c);
        }

        tablaCirugias.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        LocalDate hoy = LocalDate.now();
        int exitosas = 0;
        int complicadas = 0;
        int mes = 0;
        for (Cirugia c : todasLasCirugias) {
            switch (categoriaResultado(c)) {
                case "Exitoso" -> exitosas++;
                case "Complicación" -> complicadas++;
                default -> { }
            }
            if (c.getFechaHora() != null
                    && c.getFechaHora().getMonth() == hoy.getMonth()
                    && c.getFechaHora().getYear() == hoy.getYear()) {
                mes++;
            }
        }
        lblStatTotal.setText(String.valueOf(todasLasCirugias.size()));
        lblStatExitosas.setText(String.valueOf(exitosas));
        lblStatComplicadas.setText(String.valueOf(complicadas));
        lblStatMes.setText(String.valueOf(mes));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaCirugia.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Cirugía");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarResultado(Cirugia sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registrarResultadoCirugia.fxml"));
            Parent root = loader.load();
            RegistrarResultadoCirugiaController ctrl = loader.getController();
            ctrl.setCirugia(sel);
            ctrl.setOnGuardado(this::cargarDatos);
            Stage stage = new Stage();
            stage.setTitle("Registrar Resultado de Cirugía");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verDetalle(Cirugia sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleCirugia.fxml"));
            Parent root = loader.load();
            DetalleCirugiaController ctrl = loader.getController();
            ctrl.setDatos(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Cirugía #" + sel.getId());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
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