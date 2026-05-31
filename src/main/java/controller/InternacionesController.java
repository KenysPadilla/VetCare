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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Internacion;
import service.InternacionService;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class InternacionesController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatInternados;
    @FXML private Label lblStatEgresados;
    @FXML private Label lblStatDias;
    @FXML private TableView<Internacion> tablaInternaciones;
    @FXML private TableColumn<Internacion, String> colId;
    @FXML private TableColumn<Internacion, Internacion> colPacienteProp;
    @FXML private TableColumn<Internacion, String> colVeterinario;
    @FXML private TableColumn<Internacion, String> colMotivo;
    @FXML private TableColumn<Internacion, String> colFechaIngreso;
    @FXML private TableColumn<Internacion, String> colFechaEgreso;
    @FXML private TableColumn<Internacion, String> colDias;
    @FXML private TableColumn<Internacion, String> colEstado;
    @FXML private TableColumn<Internacion, String> colCostoTotal;
    @FXML private TableColumn<Internacion, Internacion> colAcciones;
    @FXML private TableColumn<Internacion, Internacion> colOperaciones;

    private final InternacionService service = new InternacionService();
    private final List<Internacion> todasLasInternaciones = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEstado.getItems().addAll("Todos los estados", "Internado", "Egresado");
        cbEstado.getSelectionModel().selectFirst();
        cbEstado.setOnAction(e -> aplicarFiltros());
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("I-%03d", data.getValue().getId())));
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
            protected void updateItem(Internacion i, boolean empty) {
                super.updateItem(i, empty);
                if (empty || i == null) {
                    setGraphic(null);
                    return;
                }
                String pet = i.getPaciente() != null ? i.getPaciente().getNombre() : "—";
                String owner = i.getPaciente() != null && i.getPaciente().getPropietario() != null
                        ? i.getPaciente().getPropietario().getNombreCompleto() : "—";
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

        colMotivo.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMotivo() != null ? data.getValue().getMotivo() : "—"));
        colMotivo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) {
                    setText(null);
                    return;
                }
                setText(m.length() > 28 ? m.substring(0, 26) + "…" : m);
                setStyle("-fx-text-fill: #6b7f8e; -fx-font-size: 11px;");
            }
        });

        colFechaIngreso.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHoraIngreso() != null
                        ? data.getValue().getFechaHoraIngreso().toLocalDate().format(FECHA_FMT) : "—"));

        colFechaEgreso.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHoraEgreso() != null
                        ? data.getValue().getFechaHoraEgreso().toLocalDate().format(FECHA_FMT) : "—"));
        colFechaEgreso.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : fecha);
                setStyle(empty || fecha == null || "—".equals(fecha)
                        ? "-fx-text-fill: #6b7f8e;" : "-fx-text-fill: #27ae60;");
            }
        });

        colDias.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(diasEstadia(data.getValue()))));

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(estadoInternacion(data.getValue())));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(estado);
                badge.getStyleClass().add("Internado".equals(estado) ? "badge-pendiente" : "badge-confirmado");
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        colCostoTotal.setCellValueFactory(data -> new SimpleStringProperty(formatearCosto(data.getValue())));
        colCostoTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String costo, boolean empty) {
                super.updateItem(costo, empty);
                setText(empty || costo == null ? null : costo);
                setStyle(empty || costo == null || "—".equals(costo)
                        ? "-fx-text-fill: #6b7f8e;" : "-fx-font-weight: bold; -fx-text-fill: #27ae60;");
            }
        });

        colAcciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = crearChip("Ver", "action-chip action-chip-ver");
            {
                btnVer.setOnAction(e -> {
                    Internacion i = getTableRow().getItem();
                    if (i != null) verDetalle(i);
                });
            }
            @Override
            protected void updateItem(Internacion item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : btnVer);
            }
        });

        colOperaciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colOperaciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnModificar = crearChip("Modificar", "action-chip action-chip-modificar");
            private final Button btnEgreso    = crearChip("Egreso",    "action-chip action-chip-editar");
            private final HBox botonesInternado = new HBox(6, btnModificar, btnEgreso);
            {
                botonesInternado.setAlignment(javafx.geometry.Pos.CENTER);
                btnModificar.setOnAction(e -> {
                    Internacion i = getTableRow().getItem();
                    if (i != null) modificarInternacion(i);
                });
                btnEgreso.setOnAction(e -> {
                    Internacion i = getTableRow().getItem();
                    if (i != null) darEgreso(i);
                });
            }
            @Override
            protected void updateItem(Internacion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                setGraphic(item.getFechaHoraEgreso() == null ? botonesInternado : null);
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

    private String estadoInternacion(Internacion i) {
        return i.getFechaHoraEgreso() == null ? "Internado" : "Egresado";
    }

    private long diasEstadia(Internacion i) {
        if (i.getFechaHoraIngreso() == null) {
            return 0;
        }
        LocalDate fin = i.getFechaHoraEgreso() != null
                ? i.getFechaHoraEgreso().toLocalDate()
                : LocalDate.now();
        return Math.max(0, ChronoUnit.DAYS.between(i.getFechaHoraIngreso().toLocalDate(), fin));
    }

    private String formatearCosto(Internacion i) {
        if (i.getFechaHoraEgreso() == null) {
            return "—";
        }
        return String.format("$%.2f", i.calcularCostoTotal());
    }

    private void cargarDatos() {
        try {
            todasLasInternaciones.clear();
            todasLasInternaciones.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar internaciones: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estadoFiltro = cbEstado.getValue();

        List<Internacion> filtradas = new ArrayList<>();
        for (Internacion i : todasLasInternaciones) {
            String estado = estadoInternacion(i);
            if (estadoFiltro != null && !"Todos los estados".equals(estadoFiltro) && !estadoFiltro.equals(estado)) {
                continue;
            }
            if (!texto.isEmpty()) {
                String busqueda = (
                        (i.getPaciente() != null ? i.getPaciente().getNombre() : "") + " "
                                + (i.getMotivo() != null ? i.getMotivo() : "") + " "
                                + (i.getVeterinario() != null ? i.getVeterinario().getNombreCompleto() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtradas.add(i);
        }

        tablaInternaciones.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int internados = 0;
        int egresados = 0;
        long diasTotal = 0;
        for (Internacion i : todasLasInternaciones) {
            if (i.getFechaHoraEgreso() == null) {
                internados++;
            } else {
                egresados++;
            }
            diasTotal += diasEstadia(i);
        }
        lblStatTotal.setText(String.valueOf(todasLasInternaciones.size()));
        lblStatInternados.setText(String.valueOf(internados));
        lblStatEgresados.setText(String.valueOf(egresados));
        lblStatDias.setText(String.valueOf(diasTotal));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaInternacion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Internación");
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

    private void modificarInternacion(Internacion sel) {
        if (sel.getFechaHoraEgreso() != null) return; // solo Internado
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modificarInternacion.fxml"));
            Parent root = loader.load();
            ModificarInternacionController ctrl = loader.getController();
            ctrl.setInternacion(sel);
            ctrl.setOnGuardado(this::cargarDatos);
            Stage stage = new Stage();
            stage.setTitle("Modificar Internación #" + sel.getId());
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

    private void darEgreso(Internacion sel) {
        if (sel.getFechaHoraEgreso() != null) {
            mostrarAlerta("Esta internación ya tiene fecha de egreso registrada.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Egreso");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Registrar egreso para la internación #"
                + sel.getId() + " con fecha de hoy?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                service.darEgreso(sel.getId(), LocalDateTime.now());
                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error al registrar egreso: " + e.getMessage());
            }
        }
    }

    private void verDetalle(Internacion sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleInternacion.fxml"));
            Parent root = loader.load();
            DetalleInternacionController ctrl = loader.getController();
            ctrl.setDatos(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Internación #" + sel.getId());
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}