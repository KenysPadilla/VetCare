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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Propietario;
import service.PacienteService;
import service.PropietarioService;
import ui.StyleManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PropietariosController implements Initializable {

    private static final String[] AVATAR_COLORS = {
            "#2b87a0", "#27ae60", "#8b5cf6", "#e67e22",
            "#e53e3e", "#4db6d4", "#f5a623", "#2980b9"
    };

    @FXML private TextField txtBuscar;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatConEmail;
    @FXML private Label lblStatConTelefono;
    @FXML private Label lblStatMascotas;
    @FXML private TableView<Propietario> tablaPropietarios;
    @FXML private TableColumn<Propietario, String> colPropietario;
    @FXML private TableColumn<Propietario, String> colTelefono;
    @FXML private TableColumn<Propietario, String> colCorreo;
    @FXML private TableColumn<Propietario, String> colCiudad;
    @FXML private TableColumn<Propietario, String> colMascotas;
    @FXML private TableColumn<Propietario, Propietario> colAcciones;
    @FXML private Button btnEditar;

    private final List<Propietario> todosLosPropietarios = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        tablaPropietarios.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                btnEditar.setDisable(sel == null));
    }

    private void configurarColumnas() {
        colPropietario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCompleto()));
        colPropietario.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                super.updateItem(nombre, empty);
                if (empty || nombre == null) {
                    setGraphic(null);
                    return;
                }
                Propietario p = getTableView().getItems().get(getIndex());
                Label avatar = new Label(iniciales(p));
                avatar.getStyleClass().add("avatar-circle");
                avatar.setStyle("-fx-background-color: " + colorAvatar(getIndex()) + ";");
                Label name = new Label(nombre);
                name.getStyleClass().add("owner-name-cell");
                HBox row = new HBox(10, avatar, name);
                row.setAlignment(Pos.CENTER_LEFT);
                setGraphic(row);
            }
        });

        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCorreo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String email, boolean empty) {
                super.updateItem(email, empty);
                setText(empty || email == null ? null : email);
                setStyle(empty || email == null ? "" : "-fx-text-fill: #6b7f8e;");
            }
        });
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        colMascotas.setCellValueFactory(data -> {
            int count = contarMascotas(data.getValue());
            String texto = count == 1 ? "1 mascota" : count + " mascotas";
            return new SimpleStringProperty(texto);
        });
        colMascotas.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String texto, boolean empty) {
                super.updateItem(texto, empty);
                if (empty || texto == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(texto);
                badge.getStyleClass().add("badge-mascotas");
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
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
                    Propietario p = getTableRow().getItem();
                    if (p != null) verDetalle(p);
                });
            }

            @Override
            protected void updateItem(Propietario item, boolean empty) {
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
        } else {
            btn.getStyleClass().add(cssClass);
        }
        btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        return btn;
    }

    private String iniciales(Propietario p) {
        String nombre = p.getNombre() != null ? p.getNombre().trim() : "";
        String apellido = p.getApellido() != null ? p.getApellido().trim() : "";
        String i1 = nombre.isEmpty() ? "" : nombre.substring(0, 1);
        String i2 = apellido.isEmpty() ? "" : apellido.substring(0, 1);
        return (i1 + i2).toUpperCase();
    }

    private String colorAvatar(int index) {
        return AVATAR_COLORS[Math.floorMod(index, AVATAR_COLORS.length)];
    }

    private int contarMascotas(Propietario p) {
        try {
            return new PacienteService().buscarPorPropietario(p.getCedula()).size();
        } catch (SQLException e) {
            return 0;
        }
    }

    private void cargarDatos() {
        try {
            todosLosPropietarios.clear();
            todosLosPropietarios.addAll(new PropietarioService().listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar propietarios: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<Propietario> filtrados = new ArrayList<>();
        for (Propietario p : todosLosPropietarios) {
            if (!texto.isEmpty()) {
                String busqueda = (p.getNombre() + " " + p.getApellido() + " " + p.getCedula()).toLowerCase();
                if (!busqueda.contains(texto)) continue;
            }
            filtrados.add(p);
        }
        tablaPropietarios.setItems(FXCollections.observableArrayList(filtrados));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int total = todosLosPropietarios.size(), conEmail = 0, conTel = 0, mascotas = 0;
        for (Propietario p : todosLosPropietarios) {
            if (p.getEmail() != null && !p.getEmail().isBlank()) conEmail++;
            if (p.getTelefono() != null && !p.getTelefono().isBlank()) conTel++;
            mascotas += contarMascotas(p);
        }
        lblStatTotal.setText(String.valueOf(total));
        lblStatConEmail.setText(String.valueOf(conEmail));
        lblStatConTelefono.setText(String.valueOf(conTel));
        lblStatMascotas.setText(String.valueOf(mascotas));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoPropietario.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Propietario");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(700);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editarPropietario(Propietario sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoPropietario.fxml"));
            Parent root = loader.load();
            NuevoPropietarioController ctrl = loader.getController();
            ctrl.setModoEdicion(sel);
            Stage stage = new Stage();
            stage.setTitle("Editar Propietario");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(700);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir edición: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verDetalle(Propietario sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detallePropietario.fxml"));
            Parent root = loader.load();
            DetallePropietarioController ctrl = loader.getController();
            ctrl.setPropietario(sel);
            Stage stage = new Stage();
            stage.setTitle("Propietario — " + sel.getNombreCompleto());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(700);
            stage.showAndWait();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditar() {
        Propietario sel = tablaPropietarios.getSelectionModel().getSelectedItem();
        if (sel != null) editarPropietario(sel);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}