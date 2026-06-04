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
import ui.NumericFormatter;
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
    @FXML private TableColumn<Cirugia, String> colDuracion;
    @FXML private TableColumn<Cirugia, String> colEstado;
    @FXML private TableColumn<Cirugia, Cirugia> colResultado;
    @FXML private TableColumn<Cirugia, Cirugia> colOperaciones;
    @FXML private TableColumn<Cirugia, String> colCosto;

    private final CirugiaService service = new CirugiaService();
    private final List<Cirugia> todasLasCirugias = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbResultado.getItems().addAll("Todos", "Programada", "En Curso", "Finalizada");
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

        colDuracion.setCellValueFactory(data -> {
            Cirugia c = data.getValue();
            if ("En Curso".equals(c.getEstado())) return new SimpleStringProperty("En progreso");
            return new SimpleStringProperty(formatDuracion(c.getDuracion()));
        });
        colDuracion.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String dur, boolean empty) {
                super.updateItem(dur, empty);
                if (empty || dur == null) { setText(null); setStyle(""); return; }
                setText(dur);
                if ("En progreso".equals(dur)) {
                    setStyle("-fx-text-fill: #e67e22; -fx-font-style: italic;");
                } else {
                    setStyle("—".equals(dur) ? "" : "-fx-text-fill: #6b7f8e;");
                }
            }
        });

        colEstado.setCellValueFactory(data -> {
            String est = data.getValue().getEstado();
            return new SimpleStringProperty(est != null ? est : "Programada");
        });
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) { setGraphic(null); return; }
                Label badge = new Label(estado);
                badge.getStyleClass().add(claseBadgeEstado(estado));
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
            private final Button btn = new Button();
            {
                btn.setOnAction(e -> {
                    Cirugia c = getTableRow().getItem();
                    if (c == null) return;
                    String est = c.getEstado() != null ? c.getEstado() : "Programada";
                    switch (est) {
                        case "Programada" -> iniciarCirugia(c);
                        case "En Curso"   -> finalizarCirugia(c);
                        default           -> registrarResultado(c);
                    }
                });
            }
            @Override
            protected void updateItem(Cirugia item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                String est = item.getEstado() != null ? item.getEstado() : "Programada";
                switch (est) {
                    case "Programada" -> {
                        btn.setText("Iniciar");
                        btn.getStyleClass().setAll("action-chip", "action-chip-iniciar");
                        setGraphic(btn);
                    }
                    case "En Curso" -> {
                        btn.setText("Finalizar");
                        btn.getStyleClass().setAll("action-chip", "action-chip-finalizar");
                        setGraphic(btn);
                    }
                    default -> {
                        boolean sinResultado = item.getResultado() == null || item.getResultado().isBlank();
                        if (sinResultado) {
                            btn.setText("Registrar");
                            btn.getStyleClass().setAll("action-chip", "action-chip-editar");
                            setGraphic(btn);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            }
        });

        colCosto.setCellValueFactory(data -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(data.getValue().getCosto())));
        colCosto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String costo, boolean empty) {
                super.updateItem(costo, empty);
                setText(empty || costo == null ? null : costo);
                setStyle(empty || costo == null ? "" : "-fx-font-weight: bold; -fx-text-fill: #27ae60;");
            }
        });
    }

    private static String formatDuracion(int minutos) {
        if (minutos <= 0) return "—";
        if (minutos < 60) return minutos + "min";
        int h = minutos / 60;
        int m = minutos % 60;
        return h + "h " + String.format("%02d", m) + "min";
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

    private String categoriaResultado(Cirugia c) {
        String r = c.getResultado();
        if (r == null || r.isBlank()) return "Sin registrar";
        String lower = r.toLowerCase(Locale.ROOT);
        if (lower.contains("complic") || lower.contains("fall")) return "Complicación";
        return "Exitoso";
    }

    private String claseBadgeEstado(String estado) {
        if (estado == null) return "badge-programada";
        return switch (estado) {
            case "Programada" -> "badge-programada";
            case "En Curso"   -> "badge-pendiente-naranja";
            case "Realizada",
                 "Finalizada" -> "badge-confirmado";
            case "Cancelada"  -> "badge-cancelada";
            default           -> "badge-programada";
        };
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
        String estadoFiltro = cbResultado.getValue();

        List<Cirugia> filtradas = new ArrayList<>();
        for (Cirugia c : todasLasCirugias) {
            String est = c.getEstado() != null ? c.getEstado() : "Programada";
            if (estadoFiltro != null && !"Todos".equals(estadoFiltro)
                    && !estadoFiltro.equals(est)) {
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

    private void iniciarCirugia(Cirugia sel) {
        try {
            service.iniciar(sel.getId());
            cargarDatos();
        } catch (java.sql.SQLException e) {
            mostrarAlerta("Error al iniciar cirugía: " + e.getMessage());
        }
    }

    private void finalizarCirugia(Cirugia sel) {
        try {
            service.finalizar(sel.getId());
            cargarDatos();
        } catch (java.sql.SQLException e) {
            mostrarAlerta("Error al finalizar cirugía: " + e.getMessage());
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