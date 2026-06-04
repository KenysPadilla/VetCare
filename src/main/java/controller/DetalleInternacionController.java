package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Internacion;
import model.InternacionMedicamento;
import model.Medicamento;
import service.InternacionService;
import ui.NumericFormatter;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DetalleInternacionController {

    @FXML private Label   lblHeaderPaciente;
    @FXML private Label   lblNombrePac;
    @FXML private Label   lblEspecie;
    @FXML private Label   lblPropietario;
    @FXML private Label   lblEstado;
    @FXML private Label   lblIngreso;
    @FXML private Label   lblEgreso;
    @FXML private Label   lblVeterinario;
    @FXML private Label   lblCostoDia;
    @FXML private Label   lblCostoDias;
    @FXML private Label   lblCostoMeds;
    @FXML private Label   lblCostoTotal;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox    vboxMedicamentos;
    @FXML private TextArea taMotivo;
    @FXML private TextArea taDiagnostico;
    @FXML private TextArea taObservaciones;

    private final InternacionService service = new InternacionService();
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setDatos(Internacion i) {
        String nomPac = (i.getPaciente() != null && i.getPaciente().getNombre() != null)
                        ? i.getPaciente().getNombre() : "—";
        lblHeaderPaciente.setText("Paciente: " + nomPac);
        lblNombrePac.setText(nomPac);

        if (i.getPaciente() != null) {
            lblEspecie.setText(orDash(i.getPaciente().getEspecie()));
            lblPropietario.setText(i.getPaciente().getPropietario() != null
                    ? i.getPaciente().getPropietario().getNombreCompleto() : "—");
        } else {
            lblEspecie.setText("—");
            lblPropietario.setText("—");
        }

        boolean internado = i.getFechaHoraEgreso() == null;
        lblEstado.setText(internado ? "Internado" : "Egresado");
        lblEstado.setStyle(lblEstado.getStyle()
                + (internado ? " -fx-text-fill: #D97706;" : " -fx-text-fill: #1B6B2F;"));

        lblIngreso.setText(i.getFechaHoraIngreso() != null
                ? i.getFechaHoraIngreso().toString().replace("T", "  ") : "—");
        lblEgreso.setText(i.getFechaHoraEgreso() != null
                ? i.getFechaHoraEgreso().toString().replace("T", "  ") : "Aún internado");

        lblVeterinario.setText(i.getVeterinario() != null
                ? "Dr. " + i.getVeterinario().getNombreCompleto() : "—");

        lblCostoDia.setText(NumericFormatter.formatCurrency(i.getCostoDia()));

        try {
            List<InternacionMedicamento> meds = service.listarMedicamentosPorInternacion(i.getId());
            i.setMedicamentos(meds);
        } catch (SQLException ex) {
            i.setMedicamentos(new ArrayList<>());
        }

        poblarMedicamentos(i.getMedicamentos());

        double costoMeds = i.calcularCostoMedicamentos();
        double costoDias = i.calcularCostoDias();

        lblCostoDias.setText(internado ? "En curso" : NumericFormatter.formatCurrency(costoDias));
        lblCostoMeds.setText(NumericFormatter.formatCurrency(costoMeds));

        if (internado) {
            lblCostoTotal.setText(costoMeds > 0
                    ? "En curso + " + NumericFormatter.formatCurrency(costoMeds) + " (medic.)"
                    : "En curso");
        } else {
            lblCostoTotal.setText(NumericFormatter.formatCurrency(costoDias + costoMeds));
        }

        taMotivo.setText(orDash(i.getMotivo()));
        taDiagnostico.setText(orDash(i.getDiagnostico()));
        taObservaciones.setText(orDash(i.getObservaciones()));

        Platform.runLater(() -> scrollPane.setVvalue(0));
    }

    private void poblarMedicamentos(List<InternacionMedicamento> meds) {
        vboxMedicamentos.getChildren().clear();

        if (meds == null || meds.isEmpty()) {
            Label lbl = new Label("Sin medicamentos registrados");
            lbl.setStyle("-fx-text-fill: #8e9db0; -fx-font-style: italic; -fx-font-size: 12px;");
            vboxMedicamentos.getChildren().add(lbl);
            return;
        }

        for (int idx = 0; idx < meds.size(); idx++) {
            InternacionMedicamento im = meds.get(idx);
            Medicamento med = im.getMedicamento();

            String nombre = med != null ? med.getNombre() : "—";
            String conc = (med != null && med.getConcentracion() != null && !med.getConcentracion().isBlank())
                    ? " [" + med.getConcentracion() + "]" : "";
            double precio = med != null ? med.getPrecio() : 0.0;
            double subtotal = precio * im.getCantidad();

            String linea1 = nombre + conc + "  ×" + im.getCantidad()
                    + "   →   $" + String.format("%.2f", subtotal);

            Label lNombre = new Label(linea1);
            lNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1a2e3b;");
            lNombre.setWrapText(true);

            VBox rowBox = new VBox(2, lNombre);

            StringBuilder detalle = new StringBuilder();
            if (im.getFechaAplicacion() != null) {
                detalle.append("Fecha: ").append(im.getFechaAplicacion().format(FMT_FECHA));
            }
            if (im.getDosis() != null && !im.getDosis().isBlank()) {
                if (detalle.length() > 0) detalle.append("   |   ");
                detalle.append("Dosis: ").append(im.getDosis());
            }
            if (detalle.length() > 0) {
                Label lDetalle = new Label(detalle.toString());
                lDetalle.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7f8e;");
                lDetalle.setWrapText(true);
                rowBox.getChildren().add(lDetalle);
            }

            vboxMedicamentos.getChildren().add(rowBox);

            if (idx < meds.size() - 1) {
                Separator sep = new Separator();
                sep.setStyle("-fx-opacity: 0.35;");
                vboxMedicamentos.getChildren().add(sep);
            }
        }
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderPaciente.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
