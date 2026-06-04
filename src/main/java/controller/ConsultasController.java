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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Consulta;
import service.CitaService;
import service.ConsultaService;
import ui.ConfirmDialog;
import ui.NumericFormatter;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtBuscar;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatMes;
    @FXML private TableView<Consulta> tablaConsultas;
    @FXML private TableColumn<Consulta, String> colId;
    @FXML private TableColumn<Consulta, String> colFecha;
    @FXML private TableColumn<Consulta, Consulta> colPacienteProp;
    @FXML private TableColumn<Consulta, String> colVeterinario;
    @FXML private TableColumn<Consulta, String> colDiagnostico;
    @FXML private TableColumn<Consulta, String> colTratamiento;
    @FXML private TableColumn<Consulta, String> colCosto;
    @FXML private TableColumn<Consulta, Consulta> colAcciones;
    @FXML private Button btnEditar;

    private final ConsultaService service = new ConsultaService();
    private final List<Consulta> todasLasConsultas = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        tablaConsultas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
            btnEditar.setDisable(sel == null));
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("C-%03d", data.getValue().getId())));
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

        colFecha.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHora() != null
                        ? data.getValue().getFechaHora().format(FECHA_FMT) : "—"));
        colFecha.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : fecha);
                setStyle(empty || fecha == null ? "" : "-fx-text-fill: #6b7f8e;");
            }
        });

        colPacienteProp.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colPacienteProp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Consulta c, boolean empty) {
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

        colVeterinario.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getVeterinario() != null
                        ? data.getValue().getVeterinario().getNombreCompleto() : "—"));

        colDiagnostico.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDiagnostico() != null ? data.getValue().getDiagnostico() : "—"));
        colDiagnostico.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null : d);
                setStyle(empty || d == null ? "" : "-fx-font-weight: bold;");
            }
        });

        colTratamiento.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTratamiento() != null ? data.getValue().getTratamiento() : "—"));
        colTratamiento.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : t);
                setStyle(empty || t == null ? "" : "-fx-text-fill: #6b7f8e; -fx-font-size: 11px;");
            }
        });

        colCosto.setCellValueFactory(data -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(data.getValue().getCosto())));
        colCosto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : val);
                setStyle(empty || val == null ? "" : "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");
            }
        });

        colAcciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = crearChip("Ver", "action-chip action-chip-ver");
            private final HBox box = new HBox(btnVer);

            {
                box.setAlignment(Pos.CENTER);
                box.setMaxWidth(Double.MAX_VALUE);
                btnVer.setOnAction(e -> {
                    Consulta c = getTableRow().getItem();
                    if (c != null) verDetalle(c);
                });
            }

            @Override
            protected void updateItem(Consulta item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
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

    private void cargarDatos() {
        try {
            todasLasConsultas.clear();
            todasLasConsultas.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar consultas: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";

        List<Consulta> filtradas = new ArrayList<>();
        for (Consulta c : todasLasConsultas) {
            if (!texto.isEmpty()) {
                String busqueda = (
                        (c.getPaciente() != null ? c.getPaciente().getNombre() : "") + " "
                                + (c.getDiagnostico() != null ? c.getDiagnostico() : "") + " "
                                + (c.getTratamiento() != null ? c.getTratamiento() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) continue;
            }
            filtradas.add(c);
        }

        tablaConsultas.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        long mes = todasLasConsultas.stream()
                .filter(c -> c.getFechaHora() != null
                        && c.getFechaHora().getMonth() == hoy.getMonth()
                        && c.getFechaHora().getYear() == hoy.getYear())
                .count();
        lblStatTotal.setText(String.valueOf(todasLasConsultas.size()));
        lblStatMes.setText(String.valueOf(mes));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaConsulta.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Consulta");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir el formulario de nueva consulta:\n" + e.getMessage());
        }
    }

    private void verDetalle(Consulta sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleConsulta.fxml"));
            Parent root = loader.load();
            DetalleConsultaController ctrl = loader.getController();
            ctrl.setDatos(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Consulta #" + sel.getId());
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

    @FXML
    private void handleEditar() {
        Consulta sel = tablaConsultas.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modificarConsulta.fxml"));
            Parent root = loader.load();
            ModificarConsultaController ctrl = loader.getController();
            ctrl.setConsulta(sel);
            ctrl.setOnGuardado(this::cargarDatos);
            Stage stage = new Stage();
            stage.setTitle("Modificar Consulta #" + sel.getId());
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

    @FXML
    private void handleEliminar() {
        Consulta sel = tablaConsultas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Seleccione una consulta para eliminar.");
            return;
        }
        if (ConfirmDialog.mostrar(
                "Eliminar Consulta", "🗑",
                "¿Eliminar la Consulta #" + sel.getId() + "?\n"
                        + "Si tenía una cita asociada, la cita volverá a estado PROGRAMADA.",
                "Eliminar", "#e53e3e")) {
            try {
                if (sel.getCita() != null) {
                    new CitaService().actualizarEstado(sel.getCita().getId(), "PROGRAMADA");
                }
                service.eliminar(sel.getId());
                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error al eliminar: " + e.getMessage());
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