package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import model.Internacion;
import service.InternacionService;
import ui.ConfirmDialog;
import ui.StyleManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class InternacionesController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private enum EstadoClinico {
        CRITICO    ("⚠",  "Crítico",    "#ef4444", "#fff5f5", "rgba(239,68,68,0.12)",   "rgba(239,68,68,0.30)"),
        EVALUACION ("⏱",  "Evaluación", "#f59e0b", "#fffbeb", "rgba(245,158,11,0.12)",  "rgba(245,158,11,0.30)"),
        ESTABLE    ("⏱",  "Estable",    "#2b87a0", "#eff8ff", "rgba(43,135,160,0.12)",  "rgba(43,135,160,0.30)"),
        LISTA_ALTA ("✓",  "Lista alta", "#8b5cf6", "#f5f3ff", "rgba(139,92,246,0.12)",  "rgba(139,92,246,0.30)"),
        EGRESADO   ("✓",  "Egresado",   "#22c55e", "#f0fdf4", "rgba(34,197,94,0.12)",   "rgba(34,197,94,0.30)");

        final String icon, label, color, headerBg, badgeBg, borderColor;
        EstadoClinico(String icon, String label, String color,
                      String headerBg, String badgeBg, String borderColor) {
            this.icon = icon; this.label = label; this.color = color;
            this.headerBg = headerBg; this.badgeBg = badgeBg; this.borderColor = borderColor;
        }
    }

    private EstadoClinico calcularEstadoClinico(Internacion i) {
        if (i.getFechaHoraEgreso() != null) return EstadoClinico.EGRESADO;
        long dias = diasEstadia(i);
        if (dias == 0)  return EstadoClinico.EVALUACION;
        if (dias <= 3)  return EstadoClinico.CRITICO;
        if (dias <= 7)  return EstadoClinico.ESTABLE;
        return EstadoClinico.LISTA_ALTA;
    }

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Label lblStatTotal;
    @FXML private Label lblStatInternados;
    @FXML private Label lblStatEgresados;
    @FXML private Label lblStatDias;
    @FXML private GridPane cardsContainer;

    private final InternacionService service = new InternacionService();
    private final List<Internacion> todasLasInternaciones = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        col.setHgrow(Priority.ALWAYS);
        cardsContainer.getColumnConstraints().addAll(col, col);

        cbEstado.getItems().addAll("Todos los estados", "Internado", "Egresado");
        cbEstado.getSelectionModel().selectFirst();
        cbEstado.setOnAction(e -> aplicarFiltros());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            todasLasInternaciones.clear();
            todasLasInternaciones.addAll(service.listarTodos());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar internaciones: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estadoFiltro = cbEstado.getValue();

        List<Internacion> filtradas = new ArrayList<>();
        for (Internacion i : todasLasInternaciones) {
            String estado = estadoInternacion(i);
            if (estadoFiltro != null && !"Todos los estados".equals(estadoFiltro) && !estadoFiltro.equals(estado)) continue;
            if (!texto.isEmpty()) {
                String busqueda = (
                        (i.getPaciente() != null ? i.getPaciente().getNombre() : "") + " " +
                        (i.getMotivo() != null ? i.getMotivo() : "") + " " +
                        (i.getVeterinario() != null ? i.getVeterinario().getNombreCompleto() : "") + " " +
                        (i.getPaciente() != null && i.getPaciente().getPropietario() != null
                                ? i.getPaciente().getPropietario().getNombreCompleto() : "")
                ).toLowerCase();
                if (!busqueda.contains(texto)) continue;
            }
            filtradas.add(i);
        }
        renderCards(filtradas);
        actualizarEstadisticas();
    }

    private void renderCards(List<Internacion> lista) {
        cardsContainer.getChildren().clear();
        for (int idx = 0; idx < lista.size(); idx++) {
            VBox card = crearCard(lista.get(idx));
            GridPane.setHgrow(card, Priority.ALWAYS);
            cardsContainer.add(card, idx % 2, idx / 2);
        }
    }

    private VBox crearCard(Internacion i) {
        boolean internado = i.getFechaHoraEgreso() == null;
        EstadoClinico ec  = calcularEstadoClinico(i);

        String colorBase   = ec.color;
        String headerBg    = ec.headerBg;
        String badgeBg     = ec.badgeBg;
        String borderColor = ec.borderColor;
        String stateIcon   = ec.icon;
        String stateText   = ec.label;

        // ── Header ──────────────────────────────────────────────────────────
        Label iconLbl = new Label(stateIcon);
        iconLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + colorBase + ";");

        Label badgeEstado = new Label(stateText);
        badgeEstado.setStyle(
                "-fx-background-color: " + badgeBg + ";" +
                "-fx-text-fill: " + colorBase + ";" +
                "-fx-font-size: 12px; -fx-font-weight: bold;" +
                "-fx-background-radius: 999; -fx-padding: 3 10 3 6;");

        HBox statusGroup = new HBox(5, iconLbl, badgeEstado);
        statusGroup.setAlignment(Pos.CENTER_LEFT);

        Region spacerH = new Region();
        HBox.setHgrow(spacerH, Priority.ALWAYS);

        Label lblId = new Label(String.format("INT-%03d", i.getId()));
        lblId.setStyle(
                "-fx-font-family: 'Consolas'; -fx-font-size: 12px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + colorBase + ";" +
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 6; -fx-padding: 2 8 2 8;" +
                "-fx-border-color: rgba(0,0,0,0.09); -fx-border-radius: 6; -fx-border-width: 1;");

        long dias = diasEstadia(i);
        Label lblDias = new Label(dias + (dias == 1 ? " día" : " días"));
        lblDias.setStyle(
                "-fx-font-size: 12px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + colorBase + ";" +
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 999; -fx-padding: 3 10 3 10;" +
                "-fx-border-color: rgba(0,0,0,0.09); -fx-border-radius: 999; -fx-border-width: 1;");

        HBox header = new HBox(8, statusGroup, spacerH, lblId, lblDias);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(7, 16, 7, 16));
        header.setStyle("-fx-background-color: " + headerBg + "; -fx-background-radius: 10.5 10.5 0 0;");

        // ── Fila paciente ────────────────────────────────────────────────────
        String especie = (i.getPaciente() != null && i.getPaciente().getEspecie() != null)
                ? i.getPaciente().getEspecie() : "";
        HBox emojiLbl = crearIconoAnimal(especie);

        Label lblNombrePac = new Label(i.getPaciente() != null ? safe(i.getPaciente().getNombre()) : "—");
        lblNombrePac.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a2e3b;");

        String propNombre = (i.getPaciente() != null && i.getPaciente().getPropietario() != null)
                ? i.getPaciente().getPropietario().getNombreCompleto() : "—";
        Label lblProp = new Label(propNombre);
        lblProp.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7f8e;");

        VBox patInfo = new VBox(2, lblNombrePac, lblProp);

        Region spacerPat = new Region();
        HBox.setHgrow(spacerPat, Priority.ALWAYS);

        Label lblDesde = new Label("Desde: " + (i.getFechaHoraIngreso() != null
                ? i.getFechaHoraIngreso().format(FECHA_FMT) : "—"));
        lblDesde.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7f8e;");
        VBox desdeBox = new VBox(lblDesde);
        desdeBox.setAlignment(Pos.TOP_RIGHT);

        HBox patRow = new HBox(10, emojiLbl, patInfo, spacerPat, desdeBox);
        patRow.setAlignment(Pos.CENTER_LEFT);

        // ── Motivo (fondo grisáceo) ───────────────────────────────────────────
        Label lblMotivoHdr = new Label("MOTIVO");
        lblMotivoHdr.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #9eaab5;");
        Label lblMotivoVal = new Label(safe(i.getMotivo()));
        lblMotivoVal.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a2e3b;");
        lblMotivoVal.setWrapText(true);
        VBox motivoBox = new VBox(4, lblMotivoHdr, lblMotivoVal);
        motivoBox.setStyle(
                "-fx-background-color: #f5f7fa;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 12 10 12;");

        // ── Grid info (fondo grisáceo): VETERINARIO | DIAGNÓSTICO | OBSERVACIONES
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        cc.setPercentWidth(33.33);
        infoGrid.getColumnConstraints().addAll(cc, cc, cc);

        String vetNombre = i.getVeterinario() != null ? i.getVeterinario().getNombreCompleto() : "—";
        infoGrid.add(infoCell("VETERINARIO",   vetNombre),                       0, 0);
        infoGrid.add(infoCell("DIAGNÓSTICO",   truncar(i.getDiagnostico(), 28)), 1, 0);
        infoGrid.add(infoCell("OBSERVACIONES", truncar(i.getObservaciones(), 28)), 2, 0);

        VBox infoBox = new VBox(infoGrid);
        infoBox.setStyle(
                "-fx-background-color: #f5f7fa;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 12 10 12;");

        // ── Cuerpo ───────────────────────────────────────────────────────────
        VBox body = new VBox(10, patRow, motivoBox, infoBox);
        body.setPadding(new Insets(14, 16, 12, 16));

        // ── Botones ──────────────────────────────────────────────────────────
        Button btnVer = new Button("Ver Historia");
        btnVer.setMaxWidth(Double.MAX_VALUE);
        btnVer.setPrefHeight(28);
        btnVer.setMinHeight(28);
        btnVer.setMaxHeight(28);
        HBox.setHgrow(btnVer, Priority.ALWAYS);
        btnVer.setStyle(
                "-fx-background-color: #2b87a0; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: transparent; -fx-border-width: 1.5; -fx-border-radius: 8;" +
                "-fx-padding: 0; -fx-cursor: hand;");
        btnVer.setOnMouseEntered(e -> btnVer.setOpacity(0.88));
        btnVer.setOnMouseExited(e -> btnVer.setOpacity(1.0));
        btnVer.setOnAction(e -> verDetalle(i));

        HBox btnRow = new HBox(10, btnVer);
        btnRow.setAlignment(Pos.CENTER);

        if (internado) {
            Button btnAlta = new Button("Dar de Alta");
            btnAlta.setMaxWidth(Double.MAX_VALUE);
            btnAlta.setPrefHeight(28);
            btnAlta.setMinHeight(28);
            btnAlta.setMaxHeight(28);
            HBox.setHgrow(btnAlta, Priority.ALWAYS);
            btnAlta.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: #22c55e; -fx-border-width: 1.5;" +
                    "-fx-border-radius: 8; -fx-background-radius: 8;" +
                    "-fx-text-fill: #22c55e;" +
                    "-fx-font-size: 13px; -fx-font-weight: bold;" +
                    "-fx-padding: 0; -fx-cursor: hand; -fx-background-insets: 0;");
            btnAlta.setOnMouseEntered(e -> btnAlta.setOpacity(0.80));
            btnAlta.setOnMouseExited(e -> btnAlta.setOpacity(1.0));
            btnAlta.setOnAction(e -> darEgreso(i));
            btnRow.getChildren().add(btnAlta);
        }

        VBox footer = new VBox(btnRow);
        footer.setPadding(new Insets(0, 16, 14, 16));

        // ── Card ─────────────────────────────────────────────────────────────
        String baseStyle =
                "-fx-background-color: white; -fx-background-radius: 12;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-radius: 12; -fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(26,46,59,0.07), 14, 0, 0, 4);";
        String hoverStyle =
                "-fx-background-color: white; -fx-background-radius: 12;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-radius: 12; -fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(26,46,59,0.14), 20, 0, 0, 7);";

        VBox card = new VBox(header, body, footer);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));
        return card;
    }

    private VBox infoCell(String headerText, String value) {
        Label hdr = new Label(headerText);
        hdr.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #9eaab5;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 12px; -fx-text-fill: #1a2e3b;");
        val.setWrapText(true);
        return new VBox(3, hdr, val);
    }

    private HBox crearIconoAnimal(String especie) {
        String lit = "fas-paw";
        if (especie != null) {
            String e = especie.toLowerCase();
            if (e.contains("canino") || e.contains("perro"))       lit = "fas-dog";
            else if (e.contains("felino") || e.contains("gato"))   lit = "fas-cat";
            else if (e.contains("ave") || e.contains("aviar"))     lit = "fas-dove";
            else if (e.contains("pez")  || e.contains("peces"))    lit = "fas-fish";
            else if (e.contains("reptil"))                         lit = "fas-frog";
            else if (e.contains("conejo"))                         lit = "fas-paw";
            else if (e.contains("caballo") || e.contains("equino")) lit = "fas-horse";
        }
        FontIcon icon = new FontIcon(lit);
        icon.setIconSize(26);
        icon.setIconColor(Color.web("#c2843a"));
        HBox wrapper = new HBox(icon);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setMinWidth(38);
        wrapper.setMaxWidth(38);
        return wrapper;
    }

    private String estadoInternacion(Internacion i) {
        return i.getFechaHoraEgreso() == null ? "Internado" : "Egresado";
    }

    private long diasEstadia(Internacion i) {
        if (i.getFechaHoraIngreso() == null) return 0;
        LocalDate fin = i.getFechaHoraEgreso() != null
                ? i.getFechaHoraEgreso().toLocalDate() : LocalDate.now();
        return Math.max(0, ChronoUnit.DAYS.between(i.getFechaHoraIngreso().toLocalDate(), fin));
    }

    private void actualizarEstadisticas() {
        int internados = 0, egresados = 0;
        long diasTotal = 0;
        for (Internacion i : todasLasInternaciones) {
            if (i.getFechaHoraEgreso() == null) internados++; else egresados++;
            diasTotal += diasEstadia(i);
        }
        int total = todasLasInternaciones.size();
        long promedio = total > 0 ? diasTotal / total : 0;
        lblStatInternados.setText(String.valueOf(internados));
        lblStatEgresados.setText(String.valueOf(egresados));
        lblStatDias.setText(String.valueOf(promedio));
        lblStatTotal.setText(String.valueOf(total));
    }

    private String safe(String s) {
        return s != null && !s.isBlank() ? s : "—";
    }

    private String truncar(String s, int max) {
        if (s == null || s.isBlank()) return "—";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    @FXML
    private void handleBuscar() { aplicarFiltros(); }

    @FXML
    private void handleNueva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevaInternacion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Internación");
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

    private void darEgreso(Internacion sel) {
        if (sel.getFechaHoraEgreso() != null) {
            mostrarAlerta("Esta internación ya tiene fecha de egreso registrada.");
            return;
        }
        if (ConfirmDialog.mostrar(
                "Confirmar Egreso", "🏥",
                "¿Registrar egreso para la Internación #" + sel.getId() + " con la fecha de hoy?",
                "Confirmar Egreso", "#2b87a0")) {
            try {
                service.darEgreso(sel.getId(), LocalDateTime.now());
                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error al registrar egreso: " + e.getMessage());
            }
        }
    }

    private void verDetalle(Internacion sel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalleInternacion.fxml"));
            Parent root = loader.load();
            DetalleInternacionController ctrl = loader.getController();
            ctrl.setDatos(sel);
            Stage stage = new Stage();
            stage.setTitle("Detalle de Internación #" + sel.getId());
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}