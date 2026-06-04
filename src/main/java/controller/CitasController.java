package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Cita;
import model.Consulta;
import model.Veterinario;
import service.CitaService;
import service.ConsultaService;
import service.VeterinarioService;
import ui.ConfirmDialog;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CitasController implements Initializable {

    private static final DateTimeFormatter HORA_FMT      = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FECHA_HORA_FMT = DateTimeFormatter.ofPattern("dd/MM/yy  HH:mm");
    private static final DateTimeFormatter DIA_FMT        = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("es", "ES"));

    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Veterinario> cbVeterinario;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TextField txtBuscarCita;
    @FXML private Label lblMesCita;
    @FXML private Label lblCitasDia;
    @FXML private Label lblStatHoy;
    @FXML private Label lblStatConfirmadas;
    @FXML private Label lblStatPendientes;
    @FXML private Label lblStatCanceladas;
    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colPropietario;
    @FXML private TableColumn<Cita, String> colMotivo;
    @FXML private TableColumn<Cita, String> colVeterinario;
    @FXML private TableColumn<Cita, String> colEstado;
    @FXML private javafx.scene.control.Button btnVerTodas;

    private final List<Cita> todasLasCitas = new ArrayList<>();
    private boolean mostrarTodas = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dpFecha.setValue(LocalDate.now());
        dpFecha.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                lblMesCita.setText(val.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"))));
                lblCitasDia.setText("Citas del día — " + val.format(DIA_FMT));
                aplicarFiltros();
            }
        });

        cbEstado.getItems().addAll("Todos", "Programada", "En curso", "Realizada", "Cancelada");
        cbEstado.setValue("Todos");

        cbVeterinario.getItems().add(null);
        try {
            cbVeterinario.getItems().addAll(new VeterinarioService().listarActivos());
        } catch (SQLException e) {
            System.err.println("Error cargando veterinarios: " + e.getMessage());
        }
        cbVeterinario.setValue(null);
        cbVeterinario.setConverter(new StringConverter<>() {
            @Override
            public String toString(Veterinario v) {
                return v == null ? "Todos" : v.getNombreCompleto();
            }

            @Override
            public Veterinario fromString(String s) {
                return null;
            }
        });

        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colHora.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHora() != null
                        ? data.getValue().getFechaHora().format(HORA_FMT) : "—"));

        colPaciente.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPaciente() != null ? data.getValue().getPaciente().getNombre() : ""));

        colPropietario.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPaciente() != null && data.getValue().getPaciente().getPropietario() != null
                        ? data.getValue().getPaciente().getPropietario().getNombreCompleto() : ""));

        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        colVeterinario.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getVeterinario() != null
                        ? data.getValue().getVeterinario().getNombreCompleto() : ""));
        colVeterinario.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String vet, boolean empty) {
                super.updateItem(vet, empty);
                setText(empty || vet == null ? null : vet);
                setStyle(empty || vet == null ? "" : "-fx-text-fill: #6b7f8e;");
            }
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(formatearEstado(estado));
                badge.getStyleClass().add(claseBadge(estado));
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });
    }

    private String formatearEstado(String estado) {
        if (estado == null) {
            return "—";
        }
        return switch (estado) {
            case "PROGRAMADA" -> "Pendiente";
            case "EN_CURSO"   -> "En Curso";
            case "REALIZADA"  -> "Realizada";
            case "CANCELADA"  -> "Cancelado";
            default -> estado;
        };
    }

    private String claseBadge(String estado) {
        if (estado == null) {
            return "badge-activo";
        }
        return switch (estado) {
            case "PROGRAMADA" -> "badge-programada";
            case "EN_CURSO"   -> "badge-en-curso";
            case "REALIZADA"  -> "badge-confirmado";
            case "CANCELADA"  -> "badge-cancelada";
            default -> "badge-programada";
        };
    }

    private void cargarDatos() {
        try {
            todasLasCitas.clear();
            todasLasCitas.addAll(new CitaService().listarTodos());
            LocalDate hoy = LocalDate.now();
            lblMesCita.setText(hoy.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"))));
            lblCitasDia.setText("Citas del día — " + hoy.format(DIA_FMT));
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar citas: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        LocalDate fecha = dpFecha.getValue() != null ? dpFecha.getValue() : LocalDate.now();
        Veterinario vet = cbVeterinario.getValue();
        String estadoFx = cbEstado.getValue();
        String texto = txtBuscarCita.getText() != null ? txtBuscarCita.getText().trim().toLowerCase() : "";

        List<Cita> filtradas = new ArrayList<>();
        for (Cita c : todasLasCitas) {
            if (!mostrarTodas && (c.getFechaHora() == null
                    || !c.getFechaHora().toLocalDate().equals(fecha))) {
                continue;
            }
            if (vet != null && (c.getVeterinario() == null
                    || !vet.getCedula().equals(c.getVeterinario().getCedula()))) {
                continue;
            }
            if (estadoFx != null && !"Todos".equals(estadoFx)
                    && !mapearEstado(estadoFx).equals(c.getEstadoCita())) {
                continue;
            }
            if (!texto.isEmpty()) {
                String busqueda = (
                        (c.getPaciente() != null ? c.getPaciente().getNombre() : "") + " "
                                + (c.getMotivo() != null ? c.getMotivo() : "") + " "
                                + (c.getVeterinario() != null ? c.getVeterinario().getNombreCompleto() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtradas.add(c);
        }

        filtradas.sort((a, b) -> {
            LocalDateTime fa = a.getFechaHora();
            LocalDateTime fb = b.getFechaHora();
            if (fa == null || fb == null) return 0;
            return fa.compareTo(fb);
        });

        tablaCitas.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas(filtradas);
    }

    private void actualizarEstadisticas(List<Cita> visible) {
        int confirmadas = 0;
        int pendientes = 0;
        int canceladas = 0;
        for (Cita c : visible) {
            String e = c.getEstadoCita();
            if ("CANCELADA".equals(e)) {
                canceladas++;
            } else if ("PROGRAMADA".equals(e)) {
                pendientes++;
            } else if ("REALIZADA".equals(e) || "EN_CURSO".equals(e)) {
                confirmadas++;
            }
        }
        lblStatHoy.setText(String.valueOf(visible.size()));
        lblStatConfirmadas.setText(String.valueOf(confirmadas));
        lblStatPendientes.setText(String.valueOf(pendientes));
        lblStatCanceladas.setText(String.valueOf(canceladas));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleVerTodas() {
        mostrarTodas = !mostrarTodas;
        if (mostrarTodas) {
            btnVerTodas.setText("Ver por fecha");
            btnVerTodas.setStyle("-fx-background-color: #e8f4f8; -fx-text-fill: #2b87a0; -fx-border-color: #2b87a0; -fx-border-radius: 8; -fx-border-width: 1; -fx-background-insets: 0; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 18 9 18; -fx-cursor: hand;");
            lblCitasDia.setText("Todas las citas registradas");
            colHora.setText("FECHA / HORA");
            colHora.setPrefWidth(120);
            colHora.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().getFechaHora() != null
                            ? data.getValue().getFechaHora().format(FECHA_HORA_FMT) : "—"));
        } else {
            btnVerTodas.setText("Ver todas las citas");
            btnVerTodas.setStyle("-fx-background-color: #2b87a0; -fx-text-fill: white; -fx-border-color: transparent; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 18 9 18; -fx-cursor: hand;");
            LocalDate fecha = dpFecha.getValue() != null ? dpFecha.getValue() : LocalDate.now();
            lblCitasDia.setText("Citas del día — " + fecha.format(DIA_FMT));
            colHora.setText("HORA");
            colHora.setPrefWidth(80);
            colHora.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().getFechaHora() != null
                            ? data.getValue().getFechaHora().format(HORA_FMT) : "—"));
        }
        aplicarFiltros();
    }

    @FXML
    private void handleNuevaCita() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaCita.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Cita Médica");
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

    @FXML
    private void handleVerDetalle() {
        Cita sel = tablaCitas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Debe seleccionar una cita de la tabla para ver su detalle.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleCita.fxml"));
            Parent root = loader.load();
            DetalleCitaController ctrl = loader.getController();
            ctrl.setCita(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Cita #" + sel.getId());
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

    @FXML
    private void handleModificar() {
        Cita sel = tablaCitas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Debe seleccionar una cita de la tabla para modificarla.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaCita.fxml"));
            Parent root = loader.load();
            NuevaCitaController ctrl = loader.getController();
            ctrl.setModoEdicion(sel);
            Stage stage = new Stage();
            stage.setTitle("Editar Cita #" + sel.getId());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir edición: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMarcarEnCurso() {
        Cita sel = tablaCitas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Debe seleccionar una cita de la tabla.");
            return;
        }
        if (!"PROGRAMADA".equals(sel.getEstadoCita())) {
            mostrarAlerta("Solo las citas en estado PROGRAMADA pueden marcarse como En Curso.");
            return;
        }

        if (!ConfirmDialog.mostrar(
                "Marcar En Curso", "▶",
                "¿Marcar la Cita #" + sel.getId() + " como EN CURSO?",
                "Confirmar", "#e67e22")) {
            return;
        }

        try {
            new CitaService().actualizarEstado(sel.getId(), "EN_CURSO");
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al cambiar estado: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelarCita() {
        Cita sel = tablaCitas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Debe seleccionar una cita de la tabla para cancelarla.");
            return;
        }

        Consulta consultaAsoc = null;
        try {
            consultaAsoc = new ConsultaService().buscarPorCita(sel.getId());
        } catch (SQLException ignored) {
        }

        String mensaje = "¿Desea cancelar la cita #" + sel.getId() + "?";
        if (consultaAsoc != null) {
            mensaje += "\n\nATENCIÓN: Esta cita tiene una consulta médica registrada (ID "
                    + consultaAsoc.getId() + "). Al cancelar, la consulta y sus prescripciones también serán eliminadas.";
        }

        if (ConfirmDialog.mostrar(
                "Cancelar Cita", "✖",
                mensaje,
                "Cancelar Cita", "#e53e3e")) {
            try {
                if (consultaAsoc != null) {
                    new ConsultaService().eliminar(consultaAsoc.getId());
                }
                new CitaService().cancelarCita(sel.getId());
                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error al cancelar la cita: " + e.getMessage());
            }
        }
    }

    private String mapearEstado(String estadoFx) {
        return switch (estadoFx) {
            case "Programada" -> "PROGRAMADA";
            case "En curso"   -> "EN_CURSO";
            case "Realizada"  -> "REALIZADA";
            case "Cancelada"  -> "CANCELADA";
            default -> estadoFx.toUpperCase();
        };
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}