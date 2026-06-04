package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Veterinario;
import service.VeterinarioService;
import ui.ConfirmDialog;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VeterinariosController implements Initializable {

    private static final String[] CARD_COLORS = {
            "#2b87a0", "#27ae60", "#8b5cf6", "#e67e22", "#e53e3e", "#2980b9"
    };

    @FXML private TextField txtBuscar;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatDisponibles;
    @FXML private Label lblStatNoDisponibles;
    @FXML private Label lblStatLicencia;
    @FXML private GridPane cardsContainer;

    private final List<Veterinario> todosLosVeterinarios = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(33.33);
        col.setHgrow(Priority.ALWAYS);
        cardsContainer.getColumnConstraints().addAll(col, col, col);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            todosLosVeterinarios.clear();
            todosLosVeterinarios.addAll(new VeterinarioService().listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar veterinarios: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<Veterinario> filtrados = new ArrayList<>();
        for (Veterinario v : todosLosVeterinarios) {
            if (!texto.isEmpty()) {
                String busqueda = (v.getNombre() + " " + v.getApellido() + " " + v.getEspecialidad()).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtrados.add(v);
        }
        renderCards(filtrados);
        actualizarEstadisticas(todosLosVeterinarios);
    }

    private void actualizarEstadisticas(List<Veterinario> vets) {
        int activos = 0, inactivos = 0, conLicencia = 0;
        for (Veterinario v : vets) {
            if (v.isActivo()) activos++; else inactivos++;
            if (v.getNumeroLicencia() != null && !v.getNumeroLicencia().isBlank()) conLicencia++;
        }
        lblStatTotal.setText(String.valueOf(vets.size()));
        lblStatDisponibles.setText(String.valueOf(activos));
        lblStatNoDisponibles.setText(String.valueOf(inactivos));
        lblStatLicencia.setText(String.valueOf(conLicencia));
    }

    private void renderCards(List<Veterinario> vets) {
        cardsContainer.getChildren().clear();
        for (int i = 0; i < vets.size(); i++) {
            VBox card = crearCard(vets.get(i), CARD_COLORS[i % CARD_COLORS.length]);
            GridPane.setHgrow(card, Priority.ALWAYS);
            cardsContainer.add(card, i % 3, i / 3);
        }
    }

    private VBox crearCard(Veterinario v, String colorBase) {
        boolean inactivo = !v.isActivo();
        String color = inactivo ? "#9e9e9e" : colorBase;

        Region accent = new Region();
        accent.getStyleClass().add("vet-card-accent");
        accent.setStyle("-fx-background-color: " + color + ";");

        Label avatar = new Label(iniciales(v));
        avatar.getStyleClass().add("avatar-circle");
        avatar.setStyle("-fx-background-color: " + color + ";");

        Label nombre = new Label("Dr. " + v.getNombreCompleto());
        nombre.getStyleClass().add("vet-card-name");
        if (inactivo) nombre.setStyle("-fx-text-fill: #9e9e9e;");
        Label especialidad = new Label(v.getEspecialidad() != null ? v.getEspecialidad() : "—");
        especialidad.getStyleClass().add("vet-card-specialty");

        Label estadoBadge = inactivo ? new Label("Inactivo") : new Label("Disponible");
        estadoBadge.getStyleClass().add(inactivo ? "badge-inactivo" : "badge-disponible");
        if (inactivo) estadoBadge.setStyle("-fx-text-fill: #757575; -fx-background-color: #eeeeee; -fx-background-radius: 12; -fx-padding: 3 10 3 10; -fx-font-size: 11px;");

        HBox header = new HBox(12, avatar, wrapVBox(nombre, especialidad), new Region(), estadoBadge);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        Label tel = new Label("📞  " + nullSafe(v.getTelefono()));
        tel.getStyleClass().add("vet-card-contact");
        if (inactivo) tel.setStyle("-fx-text-fill: #bdbdbd;");
        Label mail = new Label("✉  " + nullSafe(v.getEmail()));
        mail.getStyleClass().add("vet-card-contact");
        if (inactivo) mail.setStyle("-fx-text-fill: #bdbdbd;");

        GridPane stats = new GridPane();
        stats.setHgap(8);
        ColumnConstraints col = new ColumnConstraints();
        col.setHgrow(Priority.ALWAYS);
        col.setPercentWidth(33.33);
        stats.getColumnConstraints().addAll(col, col, col);
        stats.add(statBox(nullSafe(v.getNumeroLicencia()), "Licencia"), 0, 0);
        stats.add(statBox(inactivo ? "Inactivo" : "Activo", "Estado"), 1, 0);
        stats.add(statBox("★ 4.8", "Rating"), 2, 0);
        GridPane.setHgrow(stats.getChildren().get(0), Priority.ALWAYS);
        GridPane.setHgrow(stats.getChildren().get(1), Priority.ALWAYS);
        GridPane.setHgrow(stats.getChildren().get(2), Priority.ALWAYS);

        Label horario = new Label("🕐  Lun-Vie 08:00-17:00");
        horario.getStyleClass().add("vet-card-contact");
        if (inactivo) horario.setStyle("-fx-text-fill: #bdbdbd;");

        Button btnVer = new Button("Ver Perfil");
        btnVer.setMaxWidth(Double.MAX_VALUE);
        btnVer.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 3 16 3 16;" +
                "-fx-cursor: hand;");
        btnVer.setOnMouseEntered(e -> btnVer.setOpacity(0.88));
        btnVer.setOnMouseExited(e -> btnVer.setOpacity(1.0));
        btnVer.setOnAction(e -> verPerfil(v));

        Button btnEditar = new Button("Modificar");
        btnEditar.setMaxWidth(Double.MAX_VALUE);
        btnEditar.setDisable(inactivo);
        btnEditar.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 3 16 3 16;" +
                "-fx-cursor: hand;" +
                "-fx-background-insets: 0;");
        btnEditar.setOnMouseEntered(e -> { if (!inactivo) btnEditar.setOpacity(0.80); });
        btnEditar.setOnMouseExited(e -> btnEditar.setOpacity(1.0));
        btnEditar.setOnAction(e -> editarVeterinario(v));

        Button btnAccion;
        if (inactivo) {
            btnAccion = new Button("Reactivar");
            btnAccion.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: #27ae60;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-text-fill: #27ae60;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 3 16 3 16;" +
                    "-fx-cursor: hand;" +
                    "-fx-background-insets: 0;");
            btnAccion.setOnAction(e -> reactivarVeterinario(v));
        } else {
            btnAccion = new Button("Desactivar");
            btnAccion.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: #c0392b;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-text-fill: #c0392b;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 3 16 3 16;" +
                    "-fx-cursor: hand;" +
                    "-fx-background-insets: 0;");
            btnAccion.setOnAction(e -> desactivarVeterinario(v));
        }
        btnAccion.setMaxWidth(Double.MAX_VALUE);
        btnAccion.setOnMouseEntered(e -> btnAccion.setOpacity(0.80));
        btnAccion.setOnMouseExited(e -> btnAccion.setOpacity(1.0));

        for (Button b : new Button[]{btnVer, btnEditar, btnAccion}) {
            b.setPrefHeight(28);
            b.setMinHeight(28);
        }
        HBox actions = new HBox(8, btnVer, btnEditar, btnAccion);
        HBox.setHgrow(btnVer, Priority.ALWAYS);
        HBox.setHgrow(btnEditar, Priority.ALWAYS);
        HBox.setHgrow(btnAccion, Priority.ALWAYS);

        VBox body = new VBox(12, header, tel, mail, stats, horario, actions);
        body.getStyleClass().add("vet-card-body");
        if (inactivo) body.setStyle("-fx-background-color: #fafafa; -fx-opacity: 0.85;");

        VBox card = new VBox(accent, body);
        card.getStyleClass().add("vet-card");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private VBox statBox(String value, String label) {
        Label v = new Label(value);
        v.getStyleClass().add("vet-card-stat-value");
        Label l = new Label(label);
        l.getStyleClass().add("vet-card-stat-label");
        VBox box = new VBox(2, v, l);
        box.getStyleClass().add("vet-card-stat-box");
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox wrapVBox(Label... labels) {
        return new VBox(2, labels);
    }

    private String iniciales(Veterinario v) {
        String n = v.getNombre() != null ? v.getNombre().trim() : "";
        String a = v.getApellido() != null ? v.getApellido().trim() : "";
        return ((n.isEmpty() ? "" : n.substring(0, 1)) + (a.isEmpty() ? "" : a.substring(0, 1))).toUpperCase();
    }

    private String nullSafe(String s) {
        return s != null && !s.isBlank() ? s : "—";
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoVeterinario.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Veterinario");
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

    private void verPerfil(Veterinario sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleVeterinario.fxml"));
            Parent root = loader.load();
            DetalleVeterinarioController ctrl = loader.getController();
            ctrl.setVeterinario(sel);
            Stage stage = new Stage();
            stage.setTitle("Perfil — " + sel.getNombreCompleto());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir perfil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editarVeterinario(Veterinario sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoVeterinario.fxml"));
            Parent root = loader.load();
            NuevoVeterinarioController ctrl = loader.getController();
            ctrl.setModoEdicion(sel);
            Stage stage = new Stage();
            stage.setTitle("Editar Veterinario");
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

    private void desactivarVeterinario(Veterinario v) {
        if (ConfirmDialog.mostrar(
                "Desactivar Veterinario", "⚠",
                "¿Desactivar a " + v.getNombreCompleto() + "?\n"
                        + "Su historial de atenciones se conservará, pero no podrá acceder al sistema.",
                "Desactivar", "#e67e22")) {
            try {
                new VeterinarioService().desactivar(v.getCedula());
                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error al desactivar: " + e.getMessage());
            }
        }
    }

    private void reactivarVeterinario(Veterinario v) {
        if (ConfirmDialog.mostrar(
                "Reactivar Veterinario", "✅",
                "¿Reactivar a " + v.getNombreCompleto() + "?\n"
                        + "Volverá a tener acceso al sistema.",
                "Reactivar", "#27ae60")) {
            try {
                new VeterinarioService().reactivar(v.getCedula());
                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error al reactivar: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Veterinario");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
