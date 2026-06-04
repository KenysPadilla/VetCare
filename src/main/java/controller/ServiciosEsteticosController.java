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
import model.ServicioEstetico;
import service.ServicioEsteticoService;
import ui.NumericFormatter;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServiciosEsteticosController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final String ESTILO_TAB_ACTIVO =
            "-fx-background-color: white; -fx-text-fill: #2b87a0;" +
            "-fx-font-size: 12.5px; -fx-font-weight: bold;" +
            "-fx-background-radius: 999; -fx-border-color: transparent; -fx-border-width: 0;" +
            "-fx-padding: 7 22 7 22; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(26,46,59,0.14), 6, 0, 0, 1);";
    private static final String ESTILO_TAB_INACTIVO =
            "-fx-background-color: transparent; -fx-text-fill: #8a9fad;" +
            "-fx-font-size: 12.5px; -fx-font-weight: normal;" +
            "-fx-background-radius: 999; -fx-border-color: transparent; -fx-border-width: 0;" +
            "-fx-padding: 7 22 7 22; -fx-cursor: hand;";

    @FXML private Button btnTabBano;
    @FXML private Button btnTabMotilada;
    @FXML private VBox contenidoBano;
    @FXML private VBox contenidoMotilada;
    @FXML private TextField txtBuscarBano;
    @FXML private TextField txtBuscarMotilada;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatProgramados;
    @FXML private Label lblStatRealizados;
    @FXML private Label lblStatCancelados;

    @FXML private TableView<ServicioEstetico> tablaBanos;
    @FXML private TableColumn<ServicioEstetico, String> colBanoId;
    @FXML private TableColumn<ServicioEstetico, ServicioEstetico> colBanoPacienteProp;
    @FXML private TableColumn<ServicioEstetico, String> colBanoEstilista;
    @FXML private TableColumn<ServicioEstetico, String> colBanoFecha;
    @FXML private TableColumn<ServicioEstetico, String> colBanoHora;
    @FXML private TableColumn<ServicioEstetico, String> colBanoPrecio;
    @FXML private TableColumn<ServicioEstetico, String> colBanoEstado;
    @FXML private TableColumn<ServicioEstetico, ServicioEstetico> colBanoAcciones;
    @FXML private TableColumn<ServicioEstetico, ServicioEstetico> colBanoOperaciones;

    @FXML private TableView<ServicioEstetico> tablaMotiladas;
    @FXML private TableColumn<ServicioEstetico, String> colMotId;
    @FXML private TableColumn<ServicioEstetico, ServicioEstetico> colMotPacienteProp;
    @FXML private TableColumn<ServicioEstetico, String> colMotEstilista;
    @FXML private TableColumn<ServicioEstetico, String> colMotFecha;
    @FXML private TableColumn<ServicioEstetico, String> colMotHora;
    @FXML private TableColumn<ServicioEstetico, String> colMotPrecio;
    @FXML private TableColumn<ServicioEstetico, String> colMotEstado;
    @FXML private TableColumn<ServicioEstetico, ServicioEstetico> colMotAcciones;
    @FXML private TableColumn<ServicioEstetico, ServicioEstetico> colMotOperaciones;

    private final ServicioEsteticoService service = new ServicioEsteticoService();
    private final List<ServicioEstetico> todosBanos = new ArrayList<>();
    private final List<ServicioEstetico> todasMotiladas = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnTabBano.setStyle(ESTILO_TAB_ACTIVO);
        btnTabMotilada.setStyle(ESTILO_TAB_INACTIVO);
        configurarTablaBanos();
        configurarTablaMotiladas();
        cargarDatos();
    }

    @FXML
    private void handleSwitchBano() {
        btnTabBano.setStyle(ESTILO_TAB_ACTIVO);
        btnTabMotilada.setStyle(ESTILO_TAB_INACTIVO);
        contenidoBano.setVisible(true);
        contenidoBano.setManaged(true);
        contenidoMotilada.setVisible(false);
        contenidoMotilada.setManaged(false);
    }

    @FXML
    private void handleSwitchMotilada() {
        btnTabMotilada.setStyle(ESTILO_TAB_ACTIVO);
        btnTabBano.setStyle(ESTILO_TAB_INACTIVO);
        contenidoMotilada.setVisible(true);
        contenidoMotilada.setManaged(true);
        contenidoBano.setVisible(false);
        contenidoBano.setManaged(false);
    }

    private void configurarTablaBanos() {
        configurarColumnasComunes(
                colBanoId, colBanoPacienteProp, colBanoEstilista,
                colBanoFecha, colBanoHora, colBanoPrecio, colBanoEstado,
                colBanoAcciones, colBanoOperaciones, true);
    }

    private void configurarTablaMotiladas() {
        configurarColumnasComunes(
                colMotId, colMotPacienteProp, colMotEstilista,
                colMotFecha, colMotHora, colMotPrecio, colMotEstado,
                colMotAcciones, colMotOperaciones, false);
    }

    private void configurarColumnasComunes(
            TableColumn<ServicioEstetico, String> cId,
            TableColumn<ServicioEstetico, ServicioEstetico> cPacienteProp,
            TableColumn<ServicioEstetico, String> cEstilista,
            TableColumn<ServicioEstetico, String> cFecha,
            TableColumn<ServicioEstetico, String> cHora,
            TableColumn<ServicioEstetico, String> cPrecio,
            TableColumn<ServicioEstetico, String> cEstado,
            TableColumn<ServicioEstetico, ServicioEstetico> cAcciones,
            TableColumn<ServicioEstetico, ServicioEstetico> cOperaciones,
            boolean esBano) {

        cId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%s-%03d", esBano ? "B" : "M", data.getValue().getId())));
        cId.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : id);
                setStyle(empty || id == null ? "" : "-fx-font-family: 'Consolas'; -fx-text-fill: #2b87a0; -fx-font-weight: bold;");
            }
        });

        cPacienteProp.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        cPacienteProp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ServicioEstetico s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setGraphic(null);
                    return;
                }
                String pet = s.getPaciente() != null ? s.getPaciente().getNombre() : "—";
                String owner = s.getPaciente() != null && s.getPaciente().getPropietario() != null
                        ? s.getPaciente().getPropietario().getNombreCompleto() : "—";
                Label l1 = new Label(pet);
                l1.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
                Label l2 = new Label(owner);
                l2.getStyleClass().add("cell-subtext");
                setGraphic(new VBox(2, l1, l2));
            }
        });

        cEstilista.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getEstilista() != null
                        ? data.getValue().getEstilista().getNombreCompleto() : "—"));

        cFecha.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHora() != null
                        ? data.getValue().getFechaHora().format(FECHA_FMT) : "—"));

        cHora.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHora() != null
                        ? data.getValue().getFechaHora().format(HORA_FMT) : "—"));

        cPrecio.setCellValueFactory(data -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(data.getValue().getPrecio())));

        cEstado.setCellValueFactory(data -> new SimpleStringProperty(
                etiquetaEstado(data.getValue().getEstadoServicio())));
        cEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(estado);
                badge.getStyleClass().add(claseEstadoServicio(estado));
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        cAcciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        cAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = crearChip("Ver", "action-chip action-chip-ver");
            {
                btnVer.setOnAction(e -> {
                    ServicioEstetico s = getTableRow().getItem();
                    if (s != null) {
                        if (esBano) verDetalleBano(s);
                        else verDetalleMotilada(s);
                    }
                });
            }
            @Override
            protected void updateItem(ServicioEstetico item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : btnVer);
            }
        });

        cOperaciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        cOperaciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnCompletar = crearChip("Realizado", "action-chip action-chip-editar");
            private final Button btnCancelar = crearChip("Cancelar", "action-chip action-chip-eliminar");
            private final HBox ops = new HBox(6, btnCompletar, btnCancelar);
            {
                ops.setAlignment(Pos.CENTER);
                ops.setMaxWidth(Double.MAX_VALUE);
                btnCompletar.setOnAction(e -> {
                    ServicioEstetico s = getTableRow().getItem();
                    if (s != null) actualizarEstado(s, "REALIZADO", esBano);
                });
                btnCancelar.setOnAction(e -> {
                    ServicioEstetico s = getTableRow().getItem();
                    if (s != null) actualizarEstado(s, "CANCELADO", esBano);
                });
            }
            @Override
            protected void updateItem(ServicioEstetico item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                setGraphic("PROGRAMADO".equals(item.getEstadoServicio()) ? ops : null);
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
        if (estado == null) {
            return "—";
        }
        return switch (estado) {
            case "PROGRAMADO" -> "Programado";
            case "REALIZADO" -> "Realizado";
            case "CANCELADO" -> "Cancelado";
            default -> estado;
        };
    }

    private String claseEstadoServicio(String etiqueta) {
        return switch (etiqueta) {
            case "Realizado" -> "badge-confirmado";
            case "Cancelado" -> "badge-critico";
            default -> "badge-programada";
        };
    }

    private void cargarDatos() {
        try {
            todosBanos.clear();
            todosBanos.addAll(service.listarBanos());
            todasMotiladas.clear();
            todasMotiladas.addAll(service.listarMotiladas());
            aplicarFiltrosBanos();
            aplicarFiltrosMotiladas();
            actualizarEstadisticas();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar servicios: " + e.getMessage());
        }
    }

    private void actualizarEstadisticas() {
        List<ServicioEstetico> todos = new ArrayList<>();
        todos.addAll(todosBanos);
        todos.addAll(todasMotiladas);
        int programados = 0;
        int realizados = 0;
        int cancelados = 0;
        for (ServicioEstetico s : todos) {
            switch (s.getEstadoServicio()) {
                case "PROGRAMADO" -> programados++;
                case "REALIZADO" -> realizados++;
                case "CANCELADO" -> cancelados++;
                default -> { }
            }
        }
        lblStatTotal.setText(String.valueOf(todos.size()));
        lblStatProgramados.setText(String.valueOf(programados));
        lblStatRealizados.setText(String.valueOf(realizados));
        lblStatCancelados.setText(String.valueOf(cancelados));
    }

    private void aplicarFiltrosBanos() {
        String texto = txtBuscarBano.getText() != null ? txtBuscarBano.getText().trim().toLowerCase() : "";
        List<ServicioEstetico> filtrados = new ArrayList<>();
        for (ServicioEstetico s : todosBanos) {
            if (!texto.isEmpty()) {
                String busqueda = (
                        (s.getPaciente() != null ? s.getPaciente().getNombre() : "") + " "
                                + (s.getEstilista() != null ? s.getEstilista().getNombreCompleto() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtrados.add(s);
        }
        tablaBanos.setItems(FXCollections.observableArrayList(filtrados));
    }

    private void aplicarFiltrosMotiladas() {
        String texto = txtBuscarMotilada.getText() != null ? txtBuscarMotilada.getText().trim().toLowerCase() : "";
        List<ServicioEstetico> filtrados = new ArrayList<>();
        for (ServicioEstetico s : todasMotiladas) {
            if (!texto.isEmpty()) {
                String busqueda = (
                        (s.getPaciente() != null ? s.getPaciente().getNombre() : "") + " "
                                + (s.getEstilista() != null ? s.getEstilista().getNombreCompleto() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtrados.add(s);
        }
        tablaMotiladas.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void handleBuscarBano() {
        aplicarFiltrosBanos();
    }

    @FXML
    private void handleBuscarMotilada() {
        aplicarFiltrosMotiladas();
    }

    private void actualizarEstado(ServicioEstetico s, String estado, boolean esBano) {
        try {
            service.actualizarEstado(s.getId(), estado);
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al actualizar estado: " + e.getMessage());
        }
    }

    @FXML
    private void handleNuevoBano() {
        abrirModal("/fxml/nuevoBano.fxml", "Nuevo Servicio de Baño", true);
    }

    @FXML
    private void handleNuevaMotilada() {
        abrirModal("/fxml/nuevaMotilada.fxml", "Nueva Motilada", false);
    }

    private void abrirModal(String fxml, String titulo, boolean esBano) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
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

    private void verDetalleBano(ServicioEstetico sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleBano.fxml"));
            Parent root = loader.load();
            DetalleBanoController ctrl = loader.getController();
            ctrl.setServicio(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Baño #" + sel.getId());
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

    private void verDetalleMotilada(ServicioEstetico sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleMotilada.fxml"));
            Parent root = loader.load();
            DetalleMotiladaController ctrl = loader.getController();
            ctrl.setServicio(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Motilada #" + sel.getId());
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
