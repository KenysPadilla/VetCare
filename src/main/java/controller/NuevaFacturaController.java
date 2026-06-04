package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Cirugia;
import model.Consulta;
import model.DetalleFactura;
import model.ExamenLab;
import model.Factura;
import model.Internacion;
import model.Paciente;
import model.Propietario;
import model.ServicioEstetico;
import model.ServicioFacturable;
import model.Vacunacion;
import service.CirugiaService;
import service.ConsultaService;
import service.ExamenLabService;
import service.FacturaService;
import service.InternacionService;
import service.MedicamentoService;
import service.PacienteService;
import service.PropietarioService;
import service.ServicioEsteticoService;
import service.VacunacionService;

import util.ComboBoxFilter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class NuevaFacturaController implements Initializable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yy");

    @FXML private ComboBox<Propietario> cbPropietario;
    @FXML private ComboBox<Paciente>    cbPaciente;
    @FXML private ComboBox<String>      cbMetodoPago;

    @FXML private Label                                      lblServiciosInfo;
    @FXML private TableView<ServicioFacturable>              tablaServicios;
    @FXML private TableColumn<ServicioFacturable, Boolean>   colSeleccionar;
    @FXML private TableColumn<ServicioFacturable, String>    colTipoServicio;
    @FXML private TableColumn<ServicioFacturable, String>    colDescServicio;
    @FXML private TableColumn<ServicioFacturable, Integer>   colCantidadServicio;
    @FXML private TableColumn<ServicioFacturable, String>    colCostoServicio;
    @FXML private TableColumn<ServicioFacturable, String>    colSubtotalServicio;

    @FXML private TableView<DetalleFactura>            tablaDetalles;
    @FXML private TableColumn<DetalleFactura, String>  colConcepto;
    @FXML private TableColumn<DetalleFactura, String>  colTipo;
    @FXML private TableColumn<DetalleFactura, Integer> colCantidad;
    @FXML private TableColumn<DetalleFactura, Double>  colPrecio;
    @FXML private TableColumn<DetalleFactura, Double>  colSubtotal;

    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;
    @FXML private Label lblMensaje;

    private final ArrayList<DetalleFactura> detallesActuales = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {

            colConcepto.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            colConcepto.setCellFactory(col -> {
                Text text = new Text();
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(16));
                text.setStyle("-fx-font-size: 12px;");
                TableCell<DetalleFactura, String> cell = new TableCell<>() {
                    @Override protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        text.setText((empty || item == null) ? null : item);
                        setGraphic(empty ? null : text);
                        setPadding(new Insets(6, 8, 6, 8));
                    }
                };
                return cell;
            });
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoConcepto"));

            colCantidad.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCantidad()));
            colPrecio.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrecioUnitario()));
            colPrecio.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : ui.NumericFormatter.formatCurrency(item));
                }
            });
            colSubtotal.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getSubtotal()));
            colSubtotal.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : ui.NumericFormatter.formatCurrency(item));
                }
            });

            tablaServicios.setEditable(true);
            colSeleccionar.setCellValueFactory(data -> data.getValue().seleccionadoProperty());
            colSeleccionar.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccionar));
            colTipoServicio.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            colDescServicio.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            colDescServicio.setCellFactory(col -> {
                Text text = new Text();
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(16));
                text.setStyle("-fx-font-size: 12px;");
                TableCell<ServicioFacturable, String> cell = new TableCell<>() {
                    @Override protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        text.setText((empty || item == null) ? null : item);
                        setGraphic(empty ? null : text);
                        setPadding(new Insets(6, 8, 6, 8));
                    }
                };
                return cell;
            });
            colCantidadServicio.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleObjectProperty<>(1));
            colCostoServicio.setCellValueFactory(data ->
                    new SimpleStringProperty(ui.NumericFormatter.formatCurrency(data.getValue().getCosto())));
            colSubtotalServicio.setCellValueFactory(data ->
                    new SimpleStringProperty(ui.NumericFormatter.formatCurrency(data.getValue().getCosto())));

            cbPropietario.setConverter(new StringConverter<Propietario>() {
                @Override public String toString(Propietario p) {
                    return p == null ? "" : p.getNombreCompleto() + " — CC: " + p.getCedula();
                }
                @Override public Propietario fromString(String s) { return null; }
            });
            cbPaciente.setConverter(new StringConverter<Paciente>() {
                @Override public String toString(Paciente p) {
                    return p == null ? "" : p.getNombre() + " (" + p.getEspecie() + ")";
                }
                @Override public Paciente fromString(String s) { return null; }
            });

            cbMetodoPago.getItems().addAll("Efectivo", "Tarjeta", "Transferencia");

            try {
                List<Propietario> propietarios = new PropietarioService().listarTodos();
                ComboBoxFilter.apply(cbPropietario, propietarios, Object::toString);
            } catch (java.sql.SQLException e) {
                mostrarMensaje("Error al cargar propietarios: " + e.getMessage(), "#D32F2F");
            }

            cbPropietario.setOnAction(e -> {
                cbPaciente.getItems().clear();
                cbPaciente.setValue(null);
                tablaServicios.getItems().clear();
                Propietario prop = cbPropietario.getValue();
                if (prop != null) {
                    try {
                        cbPaciente.getItems().addAll(
                                new PacienteService().buscarPorPropietario(prop.getCedula()));
                    } catch (java.sql.SQLException ex) {
                        mostrarMensaje("Error al cargar pacientes: " + ex.getMessage(), "#D32F2F");
                    }
                }
            });

            cbPaciente.setOnAction(e -> {
                tablaServicios.getItems().clear();
                detallesActuales.clear();
                tablaDetalles.getItems().clear();
                Paciente pac = cbPaciente.getValue();
                if (pac != null) {
                    cargarServiciosPaciente(pac);
                    autoAgregarTodosServicios();
                }
                actualizarTotales();
            });

            actualizarTotales();

        } catch (Exception e) {
            mostrarMensaje("Error al inicializar el formulario: " + e.getMessage(), "#D32F2F");
            e.printStackTrace();
        }
    }

    private void cargarServiciosPaciente(Paciente paciente) {
        int id = paciente.getId();

        try {
            for (Consulta c : new ConsultaService().listarPorPaciente(id)) {
                String desc  = "Consulta: " + nvl(c.getDiagnostico(), "sin diagnóstico");
                String fecha = c.getFechaHora() != null ? c.getFechaHora().format(FMT) : "—";
                tablaServicios.getItems().add(new ServicioFacturable("Consulta", desc, fecha, c.getCosto()));

                String tratamiento = c.getTratamiento();
                if (tratamiento != null && tratamiento.contains("MEDICAMENTOS PRESCRITOS:")) {
                    java.util.Map<String, Double> preciosMed = obtenerPreciosMedicamentos();
                    for (String linea : tratamiento.split("\n")) {
                        String t = linea.trim();
                        if (t.startsWith("• ")) {
                            String info = t.substring(2);
                            String nombre = info.contains(" [") ? info.substring(0, info.indexOf(" ["))
                                    : (info.contains(" — ") ? info.substring(0, info.indexOf(" — ")) : info);
                            nombre = nombre.trim();
                            double precio = preciosMed.getOrDefault(nombre.toLowerCase(), 0.0);
                            tablaServicios.getItems().add(
                                    new ServicioFacturable("Medicamento", "Med: " + nombre, fecha, precio));
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            for (Cirugia c : new CirugiaService().listarPorPaciente(id)) {
                String desc  = "Cirugía: " + nvl(c.getTipoCirugia(), "sin tipo");
                String fecha = c.getFechaHora() != null ? c.getFechaHora().format(FMT) : "—";
                tablaServicios.getItems().add(new ServicioFacturable("Cirugía", desc, fecha, c.getCosto()));
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            for (ExamenLab e : new ExamenLabService().listarPorPaciente(id)) {
                String desc  = "Lab: " + nvl(e.getTipoExamen(), "sin tipo");
                String fecha = e.getFechaHora() != null ? e.getFechaHora().format(FMT) : "—";
                tablaServicios.getItems().add(new ServicioFacturable("Laboratorio", desc, fecha, e.getCosto()));
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            InternacionService intSvc = new InternacionService();
            for (Internacion i : intSvc.listarPorPaciente(id)) {
                if (i.getFechaHoraIngreso() == null) continue;

                LocalDate ingreso   = i.getFechaHoraIngreso().toLocalDate();
                boolean   enCurso  = i.getFechaHoraEgreso() == null;
                LocalDate egreso   = enCurso ? LocalDate.now() : i.getFechaHoraEgreso().toLocalDate();
                long      dias     = Math.max(1, ChronoUnit.DAYS.between(ingreso, egreso));
                double    costoDias = dias * i.getCostoDia();

                String desc = "Internación: " + nvl(i.getMotivo(), "sin motivo")
                        + "  —  " + ui.NumericFormatter.formatCurrency(costoDias)
                        + (enCurso ? " (provisional)" : "")
                        + "\n" + dias + " día" + (dias != 1 ? "s" : "")
                        + " × " + ui.NumericFormatter.formatCurrency(i.getCostoDia()) + "/día";

                tablaServicios.getItems().add(
                        new ServicioFacturable("Internación", desc,
                                i.getFechaHoraIngreso().format(FMT), costoDias));
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            for (Vacunacion v : new VacunacionService().listarPorPaciente(id)) {
                String nombre = v.getVacuna() != null ? v.getVacuna().getNombre() : "Vacuna";
                String desc   = "Vacuna: " + nombre;
                String fecha  = v.getFechaHoraAplicacion() != null
                        ? v.getFechaHoraAplicacion().toLocalDate().format(FMT) : "—";
                double precio = v.getVacuna() != null ? v.getVacuna().getPrecio() : 0.0;
                tablaServicios.getItems().add(new ServicioFacturable("Vacunación", desc, fecha, precio));
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            for (ServicioEstetico s : new ServicioEsteticoService().listarPorPaciente(id)) {
                String tipoDesc;
                if ("BANO".equalsIgnoreCase(s.getTipoServicio())) {
                    tipoDesc = "Baño"
                            + (s.getTipoBano() != null && !s.getTipoBano().isBlank()
                                ? " " + s.getTipoBano() : "");
                } else if ("MOTILADA".equalsIgnoreCase(s.getTipoServicio())) {
                    tipoDesc = "Motilada"
                            + (s.getEstiloCorte() != null && !s.getEstiloCorte().isBlank()
                                ? " " + s.getEstiloCorte() : "");
                } else {
                    tipoDesc = nvl(s.getTipoServicio(), "Servicio estético");
                }
                String desc  = "Estética: " + tipoDesc;
                String fecha = s.getFechaHora() != null ? s.getFechaHora().format(FMT) : "—";
                tablaServicios.getItems().add(new ServicioFacturable("Estética", desc, fecha, s.getPrecio()));
            }
        } catch (Exception e) { e.printStackTrace(); }

        if (tablaServicios.getItems().isEmpty()) {
            lblServiciosInfo.setText("No hay servicios registrados para este paciente");
        } else {
            lblServiciosInfo.setText(tablaServicios.getItems().size()
                    + " servicio(s) — desmarca los que NO deseas cobrar y presiona \"Agregar seleccionados\"");
        }
    }

    private java.util.Map<String, Double> obtenerPreciosMedicamentos() {
        java.util.Map<String, Double> mapa = new java.util.HashMap<>();
        try {
            for (model.Medicamento m : new MedicamentoService().listarTodos()) {
                mapa.put(m.getNombre().toLowerCase(), m.getPrecio());
            }
        } catch (Exception ignored) {}
        return mapa;
    }

    private void autoAgregarTodosServicios() {
        for (ServicioFacturable sf : tablaServicios.getItems()) {
            sf.setSeleccionado(true);
        }
    }

    @FXML
    private void handleAgregarSeleccionados() {
        List<ServicioFacturable> seleccionados = tablaServicios.getItems().stream()
                .filter(ServicioFacturable::isSeleccionado)
                .collect(Collectors.toList());

        if (seleccionados.isEmpty()) {
            mostrarMensaje("Marca al menos un servicio para agregarlo.", "#D32F2F");
            return;
        }

        for (ServicioFacturable sf : seleccionados) {
            boolean yaExiste = detallesActuales.stream().anyMatch(d ->
                    d.getDescripcion().equals(sf.getDescripcion())
                    && d.getTipoConcepto().equals(sf.getTipo().toUpperCase()));
            if (!yaExiste) {
                DetalleFactura det = new DetalleFactura();
                det.setDescripcion(sf.getDescripcion());
                det.setTipoConcepto(sf.getTipo().toUpperCase());
                det.setCantidad(1);
                det.setPrecioUnitario(sf.getCosto());
                det.setSubtotal(det.calcularSubtotal());
                detallesActuales.add(det);
                tablaDetalles.getItems().add(det);
            }
        }

        actualizarTotales();
        mostrarMensaje(seleccionados.size() + " servicio(s) agregado(s) a la factura.", "#1B6B2F");
    }

    @FXML
    private void handleAgregarConcepto() {
        final String fieldStyle = "-fx-border-radius: 8; -fx-background-radius: 8; "
                + "-fx-border-color: #c8ddd1; -fx-border-width: 1.5; "
                + "-fx-font-size: 13px; -fx-padding: 8 12 8 12; -fx-background-color: white;";
        final String labelStyle = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2d5a3d;";

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll(
                "CONSULTA", "INTERNACIÓN", "CIRUGÍA", "LABORATORIO",
                "VACUNACIÓN", "ESTÉTICA", "MEDICAMENTO", "OTRO");
        cbTipo.setValue("CONSULTA");
        cbTipo.setPrefHeight(36); cbTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtCantidad = new TextField("1");
        txtCantidad.setPrefHeight(36); txtCantidad.setPrefWidth(80);
        txtCantidad.setStyle(fieldStyle);

        TextField txtOtroTipo = new TextField();
        txtOtroTipo.setPromptText("Especificar tipo de servicio...");
        txtOtroTipo.setPrefHeight(36); txtOtroTipo.setStyle(fieldStyle);
        Label lOtroTipo = new Label("Especificar tipo *"); lOtroTipo.setStyle(labelStyle);
        VBox vbOtro = new VBox(5, lOtroTipo, txtOtroTipo);
        vbOtro.setVisible(false); vbOtro.setManaged(false);

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Ej: Revisión especial, Procedimiento adicional...");
        txtDesc.setPrefHeight(36); txtDesc.setStyle(fieldStyle);

        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Ej: 50000");
        txtPrecio.setPrefHeight(36); txtPrecio.setStyle(fieldStyle);
        ui.NumericFormatter.apply(txtPrecio);

        Label lblSubtotal = new Label("$0.00");
        lblSubtotal.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Runnable recalcular = () -> {
            try {
                int    c = Integer.parseInt(txtCantidad.getText().trim());
                double p = ui.NumericFormatter.toDouble(txtPrecio);
                lblSubtotal.setText(ui.NumericFormatter.formatCurrency(c * p));
            } catch (NumberFormatException ignored) {
                lblSubtotal.setText("$0.00");
            }
        };
        txtCantidad.textProperty().addListener((obs, o, n) -> recalcular.run());
        txtPrecio.textProperty().addListener((obs, o, n) -> recalcular.run());

        Label lTipo   = new Label("Tipo de concepto");  lTipo.setStyle(labelStyle);
        Label lCant   = new Label("Cantidad");          lCant.setStyle(labelStyle);
        Label lDesc   = new Label("Descripción *");     lDesc.setStyle(labelStyle);
        Label lPrecio = new Label("Precio unitario *"); lPrecio.setStyle(labelStyle);

        VBox vbTipo = new VBox(5, lTipo, cbTipo);
        HBox.setHgrow(vbTipo, Priority.ALWAYS);
        VBox vbCant = new VBox(5, lCant, txtCantidad);
        HBox rowTC = new HBox(12, vbTipo, vbCant);

        Label lSubTexto = new Label("Subtotal estimado:");
        lSubTexto.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7f8e;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox rowSub = new HBox(lSubTexto, spacer, lblSubtotal);
        rowSub.setAlignment(Pos.CENTER_LEFT);
        rowSub.setStyle("-fx-background-color: #f8fafb; -fx-background-radius: 8; -fx-padding: 10 14 10 14;");

        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.3;");

        VBox content = new VBox(12,
                rowTC,
                vbOtro,
                new VBox(5, lDesc, txtDesc),
                new VBox(5, lPrecio, txtPrecio),
                sep,
                rowSub);
        content.setPadding(new Insets(20, 20, 10, 20));
        content.setPrefWidth(440);

        ButtonType btnAgregar = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        Dialog<DetalleFactura> dialog = new Dialog<>();
        dialog.setTitle("Agregar concepto manual");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(btnAgregar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        dialog.getDialogPane().setPrefWidth(460);

        Node btnOk = dialog.getDialogPane().lookupButton(btnAgregar);
        btnOk.setStyle("-fx-background-color: #2b87a0; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-font-size: 13px; "
                + "-fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((Region) btnOk).setPrefHeight(28);
        ((Region) btnOk).setMinHeight(28);
        ((Region) btnOk).setMaxHeight(28);
        Node btnCan = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCan.setStyle("-fx-background-color: white; -fx-text-fill: #6b7f8e; "
                + "-fx-border-color: rgba(0,0,0,0.15); -fx-border-radius: 8; -fx-border-width: 1; "
                + "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 3 22 3 22; -fx-cursor: hand;");
        ((Region) btnCan).setPrefHeight(28);
        ((Region) btnCan).setMinHeight(28);
        ((Region) btnCan).setMaxHeight(28);

        cbTipo.valueProperty().addListener((obs, o, n) -> {
            boolean esOtro = "OTRO".equals(n);
            vbOtro.setVisible(esOtro);
            vbOtro.setManaged(esOtro);
            if (!esOtro) txtOtroTipo.clear();
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        Runnable validar = () -> {
            boolean descVacia   = txtDesc.getText().trim().isEmpty();
            boolean precioVacio = txtPrecio.getText().trim().isEmpty();
            boolean otroVacio   = "OTRO".equals(cbTipo.getValue())
                    && txtOtroTipo.getText().trim().isEmpty();
            btnOk.setDisable(descVacia || precioVacio || otroVacio);
        };
        btnOk.setDisable(true);
        txtDesc.textProperty().addListener((obs, o, n) -> validar.run());
        txtPrecio.textProperty().addListener((obs, o, n) -> validar.run());
        txtOtroTipo.textProperty().addListener((obs, o, n) -> validar.run());
        cbTipo.valueProperty().addListener((obs, o, n) -> validar.run());

        dialog.setResultConverter(bt -> {
            if (bt != btnAgregar) return null;
            try {
                String tipo = "OTRO".equals(cbTipo.getValue())
                        ? txtOtroTipo.getText().trim().toUpperCase()
                        : cbTipo.getValue();
                DetalleFactura det = new DetalleFactura();
                det.setDescripcion(txtDesc.getText().trim());
                det.setTipoConcepto(tipo);
                det.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
                det.setPrecioUnitario(ui.NumericFormatter.toDouble(txtPrecio));
                det.setSubtotal(det.calcularSubtotal());
                return det;
            } catch (NumberFormatException e) {
                return null;
            }
        });

        dialog.showAndWait().ifPresent(det -> {
            if (det != null) {
                detallesActuales.add(det);
                tablaDetalles.getItems().add(det);
                actualizarTotales();
                mostrarMensaje("Concepto agregado a la factura.", "#1B6B2F");
            } else {
                mostrarMensaje("Precio unitario debe ser un número válido.", "#D32F2F");
            }
        });
    }

    @FXML
    private void handleGenerarFactura() {
        if (cbPropietario.getValue() == null
                || cbPaciente.getValue() == null
                || cbMetodoPago.getValue() == null) {
            mostrarMensaje("Propietario, paciente y método de pago son obligatorios.", "#D32F2F");
            return;
        }
        if (detallesActuales.isEmpty()) {
            mostrarMensaje("Debe agregar al menos un concepto a la factura.", "#D32F2F");
            return;
        }
        try {
            Factura f = new Factura();
            f.setPropietario(cbPropietario.getValue());
            f.setPaciente(cbPaciente.getValue());
            f.setFechaHora(java.time.LocalDateTime.now());
            f.setMetodoPago(cbMetodoPago.getValue());
            f.setEstadoFactura("PENDIENTE");
            new FacturaService().generarFactura(f, detallesActuales);
            mostrarMensaje("Factura generada exitosamente.", "#1B6B2F");
            cerrarVentana();
        } catch (java.sql.SQLException e) {
            mostrarMensaje("Error al generar la factura: " + e.getMessage(), "#D32F2F");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void actualizarTotales() {
        double sub = detallesActuales.stream()
                .mapToDouble(DetalleFactura::calcularSubtotal)
                .sum();
        lblSubtotal.setText(ui.NumericFormatter.formatCurrency(sub));
        lblIva.setText(ui.NumericFormatter.formatCurrency(sub * 0.19));
        lblTotal.setText(ui.NumericFormatter.formatCurrency(sub * 1.19));
    }

    private void cerrarVentana() {
        ((Stage) lblMensaje.getScene().getWindow()).close();
    }

    private void mostrarMensaje(String texto, String color) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }

    private static String nvl(String valor, String defecto) {
        return (valor != null && !valor.isBlank()) ? valor : defecto;
    }
}
