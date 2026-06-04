package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.SolicitudCita;
import service.FirebaseService;
import service.SolicitudCitaService;
import ui.StyleManager;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SolicitudesController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TableView<SolicitudCita> tablaSolicitudes;
    @FXML private TableColumn<SolicitudCita, String> colNombre;
    @FXML private TableColumn<SolicitudCita, String> colTelefono;
    @FXML private TableColumn<SolicitudCita, String> colCorreo;
    @FXML private TableColumn<SolicitudCita, String> colMascota;
    @FXML private TableColumn<SolicitudCita, String> colEspecie;
    @FXML private TableColumn<SolicitudCita, String> colRaza;
    @FXML private TableColumn<SolicitudCita, String> colMotivo;
    @FXML private TableColumn<SolicitudCita, String> colFecha;
    @FXML private TableColumn<SolicitudCita, String> colHora;
    @FXML private TableColumn<SolicitudCita, String> colFechaSolicitud;
    @FXML private TableColumn<SolicitudCita, Void> colAcciones;
    @FXML private Button btnAceptar;
    @FXML private Button btnRechazar;
    @FXML private Label lblMensaje;
    @FXML private Label lblInfo;
    @FXML private Label lblStatPendientes;
    @FXML private Label lblStatHoy;
    @FXML private Label lblStatSemana;
    @FXML private Label lblStatMascotas;

    private final SolicitudCitaService servicio = new SolicitudCitaService();
    private final FirebaseService firebaseService = new FirebaseService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombrePropietario"));
        colNombre.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String n, boolean empty) {
                super.updateItem(n, empty);
                setText(empty || n == null ? null : n);
                setStyle(empty || n == null ? "" : "-fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
            }
        });

        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCorreo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c);
                setStyle(empty || c == null ? "" : "-fx-text-fill: #6b7f8e; -fx-font-size: 11px;");
            }
        });

        colMascota.setCellValueFactory(new PropertyValueFactory<>("nombreMascota"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colEspecie.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e);
                setStyle(empty || e == null ? "" : "-fx-text-fill: #2b87a0;");
            }
        });

        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colMotivo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) {
                    setText(null);
                    return;
                }
                setText(m.length() > 24 ? m.substring(0, 22) + "…" : m);
            }
        });

        colFecha.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFecha() != null ? data.getValue().getFecha().format(FECHA_FMT) : "—"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colFechaSolicitud.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaSolicitud() != null
                        ? data.getValue().getFechaSolicitud().toLocalDate().format(FECHA_FMT) : "—"));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Ver");
            {
                String estilo = ui.StyleManager.chipStyle("action-chip action-chip-ver");
                if (estilo != null) {
                    btn.setStyle(estilo);
                    ui.StyleManager.applyHover(btn, "action-chip action-chip-ver");
                } else {
                    btn.getStyleClass().addAll("action-chip", "action-chip-ver");
                }
                btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                btn.setOnAction(e -> {
                    SolicitudCita s = getTableRow().getItem();
                    if (s != null) verDetalle(s);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        cargarDatos();
    }

    @FXML
    private void handleActualizar() {
        Thread hilo = new Thread(() -> {
            try {
                firebaseService.importarSolicitudesNuevas();
            } catch (Exception ignored) {

            }
            Platform.runLater(this::cargarDatos);
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    @FXML
    private void handleAceptar() {
        SolicitudCita sel = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarMensaje("Selecciona una solicitud de la tabla.", "#D32F2F");
            return;
        }

        btnAceptar.setDisable(true);
        btnRechazar.setDisable(true);
        mostrarMensaje("Procesando...", "#333333");

        Thread hilo = new Thread(() -> {
            try {
                boolean correoEnviado = servicio.aceptarSolicitud(sel);
                Platform.runLater(() -> {
                    if (correoEnviado) {
                        mostrarMensaje("✓ Cita aceptada y confirmación enviada a " + sel.getCorreo(), "#1B6B2F");
                    } else {
                        mostrarMensaje("✓ Cita aceptada. ⚠ Correo no enviado — verifique mail.password en config.properties", "#D97706");
                    }
                    cargarDatos();
                    btnAceptar.setDisable(false);
                    btnRechazar.setDisable(false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    mostrarMensaje("Error: " + ex.getMessage(), "#D32F2F");
                    btnAceptar.setDisable(false);
                    btnRechazar.setDisable(false);
                });
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    @FXML
    private void handleRechazar() {
        SolicitudCita sel = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarMensaje("Selecciona una solicitud de la tabla.", "#D32F2F");
            return;
        }

        btnAceptar.setDisable(true);
        btnRechazar.setDisable(true);
        mostrarMensaje("Rechazando...", "#333333");

        Thread hilo = new Thread(() -> {
            try {
                boolean correoEnviado = servicio.rechazarSolicitud(sel);
                Platform.runLater(() -> {
                    if (correoEnviado) {
                        mostrarMensaje("Solicitud rechazada. Correo enviado a " + sel.getCorreo(), "#888888");
                    } else {
                        mostrarMensaje("Solicitud rechazada. ⚠ Correo no enviado — verifique mail.password", "#D97706");
                    }
                    cargarDatos();
                    btnAceptar.setDisable(false);
                    btnRechazar.setDisable(false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    mostrarMensaje("Error: " + ex.getMessage(), "#D32F2F");
                    btnAceptar.setDisable(false);
                    btnRechazar.setDisable(false);
                });
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private void cargarDatos() {
        Thread hilo = new Thread(() -> {
            try {
                ArrayList<SolicitudCita> lista = servicio.listarPendientes();
                Platform.runLater(() -> {
                    tablaSolicitudes.setItems(FXCollections.observableArrayList(lista));
                    lblInfo.setText("Solicitudes pendientes: " + lista.size());
                    actualizarEstadisticas(lista);
                });
            } catch (Exception ex) {
                Platform.runLater(() ->
                        mostrarMensaje("Error al cargar solicitudes: " + ex.getMessage(), "#D32F2F"));
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private void actualizarEstadisticas(ArrayList<SolicitudCita> lista) {
        LocalDate hoy = LocalDate.now();
        int citasHoy = 0;
        int semana = 0;
        int conMascota = 0;
        for (SolicitudCita s : lista) {
            if (s.getNombreMascota() != null && !s.getNombreMascota().isBlank()) {
                conMascota++;
            }
            if (s.getFecha() != null) {
                if (s.getFecha().equals(hoy)) {
                    citasHoy++;
                }
                if (!s.getFecha().isBefore(hoy) && !s.getFecha().isAfter(hoy.plusDays(7))) {
                    semana++;
                }
            }
        }
        lblStatPendientes.setText(String.valueOf(lista.size()));
        lblStatHoy.setText(String.valueOf(citasHoy));
        lblStatSemana.setText(String.valueOf(semana));
        lblStatMascotas.setText(String.valueOf(conMascota));
    }

    private void verDetalle(SolicitudCita s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleSolicitud.fxml"));
            Parent root = loader.load();
            DetalleSolicitudController ctrl = loader.getController();
            ctrl.setSolicitud(s);
            Stage stage = new Stage();
            stage.setTitle("Detalle — Solicitud de " + (s.getNombrePropietario() != null ? s.getNombrePropietario() : ""));
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

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}
