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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ExamenLab;
import service.ExamenLabService;
import ui.NumericFormatter;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LaboratorioController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatConResultado;
    @FXML private Label lblStatPendientes;
    @FXML private Label lblStatMes;
    @FXML private TableView<ExamenLab> tablaExamenes;
    @FXML private TableColumn<ExamenLab, String> colId;
    @FXML private TableColumn<ExamenLab, ExamenLab> colPacienteProp;
    @FXML private TableColumn<ExamenLab, String> colTipoExamen;
    @FXML private TableColumn<ExamenLab, String> colPrioridad;
    @FXML private TableColumn<ExamenLab, String> colFechaSolicitud;
    @FXML private TableColumn<ExamenLab, String> colResultados;
    @FXML private TableColumn<ExamenLab, String> colEstado;
    @FXML private TableColumn<ExamenLab, String> colCosto;
    @FXML private TableColumn<ExamenLab, ExamenLab> colAcciones;
    @FXML private TableColumn<ExamenLab, ExamenLab> colRegistrar;

    private final ExamenLabService service = new ExamenLabService();
    private final List<ExamenLab> todosLosExamenes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEstado.getItems().addAll("Todos los estados", "Con resultado", "Pendiente");
        cbEstado.getSelectionModel().selectFirst();
        cbEstado.setOnAction(e -> aplicarFiltros());
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("L-%03d", data.getValue().getId())));
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
            protected void updateItem(ExamenLab e, boolean empty) {
                super.updateItem(e, empty);
                if (empty || e == null) {
                    setGraphic(null);
                    return;
                }
                String pet = e.getPaciente() != null ? e.getPaciente().getNombre() : "—";
                String owner = e.getPaciente() != null && e.getPaciente().getPropietario() != null
                        ? e.getPaciente().getPropietario().getNombreCompleto() : "—";
                Label l1 = new Label(pet);
                l1.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
                Label l2 = new Label(owner);
                l2.getStyleClass().add("cell-subtext");
                setGraphic(new VBox(2, l1, l2));
            }
        });

        colTipoExamen.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTipoExamen() != null ? data.getValue().getTipoExamen() : "—"));
        colTipoExamen.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? null : t);
                setStyle(empty || t == null ? "" : "-fx-font-weight: bold;");
            }
        });

        colPrioridad.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPrioridad() != null ? data.getValue().getPrioridad() : "NORMAL"));
        colPrioridad.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setGraphic(null); return; }
                boolean urgente = "URGENTE".equalsIgnoreCase(p);
                Label badge = new Label(urgente ? "🔔  Urgente" : "Normal");
                badge.setStyle(urgente
                        ? "-fx-background-color: #fde8e8; -fx-text-fill: #e53e3e;" +
                          "-fx-font-size: 11px; -fx-font-weight: bold;" +
                          "-fx-background-radius: 12; -fx-padding: 4 10 4 10;"
                        : "-fx-background-color: #edf9f2; -fx-text-fill: #27ae60;" +
                          "-fx-font-size: 11px; -fx-font-weight: bold;" +
                          "-fx-background-radius: 12; -fx-padding: 4 12 4 12;");
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        colFechaSolicitud.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaHora() != null
                        ? data.getValue().getFechaHora().toLocalDate().format(FECHA_FMT) : "—"));

        colResultados.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getResultado() != null && !data.getValue().getResultado().isBlank()
                        ? data.getValue().getResultado() : ""));
        colResultados.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String r, boolean empty) {
                super.updateItem(r, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                    return;
                }
                if (r == null || r.isBlank()) {
                    setText("Sin resultado");
                    setStyle("-fx-text-fill: #b0bec5; -fx-font-style: italic; -fx-font-size: 11px;");
                } else {
                    setText(r.length() > 35 ? r.substring(0, 33) + "…" : r);
                    setStyle("-fx-text-fill: #1a2e3b; -fx-font-size: 12px;");
                }
            }
        });

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(
                tieneResultado(data.getValue()) ? "Con resultado" : "Pendiente"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(estado);
                badge.getStyleClass().add("Con resultado".equals(estado) ? "badge-confirmado" : "badge-pendiente");
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        colCosto.setCellValueFactory(data -> new SimpleStringProperty(
                NumericFormatter.formatCurrency(data.getValue().getCosto())));
        colCosto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c);
                setStyle(empty || c == null ? "" : "-fx-font-weight: bold; -fx-text-fill: #27ae60;");
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
                    ExamenLab ex = getTableRow().getItem();
                    if (ex != null) verDetalle(ex);
                });
            }

            @Override
            protected void updateItem(ExamenLab item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });

        colRegistrar.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colRegistrar.setCellFactory(col -> new TableCell<>() {
            private final Button btnResultado = crearChip("Resultado", "action-chip action-chip-editar");
            private final HBox box = new HBox(btnResultado);

            {
                box.setAlignment(Pos.CENTER);
                box.setMaxWidth(Double.MAX_VALUE);
                btnResultado.setOnAction(e -> {
                    ExamenLab ex = getTableRow().getItem();
                    if (ex != null) registrarResultado(ex);
                });
            }

            @Override
            protected void updateItem(ExamenLab item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null || tieneResultado(item) ? null : box);
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

    private boolean tieneResultado(ExamenLab e) {
        return e.getResultado() != null && !e.getResultado().isBlank();
    }

    private void cargarDatos() {
        try {
            todosLosExamenes.clear();
            todosLosExamenes.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar exámenes: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estadoFiltro = cbEstado.getValue();

        List<ExamenLab> filtradas = new ArrayList<>();
        for (ExamenLab e : todosLosExamenes) {
            String estado = tieneResultado(e) ? "Con resultado" : "Pendiente";
            if (estadoFiltro != null && !"Todos los estados".equals(estadoFiltro) && !estadoFiltro.equals(estado)) {
                continue;
            }
            if (!texto.isEmpty()) {
                String busqueda = (
                        (e.getPaciente() != null ? e.getPaciente().getNombre() : "") + " "
                                + (e.getTipoExamen() != null ? e.getTipoExamen() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtradas.add(e);
        }

        tablaExamenes.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        LocalDate hoy = LocalDate.now();
        int conResultado = 0;
        int pendientes = 0;
        int mes = 0;
        for (ExamenLab e : todosLosExamenes) {
            if (tieneResultado(e)) {
                conResultado++;
            } else {
                pendientes++;
            }
            if (e.getFechaHora() != null
                    && e.getFechaHora().getMonth() == hoy.getMonth()
                    && e.getFechaHora().getYear() == hoy.getYear()) {
                mes++;
            }
        }
        lblStatTotal.setText(String.valueOf(todosLosExamenes.size()));
        lblStatConResultado.setText(String.valueOf(conResultado));
        lblStatPendientes.setText(String.valueOf(pendientes));
        lblStatMes.setText(String.valueOf(mes));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoExamen.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Examen de Laboratorio");
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

    private void verDetalle(ExamenLab sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleExamen.fxml"));
            Parent root = loader.load();
            DetalleExamenController ctrl = loader.getController();
            ctrl.setDatos(sel);
            Stage stage = new Stage();
            stage.setTitle("Examen de Laboratorio #" + String.format("%03d", sel.getId()));
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

    private void registrarResultado(ExamenLab sel) {
        final String fieldStyle =
                "-fx-control-inner-background: white; -fx-background-color: white; "
                + "-fx-border-color: #c8ddd1; -fx-border-radius: 8; -fx-border-width: 1.5; "
                + "-fx-font-size: 13px; -fx-padding: 6 10 6 10;";
        final String labelStyle =
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2b87a0;";

        Label lblInfo = new Label("LAB-" + String.format("%03d", sel.getId())
                + "  ·  " + (sel.getTipoExamen() != null ? sel.getTipoExamen() : "")
                + "  ·  " + (sel.getPaciente() != null ? sel.getPaciente().getNombre() : "—"));
        lblInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7f8e;");

        Label lResultado = new Label("INFORME CLÍNICO *");
        lResultado.setStyle(labelStyle);
        TextArea taResultado = new TextArea(sel.getResultado() != null ? sel.getResultado() : "");
        taResultado.setWrapText(true);
        taResultado.setPrefHeight(150);
        taResultado.setPromptText("Escriba el resultado clínico del examen...");
        taResultado.setStyle(fieldStyle);

        Label lObs = new Label("NOTAS ADICIONALES");
        lObs.setStyle(labelStyle);
        TextArea taObs = new TextArea(sel.getObservaciones() != null ? sel.getObservaciones() : "");
        taObs.setWrapText(true);
        taObs.setPrefHeight(80);
        taObs.setPromptText("Observaciones adicionales (opcional)...");
        taObs.setStyle(fieldStyle);

        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.3;");

        VBox content = new VBox(12,
                lblInfo, sep,
                new VBox(6, lResultado, taResultado),
                new VBox(6, lObs, taObs));
        content.setPadding(new Insets(16, 20, 8, 20));
        content.setPrefWidth(480);

        ButtonType btnGuardar = new ButtonType("Guardar Resultado", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Resultado — LAB-" + String.format("%03d", sel.getId()));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        dialog.getDialogPane().setPrefWidth(520);

        Node btnOk = dialog.getDialogPane().lookupButton(btnGuardar);
        btnOk.setStyle("-fx-background-color: #2b87a0; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-font-size: 13px; "
                + "-fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((javafx.scene.layout.Region) btnOk).setPrefHeight(28);
        ((javafx.scene.layout.Region) btnOk).setMinHeight(28);
        ((javafx.scene.layout.Region) btnOk).setMaxHeight(28);
        Node btnCan = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCan.setStyle("-fx-background-color: white; -fx-text-fill: #6b7f8e; "
                + "-fx-border-color: rgba(0,0,0,0.15); -fx-border-radius: 8; -fx-border-width: 1; "
                + "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((javafx.scene.layout.Region) btnCan).setPrefHeight(28);
        ((javafx.scene.layout.Region) btnCan).setMinHeight(28);
        ((javafx.scene.layout.Region) btnCan).setMaxHeight(28);

        btnOk.setDisable(taResultado.getText().trim().isEmpty());
        taResultado.textProperty().addListener((obs, o, n) -> btnOk.setDisable(n.trim().isEmpty()));

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == btnGuardar) {
                sel.setResultado(taResultado.getText().trim());
                sel.setObservaciones(taObs.getText().trim().isEmpty()
                        ? null : taObs.getText().trim());
                try {
                    service.actualizar(sel);
                    cargarDatos();
                } catch (SQLException ex) {
                    mostrarAlerta("Error al guardar resultado: " + ex.getMessage());
                }
            }
        });
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}