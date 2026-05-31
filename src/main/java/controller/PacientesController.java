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
import model.Paciente;
import model.Propietario;
import service.PacienteService;
import ui.ModalHelper;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PacientesController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEspecie;
    @FXML private Label lblFiltro;
    @FXML private Button btnVerTodos;
    @FXML private Button btnNuevo;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatPerros;
    @FXML private Label lblStatGatos;
    @FXML private Label lblStatSinPropietario;
    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, String> colPaciente;
    @FXML private TableColumn<Paciente, Paciente> colEspecieRaza;
    @FXML private TableColumn<Paciente, String> colSexo;
    @FXML private TableColumn<Paciente, Paciente> colEdadPeso;
    @FXML private TableColumn<Paciente, String> colPropietario;
    @FXML private TableColumn<Paciente, String> colVeterinario;
    @FXML private TableColumn<Paciente, String> colUltimaVisita;
    @FXML private TableColumn<Paciente, Paciente> colAcciones;
    @FXML private Button btnEditar;

    private final List<Paciente> todosLosPacientes = new ArrayList<>();
    private boolean modoFiltroPropietario;
    private String cedulaPropietarioFiltro;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEspecie.getItems().addAll("Todas las especies", "Perro", "Gato");
        cbEspecie.getSelectionModel().selectFirst();
        cbEspecie.setOnAction(e -> aplicarFiltros());

        configurarColumnas();
        cargarDatos();
        tablaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                btnEditar.setDisable(sel == null));
    }

    private void configurarColumnas() {
        colPaciente.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colPaciente.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                super.updateItem(nombre, empty);
                if (empty || nombre == null) {
                    setGraphic(null);
                    return;
                }
                Paciente p = getTableView().getItems().get(getIndex());
                Label emoji = new Label(emojiEspecie(p.getEspecie()));
                emoji.getStyleClass().add("species-emoji");
                Label name = new Label(nombre);
                name.getStyleClass().add("owner-name-cell");
                HBox row = new HBox(8, emoji, name);
                row.setAlignment(Pos.CENTER_LEFT);
                setGraphic(row);
            }
        });

        colEspecieRaza.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colEspecieRaza.setCellFactory(col -> celdaDobleLinea(
                p -> p.getEspecie() != null ? p.getEspecie() : "—",
                p -> p.getRaza() != null ? p.getRaza() : "—",
                true));

        colSexo.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getSexo() != null ? data.getValue().getSexo() : "—"));

        colEdadPeso.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colEdadPeso.setCellFactory(col -> celdaDobleLinea(
                p -> p.calcularEdad() + " años",
                p -> String.format("%.1f kg", p.getPeso()),
                false));

        colPropietario.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPropietario() != null
                        ? data.getValue().getPropietario().getNombreCompleto()
                        : "Sin propietario"));

        colVeterinario.setCellValueFactory(data -> new SimpleStringProperty("Sin asignar"));
        colVeterinario.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String vet, boolean empty) {
                super.updateItem(vet, empty);
                setText(empty || vet == null ? null : vet);
                setStyle(empty || vet == null ? "" : "-fx-text-fill: #6b7f8e;");
            }
        });

        colUltimaVisita.setCellValueFactory(data -> new SimpleStringProperty("—"));
        colUltimaVisita.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : fecha);
                setStyle(empty || fecha == null ? "" : "-fx-text-fill: #6b7f8e;");
            }
        });

        colAcciones.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnHistoria = crearChip("Ver", "action-chip action-chip-historia");
            private final HBox box = new HBox(btnHistoria);

            {
                box.setAlignment(Pos.CENTER);
                box.setMaxWidth(Double.MAX_VALUE);
                btnHistoria.setOnAction(e -> {
                    Paciente p = getTableRow().getItem();
                    if (p != null) abrirHistorial(p);
                });
            }

            @Override
            protected void updateItem(Paciente item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });
    }

    private TableCell<Paciente, Paciente> celdaDobleLinea(
            java.util.function.Function<Paciente, String> linea1,
            java.util.function.Function<Paciente, String> linea2,
            boolean boldFirst) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Paciente p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setGraphic(null);
                    return;
                }
                Label l1 = new Label(linea1.apply(p));
                l1.getStyleClass().add(boldFirst ? "cell-maintext" : "cell-maintext");
                if (boldFirst) {
                    l1.setStyle("-fx-font-weight: bold;");
                }
                Label l2 = new Label(linea2.apply(p));
                l2.getStyleClass().add("cell-subtext");
                VBox box = new VBox(2, l1, l2);
                setGraphic(box);
            }
        };
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

    private String emojiEspecie(String especie) {
        if (especie == null) {
            return "🐾";
        }
        String e = especie.toLowerCase();
        if (e.contains("perro") || e.contains("canin")) {
            return "🐕";
        }
        if (e.contains("gato") || e.contains("felin")) {
            return "🐈";
        }
        return "🐾";
    }

    private boolean esPerro(String especie) {
        if (especie == null) {
            return false;
        }
        String e = especie.toLowerCase();
        return e.contains("perro") || e.contains("canin");
    }

    private boolean esGato(String especie) {
        if (especie == null) {
            return false;
        }
        String e = especie.toLowerCase();
        return e.contains("gato") || e.contains("felin");
    }

    private void cargarDatos() {
        try {
            todosLosPacientes.clear();
            if (modoFiltroPropietario && cedulaPropietarioFiltro != null) {
                todosLosPacientes.addAll(
                        new PacienteService().buscarPorPropietario(cedulaPropietarioFiltro));
            } else {
                todosLosPacientes.addAll(new PacienteService().listarTodos());
            }
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar pacientes: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String especieFiltro = cbEspecie.getValue();

        List<Paciente> filtrados = new ArrayList<>();
        for (Paciente p : todosLosPacientes) {
            if (!texto.isEmpty()) {
                String busqueda = (p.getNombre() + " " + (p.getMicrochip() != null ? p.getMicrochip() : ""))
                        .toLowerCase();
                if (!busqueda.contains(texto)) continue;
            }
            if ("Perro".equals(especieFiltro) && !esPerro(p.getEspecie())) continue;
            if ("Gato".equals(especieFiltro) && !esGato(p.getEspecie())) continue;
            filtrados.add(p);
        }

        tablaPacientes.setItems(FXCollections.observableArrayList(filtrados));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int total = todosLosPacientes.size(), perros = 0, gatos = 0, sinPropietario = 0;
        for (Paciente p : todosLosPacientes) {
            if (esPerro(p.getEspecie())) perros++;
            if (esGato(p.getEspecie())) gatos++;
            if (p.getPropietario() == null) sinPropietario++;
        }
        lblStatTotal.setText(String.valueOf(total));
        lblStatPerros.setText(String.valueOf(perros));
        lblStatGatos.setText(String.valueOf(gatos));
        lblStatSinPropietario.setText(String.valueOf(sinPropietario));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoPaciente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Paciente");
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

    private void editarPaciente(Paciente sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoPaciente.fxml"));
            Parent root = loader.load();
            NuevoPacienteController ctrl = loader.getController();
            ctrl.setModoEdicion(sel);
            Stage stage = new Stage();
            stage.setTitle("Editar Paciente");
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

    public void setFiltrarPorPropietario(Propietario propietario) {
        modoFiltroPropietario = true;
        cedulaPropietarioFiltro = propietario.getCedula();
        lblFiltro.setText("Mascotas de: " + propietario.getNombreCompleto());
        lblFiltro.setVisible(true);
        lblFiltro.setManaged(true);
        // En modo sub-vista solo mostramos la lista, sin botones de acción
        btnVerTodos.setVisible(false);
        btnVerTodos.setManaged(false);
        btnNuevo.setVisible(false);
        btnNuevo.setManaged(false);
        cargarDatos();
    }

    @FXML
    private void handleVerTodos() {
        modoFiltroPropietario = false;
        cedulaPropietarioFiltro = null;
        lblFiltro.setVisible(false);
        lblFiltro.setManaged(false);
        btnVerTodos.setVisible(false);
        btnVerTodos.setManaged(false);
        cargarDatos();
    }

    private void abrirHistorial(Paciente sel) {
        try {
            ModalHelper.open("/fxml/historialClinico.fxml",
                    "Historial Clínico — " + sel.getNombre(),
                    (HistorialClinicoController ctrl) -> ctrl.setPaciente(sel));
        } catch (Exception e) {
            mostrarAlerta("Error al abrir historial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditar() {
        Paciente sel = tablaPacientes.getSelectionModel().getSelectedItem();
        if (sel != null) editarPaciente(sel);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}