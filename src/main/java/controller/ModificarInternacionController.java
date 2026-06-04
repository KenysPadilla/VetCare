package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Internacion;
import model.InternacionMedicamento;
import model.Medicamento;
import service.InternacionService;
import service.MedicamentoService;
import ui.NumericFormatter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ModificarInternacionController implements Initializable {

    @FXML private Label    lblHeaderSubtitle;
    @FXML private Label    lblPaciente;
    @FXML private Label    lblVeterinario;
    @FXML private Label    lblIngreso;
    @FXML private TextArea txtMotivo;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtObservaciones;
    @FXML private TextField txtBuscarMed;
    @FXML private ListView<Medicamento>           lvMedicamentos;
    @FXML private TextField txtCantidad;
    @FXML private DatePicker dpFechaAplicacion;
    @FXML private TextField txtDosis;
    @FXML private ListView<InternacionMedicamento> lvAdministrados;
    @FXML private Label    lblMensaje;

    private Internacion internacion;
    private Runnable onGuardado;
    private List<Medicamento> todosMedicamentos = new ArrayList<>();
    private final ObservableList<InternacionMedicamento> administrados =
            FXCollections.observableArrayList();

    private static final DateTimeFormatter FECHA_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lvMedicamentos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Medicamento m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setText(null); return; }
                String conc = (m.getConcentracion() != null && !m.getConcentracion().isBlank())
                        ? " [" + m.getConcentracion() + "]" : "";
                setText(m.getNombre() + conc + " — " + m.getFabricante()
                        + "  (" + NumericFormatter.formatCurrency(m.getPrecio()) + ")");
            }
        });

        lvAdministrados.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(InternacionMedicamento im, boolean empty) {
                super.updateItem(im, empty);
                setText(empty || im == null ? null : im.toString());
            }
        });

        lvAdministrados.setItems(administrados);
        dpFechaAplicacion.setValue(LocalDate.now());
        txtCantidad.setText("1");

        try {
            todosMedicamentos = new MedicamentoService().listarTodos();
            lvMedicamentos.getItems().setAll(todosMedicamentos);
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar medicamentos: " + e.getMessage(), "#D32F2F");
        }

        txtBuscarMed.textProperty().addListener((obs, old, val) -> {
            String f = val == null ? "" : val.trim().toLowerCase();
            if (f.isEmpty()) {
                lvMedicamentos.getItems().setAll(todosMedicamentos);
            } else {
                List<Medicamento> filtrados = new ArrayList<>();
                for (Medicamento m : todosMedicamentos) {
                    if (m.getNombre().toLowerCase().contains(f)) filtrados.add(m);
                }
                lvMedicamentos.getItems().setAll(filtrados);
            }
        });
    }

    public void setInternacion(Internacion i) {
        this.internacion = i;
        lblHeaderSubtitle.setText(String.format("I-%03d", i.getId()) + " — "
                + (i.getPaciente() != null ? i.getPaciente().getNombre() : "?"));

        String propietario = (i.getPaciente() != null && i.getPaciente().getPropietario() != null)
                ? "  /  " + i.getPaciente().getPropietario().getNombreCompleto() : "";
        lblPaciente.setText(i.getPaciente() != null
                ? i.getPaciente().getNombre() + propietario : "—");
        lblVeterinario.setText(i.getVeterinario() != null
                ? i.getVeterinario().getNombreCompleto() : "—");
        lblIngreso.setText(i.getFechaHoraIngreso() != null
                ? i.getFechaHoraIngreso().format(FECHA_FMT) : "—");

        txtMotivo.setText(i.getMotivo() != null ? i.getMotivo() : "");
        txtDiagnostico.setText(i.getDiagnostico() != null ? i.getDiagnostico() : "");
        txtObservaciones.setText(i.getObservaciones() != null ? i.getObservaciones() : "");

        try {
            List<InternacionMedicamento> existentes =
                    new InternacionService().listarMedicamentosPorInternacion(i.getId());
            administrados.setAll(existentes);
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar medicamentos de la internación.", "#D32F2F");
        }
    }

    public void setOnGuardado(Runnable callback) {
        this.onGuardado = callback;
    }

    @FXML
    private void handleAgregarMedicamento() {
        Medicamento sel = lvMedicamentos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarMensaje("Seleccione un medicamento de la lista.", "#D32F2F");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarMensaje("La cantidad debe ser un número entero positivo.", "#D32F2F");
            return;
        }

        LocalDate fecha = dpFechaAplicacion.getValue();
        if (fecha == null) {
            mostrarMensaje("Seleccione la fecha de aplicación.", "#D32F2F");
            return;
        }

        InternacionMedicamento im = new InternacionMedicamento();
        im.setInternacion(internacion);
        im.setMedicamento(sel);
        im.setCantidad(cantidad);
        im.setFechaAplicacion(fecha);
        im.setDosis(txtDosis.getText().trim());
        administrados.add(im);

        txtCantidad.setText("1");
        txtDosis.clear();
        lvMedicamentos.getSelectionModel().clearSelection();
        mostrarMensaje("", "#000000");
    }

    @FXML
    private void handleQuitarMedicamento() {
        int idx = lvAdministrados.getSelectionModel().getSelectedIndex();
        if (idx >= 0) administrados.remove(idx);
    }

    @FXML
    private void handleGuardar() {
        internacion.setMotivo(txtMotivo.getText().trim());
        internacion.setDiagnostico(txtDiagnostico.getText().trim());
        internacion.setObservaciones(txtObservaciones.getText().trim());

        try {
            InternacionService svc = new InternacionService();
            svc.actualizar(internacion);
            svc.guardarMedicamentosInternacion(internacion.getId(),
                    new ArrayList<>(administrados));
            if (onGuardado != null) onGuardado.run();
            cerrar();
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txtMotivo.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }
}