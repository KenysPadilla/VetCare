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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Vacunacion;
import service.VacunacionService;
import ui.NumericFormatter;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VacunacionesController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtBuscar;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatMes;
    @FXML private TableView<Vacunacion> tablaVacunaciones;
    @FXML private TableColumn<Vacunacion, String> colId;
    @FXML private TableColumn<Vacunacion, Vacunacion> colPacienteProp;
    @FXML private TableColumn<Vacunacion, String> colVacuna;
    @FXML private TableColumn<Vacunacion, String> colVeterinario;
    @FXML private TableColumn<Vacunacion, String> colFechaAplicacion;
    @FXML private TableColumn<Vacunacion, String> colFechaProxima;
    @FXML private TableColumn<Vacunacion, String> colCosto;
    @FXML private TableColumn<Vacunacion, String> colEstado;
    @FXML private TableColumn<Vacunacion, Vacunacion> colAplicar;
    @FXML private TableColumn<Vacunacion, Vacunacion> colAcciones;

    private final VacunacionService service = new VacunacionService();
    private final List<Vacunacion> todasLasVacunaciones = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("V-%03d", data.getValue().getId())));
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
            protected void updateItem(Vacunacion v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) {
                    setGraphic(null);
                    return;
                }
                String pet = v.getPaciente() != null ? v.getPaciente().getNombre() : "—";
                String owner = v.getPaciente() != null && v.getPaciente().getPropietario() != null
                        ? v.getPaciente().getPropietario().getNombreCompleto() : "—";
                Label l1 = new Label(pet);
                l1.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
                Label l2 = new Label(owner);
                l2.getStyleClass().add("cell-subtext");
                setGraphic(new VBox(2, l1, l2));
            }
        });

        colVacuna.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getVacuna() != null ? data.getValue().getVacuna().getNombre() : "—"));
        colVacuna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                super.updateItem(nombre, empty);
                setText(empty || nombre == null ? null : nombre);
                setStyle(empty || nombre == null ? "" : "-fx-font-weight: bold;");
            }
        });

        colVeterinario.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getVeterinario() != null
                        ? data.getValue().getVeterinario().getNombreCompleto() : "—"));

        colFechaAplicacion.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHoraAplicacion() != null
                        ? data.getValue().getFechaHoraAplicacion().toLocalDate().format(FECHA_FMT) : "—"));

        colFechaProxima.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaProxima() != null
                        ? data.getValue().getFechaProxima().format(FECHA_FMT) : "—"));
        colFechaProxima.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : fecha);
                if (!empty && fecha != null && !"—".equals(fecha)) {
                    getStyleClass().add("followup-date");
                }
            }
        });

        colCosto.setCellValueFactory(data -> {
            double precio = data.getValue().getVacuna() != null
                    ? data.getValue().getVacuna().getPrecio() : 0.0;
            return new SimpleStringProperty(NumericFormatter.formatCurrency(precio));
        });
        colCosto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c);
                setStyle(empty || c == null ? "" : "-fx-font-weight: bold; -fx-text-fill: #27ae60;");
            }
        });

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(calcularEstadoDisplay(data.getValue())));
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

        colAplicar.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colAplicar.setCellFactory(col -> new TableCell<>() {
            private final Button btnAplicar = crearChip("Aplicada", "action-chip action-chip-editar");
            {
                btnAplicar.setOnAction(e -> {
                    Vacunacion v = getTableRow().getItem();
                    if (v != null) marcarAplicada(v);
                });
            }
            @Override
            protected void updateItem(Vacunacion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                boolean yaAplicada = "APLICADA".equals(item.getEstado());
                setGraphic(yaAplicada ? null : btnAplicar);
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
                    Vacunacion v = getTableRow().getItem();
                    if (v != null) {
                        verDetalle(v);
                    }
                });
            }

            @Override
            protected void updateItem(Vacunacion item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });
    }

    /** Calcula el estado a mostrar derivandolo de la fecha cuando no esta marcado como APLICADA. */
    static String calcularEstadoDisplay(Vacunacion v) {
        if ("APLICADA".equals(v.getEstado())) return "Aplicada";
        LocalDate hoy = LocalDate.now();
        LocalDate fechaApp = v.getFechaHoraAplicacion() != null
                ? v.getFechaHoraAplicacion().toLocalDate() : null;
        if (fechaApp == null || !fechaApp.isAfter(hoy)) return "Pendiente";
        return "Programada";
    }

    private static String claseBadgeEstado(String estado) {
        return switch (estado) {
            case "Aplicada"   -> "badge-confirmado";
            case "Pendiente"  -> "badge-pendiente-naranja";
            case "Programada" -> "badge-programada-purpura";
            default           -> "badge-pendiente-naranja";
        };
    }

    private void marcarAplicada(Vacunacion v) {
        try {
            service.marcarAplicada(v.getId());
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al marcar como aplicada: " + e.getMessage());
        }
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

    private void cargarDatos() {
        try {
            todasLasVacunaciones.clear();
            todasLasVacunaciones.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar vacunaciones: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";

        List<Vacunacion> filtradas = new ArrayList<>();
        for (Vacunacion v : todasLasVacunaciones) {
            if (!texto.isEmpty()) {
                String busqueda = (
                        (v.getPaciente() != null ? v.getPaciente().getNombre() : "") + " "
                                + (v.getVacuna() != null ? v.getVacuna().getNombre() : "") + " "
                                + (v.getVeterinario() != null ? v.getVeterinario().getNombreCompleto() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtradas.add(v);
        }

        tablaVacunaciones.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        LocalDate hoy = LocalDate.now();
        int mes = 0;
        for (Vacunacion v : todasLasVacunaciones) {
            if (v.getFechaHoraAplicacion() != null
                    && v.getFechaHoraAplicacion().getMonth() == hoy.getMonth()
                    && v.getFechaHoraAplicacion().getYear() == hoy.getYear()) {
                mes++;
            }
        }
        lblStatTotal.setText(String.valueOf(todasLasVacunaciones.size()));
        lblStatMes.setText(String.valueOf(mes));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaVacunacion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Vacunación");
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

    private void verDetalle(Vacunacion sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleVacunacion.fxml"));
            Parent root = loader.load();
            DetalleVacunacionController ctrl = loader.getController();
            ctrl.setDatos(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Vacunación #" + sel.getId());
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