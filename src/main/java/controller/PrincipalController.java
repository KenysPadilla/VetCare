package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Cita;
import model.Cirugia;
import model.Factura;
import model.Internacion;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import service.CitaService;
import service.CirugiaService;
import service.ConsultaService;
import service.FacturaService;
import service.InternacionService;
import service.PacienteService;
import service.VeterinarioService;
import ui.IconHelper;
import ui.NavHelper;
import ui.NumericFormatter;
import ui.StyleManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import service.SolicitudCitaService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    @FXML private Label lblTituloPagina;
    @FXML private Label lblUsuarioActual;
    @FXML private Label lblRolActual;
    @FXML private StackPane contenedorPrincipal;
    @FXML private VBox panelWelcomeHeader;

    @FXML private Label lblCitasHoy;
    @FXML private Label lblPacientesActivos;
    @FXML private Label lblConsultasMes;
    @FXML private Label lblProximaCita;
    @FXML private Label lblInternaciones;
    @FXML private Label lblCirugias;
    @FXML private Label lblFacturacionHoy;
    @FXML private Label lblVeterinariosActivos;

    @FXML private Label lblMetaCitas;
    @FXML private VBox  vboxCitasHoy;

    @FXML private VBox vboxActividad;

    @FXML private Button btnPropietarios;
    @FXML private Button btnPacientes;
    @FXML private Button btnVeterinarios;
    @FXML private Button btnEstilistas;
    @FXML private Button btnCitas;
    @FXML private Button btnConsultas;
    @FXML private Button btnVacunaciones;
    @FXML private Button btnCirugias;
    @FXML private Button btnInternaciones;
    @FXML private Button btnLaboratorio;
    @FXML private Button btnMedicamentos;
    @FXML private Button btnVacunas;
    @FXML private Button btnServicios;
    @FXML private Button btnFacturacion;
    @FXML private Button btnUsuarios;
    @FXML private Button btnSolicitudes;
    @FXML private Button btnCerrarSesion;
    @FXML private VBox   panelNotificacion;

    @FXML private VBox grupoRegistroItems;
    @FXML private VBox grupoPersonalItems;
    @FXML private VBox grupoConsultaItems;
    @FXML private VBox grupoProcedimientosItems;
    @FXML private VBox grupoSaludPreventivaItems;
    @FXML private VBox grupoEsteticaItems;
    @FXML private VBox grupoAdministracionItems;

    @FXML private FontIcon arrowRegistro;
    @FXML private FontIcon arrowPersonal;
    @FXML private FontIcon arrowConsulta;
    @FXML private FontIcon arrowProcedimientos;
    @FXML private FontIcon arrowSaludPreventiva;
    @FXML private FontIcon arrowEstetica;
    @FXML private FontIcon arrowAdministracion;

    @FXML private Button btnGrupoRegistro;
    @FXML private Button btnGrupoPersonal;
    @FXML private Button btnGrupoConsulta;
    @FXML private Button btnGrupoProcedimientos;
    @FXML private Button btnGrupoSaludPreventiva;
    @FXML private Button btnGrupoEstetica;
    @FXML private Button btnGrupoAdministracion;

    private Node panelInicio;
    private Timeline pollingTimeline;
    private int pendingCount   = 0;
    private int dismissedCount = 0;

    private static final DateTimeFormatter FMT_HORA  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM");

    private Button[] allNavButtons() {
        return new Button[] {
                btnPropietarios, btnPacientes, btnVeterinarios, btnEstilistas,
                btnCitas, btnConsultas, btnVacunaciones, btnCirugias, btnInternaciones,
                btnLaboratorio, btnMedicamentos, btnVacunas, btnServicios, btnFacturacion,
                btnUsuarios, btnSolicitudes
        };
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblUsuarioActual.setText("Usuario: " + util.Sesion.getUsername());
        lblRolActual.setText(util.Sesion.getRol());
        if (!contenedorPrincipal.getChildren().isEmpty()) {
            panelInicio = contenedorPrincipal.getChildren().get(0);
        }
        contenedorPrincipal.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) StyleManager.apply(newScene);
        });
        inicializarGrupos();
        configureSidebarIcons();
        mostrarCabeceraInicio();
        cargarDashboard();
        iniciarPollingNotificaciones();
    }

    

    private void iniciarPollingNotificaciones() {
        verificarSolicitudesPendientes();
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> verificarSolicitudesPendientes()));
        pollingTimeline.setCycleCount(Animation.INDEFINITE);
        pollingTimeline.play();
    }

    private void verificarSolicitudesPendientes() {
        try {
            int count = new SolicitudCitaService().listarPendientes().size();
            if (count > dismissedCount) {
                mostrarNotificacion(count);
            } else if (count == 0) {
                dismissedCount = 0;
                ocultarNotificacion();
            }
        } catch (Exception ignored) {}
    }

    private void mostrarNotificacion(int count) {
        pendingCount = count;
        panelNotificacion.getChildren().clear();

        FontIcon icono = new FontIcon(FontAwesomeSolid.BELL);
        icono.setIconSize(12);
        icono.setStyle("-fx-icon-color: #d97706;");

        String texto = count == 1 ? "1 solicitud pendiente" : count + " solicitudes pendientes";
        Label lblTexto = new Label(texto);
        lblTexto.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #92400e;");
        HBox.setHgrow(lblTexto, Priority.ALWAYS);
        lblTexto.setMaxWidth(Double.MAX_VALUE);

        Button btnCerrar = new Button("×");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: #92400e;"
                + "-fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 0 2 0 2; -fx-border-color: transparent;");
        btnCerrar.setOnAction(e -> {
            e.consume();
            dismissedCount = pendingCount;
            ocultarNotificacion();
        });

        HBox fila = new HBox(6, icono, lblTexto, btnCerrar);
        fila.setAlignment(Pos.CENTER_LEFT);

        Label lblSub = new Label("del chatbot · toca para ver");
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #b45309;");
        VBox.setMargin(lblSub, new Insets(0, 0, 0, 18));

        panelNotificacion.getChildren().addAll(fila, lblSub);
        panelNotificacion.setOnMouseClicked(e -> {
            if (!(e.getTarget() instanceof Button)) {
                dismissedCount = pendingCount;
                ocultarNotificacion();
                handleSolicitudes();
            }
        });

        panelNotificacion.setVisible(true);
        panelNotificacion.setManaged(true);
    }

    private void ocultarNotificacion() {
        panelNotificacion.setVisible(false);
        panelNotificacion.setManaged(false);
    }

    

    private void cargarDashboard() {
        LocalDate hoy = LocalDate.now();

        // --- Citas ---
        List<Cita> todasCitas = new ArrayList<>();
        try {
            todasCitas = new CitaService().listarTodos();
        } catch (Exception ignored) {}

        List<Cita> citasHoy = new ArrayList<>();
        Cita proximaCita = null;
        LocalDateTime ahora = LocalDateTime.now();

        for (Cita c : todasCitas) {
            if (c.getFechaHora() == null) continue;
            if (c.getFechaHora().toLocalDate().equals(hoy)) {
                citasHoy.add(c);
            }
            if (!"CANCELADA".equals(c.getEstadoCita())
                    && !"REALIZADA".equals(c.getEstadoCita())
                    && c.getFechaHora().isAfter(ahora)) {
                if (proximaCita == null || c.getFechaHora().isBefore(proximaCita.getFechaHora())) {
                    proximaCita = c;
                }
            }
        }
        citasHoy.sort(Comparator.comparing(Cita::getFechaHora));

        lblCitasHoy.setText(String.valueOf(citasHoy.size()));
        lblMetaCitas.setText(citasHoy.size() + " citas");

        if (proximaCita != null) {
            lblProximaCita.setText(proximaCita.getFechaHora().format(FMT_HORA)
                    + " · " + (proximaCita.getPaciente() != null
                    ? proximaCita.getPaciente().getNombre() : ""));
        } else {
            lblProximaCita.setText("Sin citas");
        }

        poblarCitasHoy(citasHoy);
        poblarActividad(todasCitas);

        
        try {
            int total = new PacienteService().listarTodos().size();
            lblPacientesActivos.setText(String.valueOf(total));
        } catch (Exception ignored) { lblPacientesActivos.setText("—"); }

        try {
            long consMes = new ConsultaService().listarTodos().stream()
                    .filter(c -> c.getFechaHora() != null
                            && c.getFechaHora().getMonth() == hoy.getMonth()
                            && c.getFechaHora().getYear()  == hoy.getYear())
                    .count();
            lblConsultasMes.setText(String.valueOf(consMes));
        } catch (Exception ignored) { lblConsultasMes.setText("—"); }

        try {
            long internadas = new InternacionService().listarTodos().stream()
                    .filter(i -> i.getFechaHoraEgreso() == null)
                    .count();
            lblInternaciones.setText(String.valueOf(internadas));
        } catch (Exception ignored) { lblInternaciones.setText("—"); }

        try {
            int totalCirugias = new CirugiaService().listarTodos().size();
            lblCirugias.setText(String.valueOf(totalCirugias));
        } catch (Exception ignored) { lblCirugias.setText("—"); }

        try {
            double totalHoy = new FacturaService().listarTodos().stream()
                    .filter(f -> f.getFechaHora() != null
                            && f.getFechaHora().toLocalDate().equals(hoy)
                            && !"ANULADA".equals(f.getEstadoFactura()))
                    .mapToDouble(Factura::getTotal)
                    .sum();
            lblFacturacionHoy.setText(NumericFormatter.formatCurrency(totalHoy));
        } catch (Exception ignored) { lblFacturacionHoy.setText("—"); }

        try {
            int totalVets = new VeterinarioService().listarTodos().size();
            lblVeterinariosActivos.setText(String.valueOf(totalVets));
        } catch (Exception ignored) { lblVeterinariosActivos.setText("—"); }
    }

    private void poblarCitasHoy(List<Cita> citas) {
        vboxCitasHoy.getChildren().clear();
        if (citas.isEmpty()) {
            Label lbl = new Label("No hay citas programadas para hoy");
            lbl.setStyle("-fx-text-fill: #8e9db0; -fx-font-style: italic; -fx-font-size: 12px;");
            vboxCitasHoy.getChildren().add(lbl);
            return;
        }
        int max = Math.min(citas.size(), 5);
        for (int i = 0; i < max; i++) {
            vboxCitasHoy.getChildren().add(crearFilaCita(citas.get(i)));
        }
    }

    private Node crearFilaCita(Cita c) {
        HBox row = new HBox(10);
        row.getStyleClass().add("appointment-row");
        row.setAlignment(Pos.CENTER_LEFT);

        String hora = c.getFechaHora() != null ? c.getFechaHora().format(FMT_HORA) : "--:--";
        Label lblHora = new Label(hora);
        lblHora.getStyleClass().add("badge-time");

        String paciente = c.getPaciente() != null ? c.getPaciente().getNombre() : "—";
        String propietario = (c.getPaciente() != null && c.getPaciente().getPropietario() != null)
                ? c.getPaciente().getPropietario().getNombreCompleto() : "—";
        String tipo = c.getTipoCita() != null ? c.getTipoCita() : (c.getMotivo() != null ? c.getMotivo() : "—");

        Label lblPac = new Label(paciente);
        lblPac.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblPac.getStyleClass().add("appointment-patient");

        Label lblProp = new Label(propietario);
        lblProp.getStyleClass().add("appointment-detail");

        Label lblTipo = new Label(tipo.length() > 30 ? tipo.substring(0, 28) + "…" : tipo);
        lblTipo.getStyleClass().add("appointment-detail");

        VBox info = new VBox(2, lblPac, lblProp, lblTipo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label badge = new Label(formatearEstadoCita(c.getEstadoCita()));
        badge.getStyleClass().add(badgeClassCita(c.getEstadoCita()));

        row.getChildren().addAll(lblHora, info, spacer, badge);
        return row;
    }

    private void poblarActividad(List<Cita> todasCitas) {
        vboxActividad.getChildren().clear();

        List<Cita> recientes = new ArrayList<>(todasCitas);
        recientes.sort(Comparator.comparing(Cita::getFechaHora, Comparator.nullsLast(Comparator.reverseOrder())));

        if (recientes.isEmpty()) {
            Label lbl = new Label("Sin actividad reciente");
            lbl.setStyle("-fx-text-fill: #8e9db0; -fx-font-style: italic; -fx-font-size: 12px;");
            vboxActividad.getChildren().add(lbl);
            return;
        }

        int max = Math.min(recientes.size(), 6);
        for (int i = 0; i < max; i++) {
            vboxActividad.getChildren().add(crearFilaActividad(recientes.get(i)));
        }
    }

    private Node crearFilaActividad(Cita c) {
        HBox row = new HBox(12);
        row.getStyleClass().add("appointment-row");
        row.setAlignment(Pos.CENTER_LEFT);

        Label dot = new Label();
        dot.getStyleClass().add(dotClassActividad(c.getEstadoCita()));

        String titulo = tituloActividad(c.getEstadoCita());
        String paciente = c.getPaciente() != null ? c.getPaciente().getNombre() : "—";
        String tipo = c.getTipoCita() != null ? c.getTipoCita() : (c.getMotivo() != null ? c.getMotivo() : "—");
        String fecha = c.getFechaHora() != null ? c.getFechaHora().format(FMT_FECHA) + " " + c.getFechaHora().format(FMT_HORA) : "";

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblTitulo.getStyleClass().add("activity-title");

        String detalle = paciente + " · " + tipo + " · " + fecha;
        Label lblDetalle = new Label(detalle.length() > 45 ? detalle.substring(0, 43) + "…" : detalle);
        lblDetalle.getStyleClass().add("activity-detail");

        VBox info = new VBox(2, lblTitulo, lblDetalle);
        row.getChildren().addAll(dot, info);
        return row;
    }

    private String formatearEstadoCita(String estado) {
        if (estado == null) return "Pendiente";
        return switch (estado) {
            case "PROGRAMADA" -> "Programada";
            case "EN_CURSO"   -> "En Curso";
            case "REALIZADA"  -> "Realizada";
            case "CANCELADA"  -> "Cancelada";
            default           -> estado;
        };
    }

    private String badgeClassCita(String estado) {
        if (estado == null) return "badge-pendiente";
        return switch (estado) {
            case "EN_CURSO"  -> "badge-en-curso";
            case "REALIZADA" -> "badge-confirmado";
            case "CANCELADA" -> "badge-cancelado";
            default          -> "badge-pendiente";
        };
    }

    private String dotClassActividad(String estado) {
        if (estado == null) return "activity-dot-green";
        return switch (estado) {
            case "EN_CURSO"  -> "activity-dot-blue";
            case "REALIZADA" -> "activity-dot-teal";
            case "CANCELADA" -> "activity-dot-red";
            default          -> "activity-dot-green";
        };
    }

    private String tituloActividad(String estado) {
        if (estado == null) return "Cita registrada";
        return switch (estado) {
            case "PROGRAMADA" -> "Cita programada";
            case "EN_CURSO"   -> "Cita en curso";
            case "REALIZADA"  -> "Cita realizada";
            case "CANCELADA"  -> "Cita cancelada";
            default           -> "Cita";
        };
    }

    

    private void configureSidebarIcons() {
        IconHelper.attachNavIcon(btnPropietarios, FontAwesomeSolid.USER);
        IconHelper.attachNavIcon(btnPacientes, FontAwesomeSolid.PAW);
        IconHelper.attachNavIcon(btnVeterinarios, FontAwesomeSolid.USER_MD);
        IconHelper.attachNavIcon(btnEstilistas, FontAwesomeSolid.CUT);
        IconHelper.attachNavIcon(btnCitas, FontAwesomeSolid.CALENDAR_ALT);
        IconHelper.attachNavIcon(btnConsultas, FontAwesomeSolid.STETHOSCOPE);
        IconHelper.attachNavIcon(btnVacunaciones, FontAwesomeSolid.SYRINGE);
        IconHelper.attachNavIcon(btnCirugias, FontAwesomeSolid.PROCEDURES);
        IconHelper.attachNavIcon(btnInternaciones, FontAwesomeSolid.BED);
        IconHelper.attachNavIcon(btnLaboratorio, FontAwesomeSolid.FLASK);
        IconHelper.attachNavIcon(btnMedicamentos, FontAwesomeSolid.PILLS);
        IconHelper.attachNavIcon(btnVacunas, FontAwesomeSolid.SHIELD_VIRUS);
        IconHelper.attachNavIcon(btnServicios, FontAwesomeSolid.BATH);
        IconHelper.attachNavIcon(btnFacturacion, FontAwesomeSolid.FILE_INVOICE_DOLLAR);
        IconHelper.attachNavIcon(btnUsuarios, FontAwesomeSolid.USERS_COG);
        IconHelper.attachNavIcon(btnSolicitudes, FontAwesomeSolid.COMMENT_DOTS);
        btnCerrarSesion.setGraphic(IconHelper.icon(FontAwesomeSolid.SIGN_OUT_ALT, 14, IconHelper.NAV_ACTIVE));
        btnCerrarSesion.setGraphicTextGap(10);
    }

    private void activarNav(Button active) {
        Button[] all = allNavButtons();
        Button[] others = new Button[all.length - 1];
        int j = 0;
        for (Button b : all) {
            if (b != active) others[j++] = b;
        }
        NavHelper.setActive(active, others);
    }

    private void mostrarCabeceraInicio() {
        panelWelcomeHeader.setVisible(true);
        panelWelcomeHeader.setManaged(true);
        lblTituloPagina.setVisible(false);
        lblTituloPagina.setManaged(false);
    }

    private void mostrarCabeceraModulo(String titulo) {
        panelWelcomeHeader.setVisible(false);
        panelWelcomeHeader.setManaged(false);
        lblTituloPagina.setText(titulo);
        lblTituloPagina.setVisible(true);
        lblTituloPagina.setManaged(true);
    }

    @FXML
    private void handleInicio() {
        contenedorPrincipal.getChildren().clear();
        if (panelInicio != null) contenedorPrincipal.getChildren().add(panelInicio);
        mostrarCabeceraInicio();
        cargarDashboard();
    }

    private void cargarVista(String fxml, String titulo, Button navButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
            Parent vista = loader.load();
            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(vista);
            mostrarCabeceraModulo(titulo);
            activarNav(navButton);
            abrirGrupoDeBoton(navButton);
        } catch (Exception e) {
            mostrarCabeceraModulo("Error al cargar: " + titulo);
            e.printStackTrace();
        }
    }

    @FXML private void handlePropietarios()  { cargarVista("propietarios.fxml",       "Propietarios",    btnPropietarios);  }
    @FXML private void handlePacientes()     { cargarVista("pacientes.fxml",           "Pacientes",       btnPacientes);     }
    @FXML private void handleVeterinarios()  { cargarVista("veterinarios.fxml",        "Veterinarios",    btnVeterinarios);  }
    @FXML private void handleEstilistas()    { cargarVista("estilistas.fxml",           "Estilistas",      btnEstilistas);    }
    @FXML private void handleCitas()         { cargarVista("citas.fxml",               "Citas Médicas",   btnCitas);         }
    @FXML private void handleConsultas()     { cargarVista("consultas.fxml",           "Consultas",       btnConsultas);     }
    @FXML private void handleVacunaciones()  { cargarVista("vacunaciones.fxml",        "Vacunaciones",    btnVacunaciones);  }
    @FXML private void handleCirugias()      { cargarVista("cirugias.fxml",            "Cirugías",        btnCirugias);      }
    @FXML private void handleInternaciones() { cargarVista("internaciones.fxml",       "Internaciones",   btnInternaciones); }
    @FXML private void handleLaboratorio()   { cargarVista("laboratorio.fxml",         "Laboratorio",     btnLaboratorio);   }
    @FXML private void handleServicios()     { cargarVista("serviciosEsteticos.fxml",  "Baño y Motilada", btnServicios);     }
    @FXML private void handleFacturacion()   { cargarVista("facturacion.fxml",         "Facturación",     btnFacturacion);   }
    @FXML private void handleUsuarios()      { cargarVista("usuarios.fxml",            "Usuarios",        btnUsuarios);      }
    @FXML private void handleMedicamentos()  { cargarVista("medicamentos.fxml",        "Medicamentos",    btnMedicamentos);  }
    @FXML private void handleVacunas()       { cargarVista("vacunas.fxml",             "Vacunas",         btnVacunas);       }
    @FXML private void handleSolicitudes()   { cargarVista("solicitudes.fxml",         "Solicitudes",     btnSolicitudes);   }

    

    /** Agrupación de las tres referencias que definen un grupo del accordion. */
    private record NavGrupo(Button header, VBox items, FontIcon arrow) {}
    private List<NavGrupo> navGrupos;

    /** Construye la lista de grupos. Llamar desde initialize() tras la inyección FXML. */
    private void inicializarGrupos() {
        navGrupos = List.of(
            new NavGrupo(btnGrupoRegistro,       grupoRegistroItems,        arrowRegistro),
            new NavGrupo(btnGrupoPersonal,        grupoPersonalItems,        arrowPersonal),
            new NavGrupo(btnGrupoConsulta,        grupoConsultaItems,        arrowConsulta),
            new NavGrupo(btnGrupoProcedimientos,  grupoProcedimientosItems,  arrowProcedimientos),
            new NavGrupo(btnGrupoSaludPreventiva, grupoSaludPreventivaItems, arrowSaludPreventiva),
            new NavGrupo(btnGrupoEstetica,        grupoEsteticaItems,        arrowEstetica),
            new NavGrupo(btnGrupoAdministracion,  grupoAdministracionItems,  arrowAdministracion)
        );
    }

    /** Cierra un grupo: oculta ítems, quita clase activa y gira la flecha a 0°. */
    private void cerrarGrupo(NavGrupo g) {
        g.items().setVisible(false);
        g.items().setManaged(false);
        g.header().getStyleClass().remove("nav-group-header-active");
        RotateTransition rt = new RotateTransition(Duration.millis(180), g.arrow());
        rt.setToAngle(0);
        rt.play();
    }

    /** Abre un grupo: cierra todos los demás, muestra ítems, añade clase activa y gira la flecha a 90°. */
    private void abrirGrupo(NavGrupo target) {
        navGrupos.stream()
                 .filter(g -> g.items().isVisible() && g != target)
                 .forEach(this::cerrarGrupo);
        target.items().setVisible(true);
        target.items().setManaged(true);
        if (!target.header().getStyleClass().contains("nav-group-header-active")) {
            target.header().getStyleClass().add("nav-group-header-active");
        }
        RotateTransition rt = new RotateTransition(Duration.millis(180), target.arrow());
        rt.setToAngle(90);
        rt.play();
    }

    private void toggleGrupo(NavGrupo g) {
        if (g.items().isVisible()) cerrarGrupo(g);
        else                       abrirGrupo(g);
    }

    @FXML private void toggleGrupoRegistro()        { toggleGrupo(navGrupos.get(0)); }
    @FXML private void toggleGrupoPersonal()        { toggleGrupo(navGrupos.get(1)); }
    @FXML private void toggleGrupoConsulta()        { toggleGrupo(navGrupos.get(2)); }
    @FXML private void toggleGrupoProcedimientos()  { toggleGrupo(navGrupos.get(3)); }
    @FXML private void toggleGrupoSaludPreventiva() { toggleGrupo(navGrupos.get(4)); }
    @FXML private void toggleGrupoEstetica()        { toggleGrupo(navGrupos.get(5)); }
    @FXML private void toggleGrupoAdministracion()  { toggleGrupo(navGrupos.get(6)); }

    
    private void abrirGrupoDeBoton(Button btn) {
        navGrupos.stream()
                 .filter(g -> g.items().getChildren().stream().anyMatch(n -> n == btn))
                 .findFirst()
                 .ifPresent(g -> { if (!g.items().isVisible()) abrirGrupo(g); });
    }

    @FXML
    private void handleCerrarSesion() {
        if (pollingTimeline != null) pollingTimeline.stop();
        util.Sesion.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 960, 600);
            StyleManager.apply(scene);
            Stage stageLogin = new Stage();
            stageLogin.setTitle("VetCare - Login");
            stageLogin.setScene(scene);
            stageLogin.setResizable(false);
            stageLogin.show();
            Stage stageActual = (Stage) contenedorPrincipal.getScene().getWindow();
            stageActual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}