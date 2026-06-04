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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Usuario;
import service.UsuarioService;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class UsuariosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbRol;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatActivos;
    @FXML private Label lblStatInactivos;
    @FXML private Label lblStatAdmin;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Usuario> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, String> colEmpleado;
    @FXML private Button btnEditar;

    private final UsuarioService service = new UsuarioService();
    private final List<Usuario> todosLosUsuarios = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbRol.getItems().addAll("Todos los roles", "ADMIN", "VETERINARIO", "ESTILISTA", "RECEPCIONISTA");
        cbRol.getSelectionModel().selectFirst();
        cbRol.setOnAction(e -> aplicarFiltros());
        configurarColumnas();
        cargarDatos();
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                btnEditar.setDisable(sel == null));
    }

    private void configurarColumnas() {
        colUsuario.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colUsuario.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                if (empty || u == null) {
                    setGraphic(null);
                    return;
                }
                Label avatar = new Label(iniciales(u));
                avatar.getStyleClass().add("avatar-circle");
                avatar.setStyle("-fx-background-color: #2b87a0;");
                Label name = new Label(u.getNombreUsuario());
                name.getStyleClass().add("owner-name-cell");
                HBox hbox = new HBox(10, avatar, name);
                hbox.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);
                setGraphic(hbox);
            }
        });


        colRol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getRol() != null ? data.getValue().getRol() : "—"));
        colRol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(rol);
                badge.getStyleClass().add("badge-programada");
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        colCorreo.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getEmail() != null ? data.getValue().getEmail() : "—"));
        colCorreo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String email, boolean empty) {
                super.updateItem(email, empty);
                setText(empty || email == null ? null : email);
                setAlignment(Pos.CENTER_LEFT);
                setStyle(empty || email == null ? "" : "-fx-text-fill: #6b7f8e; -fx-font-size: 11px;");
            }
        });

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().isActivo() ? "Activo" : "Inactivo"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(estado);
                badge.getStyleClass().add("Activo".equals(estado) ? "badge-confirmado" : "badge-inactivo");
                HBox cell = new HBox(badge);
                cell.setAlignment(Pos.CENTER);
                cell.setMaxWidth(Double.MAX_VALUE);
                setGraphic(cell);
            }
        });

        colEmpleado.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getNombreEmpleado() != null ? data.getValue().getNombreEmpleado() : "—"));
        colEmpleado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                super.updateItem(nombre, empty);
                setText(empty || nombre == null ? null : nombre);
                setAlignment(Pos.CENTER_LEFT);
                setStyle(empty || nombre == null || "—".equals(nombre)
                        ? "-fx-text-fill: #6b7f8e;"
                        : "-fx-font-weight: bold; -fx-text-fill: #1a2e3b;");
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

    private String iniciales(Usuario u) {
        String user = u.getNombreUsuario() != null ? u.getNombreUsuario().trim() : "";
        return user.isEmpty() ? "?" : user.substring(0, 1).toUpperCase(Locale.ROOT);
    }

    private void cargarDatos() {
        try {
            todosLosUsuarios.clear();
            todosLosUsuarios.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String rolFiltro = cbRol.getValue();

        List<Usuario> filtrados = new ArrayList<>();
        for (Usuario u : todosLosUsuarios) {
            if (rolFiltro != null && !"Todos los roles".equals(rolFiltro) && !rolFiltro.equals(u.getRol())) {
                continue;
            }
            if (!texto.isEmpty()) {
                String busqueda = (
                        u.getNombreUsuario() + " " + u.getRol() + " "
                                + u.getNombre() + " " + u.getApellido()
                ).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtrados.add(u);
        }

        tablaUsuarios.setItems(FXCollections.observableArrayList(filtrados));
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int activos = 0;
        int inactivos = 0;
        int admin = 0;
        for (Usuario u : todosLosUsuarios) {
            if (u.isActivo()) {
                activos++;
            } else {
                inactivos++;
            }
            if ("ADMIN".equals(u.getRol())) {
                admin++;
            }
        }
        lblStatTotal.setText(String.valueOf(todosLosUsuarios.size()));
        lblStatActivos.setText(String.valueOf(activos));
        lblStatInactivos.setText(String.valueOf(inactivos));
        lblStatAdmin.setText(String.valueOf(admin));
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNuevo() {
        abrirFormulario(null);
    }

    private void editar(Usuario sel) {
        abrirFormulario(sel);
    }

    private void abrirFormulario(Usuario sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoUsuario.fxml"));
            Parent root = loader.load();
            NuevoUsuarioController ctrl = loader.getController();
            if (sel != null) {
                ctrl.setModoEdicion(sel);
            }
            Stage stage = new Stage();
            stage.setTitle(sel == null ? "Nuevo Usuario" : "Editar Usuario");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditar() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel != null) editar(sel);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}