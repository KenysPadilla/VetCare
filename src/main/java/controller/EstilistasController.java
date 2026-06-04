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
import model.Estilista;
import service.EstilistaService;
import ui.ConfirmDialog;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EstilistasController implements Initializable {

    private static final String[] CARD_COLORS = {
            "#e67e22", "#8b5cf6", "#e53e3e", "#27ae60"
    };

    @FXML private TextField txtBuscar;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatDisponibles;
    @FXML private Label lblStatEspecialidad;
    @FXML private Label lblStatRating;
    @FXML private GridPane cardsContainer;

    private final List<Estilista> todosLosEstilistas = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        col.setHgrow(Priority.ALWAYS);
        cardsContainer.getColumnConstraints().addAll(col, col);
        cardsContainer.setHgap(16);
        cardsContainer.setVgap(16);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            todosLosEstilistas.clear();
            todosLosEstilistas.addAll(new EstilistaService().listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar estilistas: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<Estilista> filtrados = new ArrayList<>();
        for (Estilista e : todosLosEstilistas) {
            if (!texto.isEmpty()) {
                String busqueda = (e.getNombre() + " " + e.getApellido() + " " + e.getEspecialidadEstetica()).toLowerCase();
                if (!busqueda.contains(texto)) {
                    continue;
                }
            }
            filtrados.add(e);
        }
        renderCards(filtrados);
        actualizarEstadisticas(todosLosEstilistas);
    }

    private void actualizarEstadisticas(List<Estilista> lista) {
        int activos = 0, conEsp = 0;
        for (Estilista e : lista) {
            if (e.isActivo()) activos++;
            if (e.getEspecialidadEstetica() != null && !e.getEspecialidadEstetica().isBlank()) conEsp++;
        }
        lblStatTotal.setText(String.valueOf(lista.size()));
        lblStatDisponibles.setText(String.valueOf(activos));
        lblStatEspecialidad.setText(String.valueOf(conEsp));
        lblStatRating.setText("4.8 ⭐");
    }

    private void renderCards(List<Estilista> lista) {
        cardsContainer.getChildren().clear();
        for (int i = 0; i < lista.size(); i++) {
            VBox card = crearCard(lista.get(i), CARD_COLORS[i % CARD_COLORS.length]);
            GridPane.setHgrow(card, Priority.ALWAYS);
            cardsContainer.add(card, i % 2, i / 2);
        }
    }

    private VBox crearCard(Estilista e, String colorBase) {
        boolean inactivo = !e.isActivo();
        String color = inactivo ? "#9e9e9e" : colorBase;

        Label avatar = new Label(iniciales(e));
        avatar.getStyleClass().add("avatar-circle");
        avatar.setStyle("-fx-background-color: " + color + "; -fx-min-width: 64; -fx-min-height: 64; -fx-max-width: 64; -fx-max-height: 64; -fx-font-size: 18px;");

        Label estadoBadge = inactivo ? new Label("Inactivo") : new Label("Disponible");
        estadoBadge.getStyleClass().add(inactivo ? "badge-inactivo" : "badge-disponible");
        if (inactivo) estadoBadge.setStyle("-fx-text-fill: #757575; -fx-background-color: #eeeeee; -fx-background-radius: 12; -fx-padding: 3 10 3 10; -fx-font-size: 11px;");

        VBox leftCol = new VBox(8, avatar, estadoBadge);
        leftCol.setAlignment(Pos.TOP_CENTER);

        Label nombre = new Label(e.getNombreCompleto());
        nombre.getStyleClass().add("vet-card-name");
        if (inactivo) nombre.setStyle("-fx-text-fill: #9e9e9e;");
        Label especialidad = new Label(e.getEspecialidadEstetica() != null ? e.getEspecialidadEstetica() : "—");
        especialidad.getStyleClass().add("vet-card-specialty");
        Label rating = new Label("⭐ 4.8");
        rating.setStyle("-fx-text-fill: " + (inactivo ? "#bdbdbd" : "#f5a623") + "; -fx-font-weight: bold;");

        HBox header = new HBox(12);
        VBox nameBox = new VBox(2, nombre, especialidad);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(nameBox, spacer, rating);

        Label tel = new Label("📞  " + nullSafe(e.getTelefono()));
        tel.getStyleClass().add("vet-card-contact");
        if (inactivo) tel.setStyle("-fx-text-fill: #bdbdbd;");

        Label exp = new Label("💼  Experiencia profesional");
        exp.getStyleClass().add("vet-card-contact");
        if (inactivo) exp.setStyle("-fx-text-fill: #bdbdbd;");

        HBox chips = new HBox(6);
        for (String s : new String[]{"Baño", "Corte", "Uñas"}) {
            Label chip = new Label(s);
            chip.getStyleClass().add("service-chip");
            chip.setStyle("-fx-background-color: " + color + "18; -fx-text-fill: " + color + ";");
            chips.getChildren().add(chip);
        }

        // expAndChips alineado al 50 % del ancho → coincide con el inicio del botón "Modificar"
        VBox expAndChips = new VBox(8, exp, chips);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(50);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(50);
        GridPane contactRow = new GridPane();
        contactRow.getColumnConstraints().addAll(cc1, cc2);
        contactRow.add(tel, 0, 0);
        contactRow.add(expAndChips, 1, 0);

        VBox rightCol = new VBox(10, header, contactRow);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        HBox body = new HBox(40, leftCol, rightCol);
        body.getStyleClass().add("estilista-card-body");
        body.setStyle("-fx-padding: 20 20 14 20;");
        body.setAlignment(Pos.TOP_LEFT);

        Region sep = new Region();
        sep.setStyle("-fx-background-color: rgba(0,0,0,0.07); -fx-min-height: 1; -fx-max-height: 1;");

        Label horario = new Label("🕐  Lun-Sáb 09:00-18:00");
        horario.getStyleClass().add("vet-card-contact");
        if (inactivo) horario.setStyle("-fx-text-fill: #bdbdbd;");

        Button btnVer = new Button("Ver perfil");
        btnVer.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 3 12 3 12;" +
                "-fx-cursor: hand;");
        btnVer.setOnMouseEntered(e2 -> btnVer.setOpacity(0.88));
        btnVer.setOnMouseExited(e2 -> btnVer.setOpacity(1.0));
        btnVer.setOnAction(ev -> verPerfil(e));

        Button btnAgenda = new Button("Agenda");
        btnAgenda.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 3 12 3 12;" +
                "-fx-cursor: hand;" +
                "-fx-background-insets: 0;");
        btnAgenda.setOnMouseEntered(e2 -> btnAgenda.setOpacity(0.80));
        btnAgenda.setOnMouseExited(e2 -> btnAgenda.setOpacity(1.0));
        btnAgenda.setOnAction(ev -> verAgenda(e));

        Button btnEditar = new Button("Modificar");
        btnEditar.setDisable(inactivo);
        btnEditar.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 3 12 3 12;" +
                "-fx-cursor: hand;" +
                "-fx-background-insets: 0;");
        btnEditar.setOnMouseEntered(e2 -> { if (!inactivo) btnEditar.setOpacity(0.80); });
        btnEditar.setOnMouseExited(e2 -> btnEditar.setOpacity(1.0));
        btnEditar.setOnAction(ev -> editarEstilista(e));

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
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 3 12 3 12;" +
                    "-fx-cursor: hand;" +
                    "-fx-background-insets: 0;");
            btnAccion.setOnAction(ev -> reactivarEstilista(e));
        } else {
            btnAccion = new Button("Desactivar");
            btnAccion.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: #c0392b;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-text-fill: #c0392b;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 3 12 3 12;" +
                    "-fx-cursor: hand;" +
                    "-fx-background-insets: 0;");
            btnAccion.setOnAction(ev -> desactivarEstilista(e));
        }
        btnAccion.setOnMouseEntered(e2 -> btnAccion.setOpacity(0.80));
        btnAccion.setOnMouseExited(e2 -> btnAccion.setOpacity(1.0));

        HBox botonesRow = new HBox(8, btnVer, btnAgenda, btnEditar, btnAccion);
        for (javafx.scene.Node node : botonesRow.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            ((Button) node).setMaxWidth(Double.MAX_VALUE);
            ((Button) node).setPrefHeight(28);
            ((Button) node).setMinHeight(28);
        }

        VBox bottomSection = new VBox(12, horario, botonesRow);
        bottomSection.setStyle("-fx-padding: 12 20 16 20;");

        VBox card = new VBox(body, sep, bottomSection);
        card.getStyleClass().add("estilista-card");
        card.setMaxWidth(Double.MAX_VALUE);
        if (inactivo) card.setStyle("-fx-background-color: #fafafa; -fx-opacity: 0.9;");
        return card;
    }

    private String iniciales(Estilista e) {
        String n = e.getNombre() != null ? e.getNombre().trim() : "";
        String a = e.getApellido() != null ? e.getApellido().trim() : "";
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoEstilista.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Estilista");
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

    private void verPerfil(Estilista sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleEstilista.fxml"));
            Parent root = loader.load();
            DetalleEstilistaController ctrl = loader.getController();
            ctrl.setEstilista(sel);
            Stage stage = new Stage();
            stage.setTitle("Perfil — " + sel.getNombreCompleto());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
        } catch (Exception ex) {
            mostrarAlerta("Error al abrir perfil: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void verAgenda(Estilista sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/agendaEstilista.fxml"));
            Parent root = loader.load();
            AgendaEstilistaController ctrl = loader.getController();
            ctrl.setEstilista(sel);
            Stage stage = new Stage();
            stage.setTitle("Agenda — " + sel.getNombreCompleto());
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
        } catch (Exception ex) {
            mostrarAlerta("Error al abrir agenda: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void editarEstilista(Estilista sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevoEstilista.fxml"));
            Parent root = loader.load();
            NuevoEstilistaController ctrl = loader.getController();
            ctrl.setModoEdicion(sel);
            Stage stage = new Stage();
            stage.setTitle("Editar Estilista");
            Scene scene = new Scene(root);
            StyleManager.apply(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setMaxHeight(720);
            stage.showAndWait();
            cargarDatos();
        } catch (Exception ex) {
            mostrarAlerta("Error al abrir edición: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void desactivarEstilista(Estilista e) {
        if (ConfirmDialog.mostrar(
                "Desactivar Estilista", "⚠",
                "¿Desactivar a " + e.getNombreCompleto() + "?\n"
                        + "Su historial de servicios se conservará, pero no podrá acceder al sistema.",
                "Desactivar", "#e67e22")) {
            try {
                new EstilistaService().desactivar(e.getCedula());
                cargarDatos();
            } catch (SQLException ex) {
                mostrarAlerta("Error al desactivar: " + ex.getMessage());
            }
        }
    }

    private void reactivarEstilista(Estilista e) {
        if (ConfirmDialog.mostrar(
                "Reactivar Estilista", "✅",
                "¿Reactivar a " + e.getNombreCompleto() + "?\n"
                        + "Volverá a tener acceso al sistema.",
                "Reactivar", "#27ae60")) {
            try {
                new EstilistaService().reactivar(e.getCedula());
                cargarDatos();
            } catch (SQLException ex) {
                mostrarAlerta("Error al reactivar: " + ex.getMessage());
            }
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Estilista");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}