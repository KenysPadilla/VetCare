package controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Paciente;
import model.Propietario;
import service.PacienteService;

import java.sql.SQLException;
import java.util.List;

public class DetallePropietarioController {

    @FXML private Label lblHeaderNombre;
    @FXML private Label lblNombreCompleto;
    @FXML private Label lblCedula;
    @FXML private Label lblDireccion;
    @FXML private Label lblTelefono;
    @FXML private Label lblEmail;
    @FXML private Label lblConteoMascotas;
    @FXML private VBox  vboxMascotas;

    public void setPropietario(Propietario p) {
        lblHeaderNombre.setText(p.getNombreCompleto());
        lblNombreCompleto.setText(p.getNombreCompleto());
        lblCedula.setText(orDash(p.getCedula()));
        lblDireccion.setText(orDash(p.getDireccion()));
        lblTelefono.setText(orDash(p.getTelefono()));
        lblEmail.setText(orDash(p.getEmail()));

        try {
            List<Paciente> mascotas = new PacienteService().buscarPorPropietario(p.getCedula());
            int n = mascotas.size();
            lblConteoMascotas.setText(n + (n == 1 ? " mascota" : " mascotas"));
            vboxMascotas.getChildren().clear();
            if (mascotas.isEmpty()) {
                Label vacia = new Label("Sin mascotas registradas.");
                vacia.setStyle("-fx-font-size: 12px; -fx-text-fill: #9eadb7;");
                vboxMascotas.getChildren().add(vacia);
            } else {
                for (Paciente m : mascotas) {
                    vboxMascotas.getChildren().add(crearTarjetaMascota(m));
                }
            }
        } catch (SQLException e) {
            lblConteoMascotas.setText("—");
            Label err = new Label("Error al cargar mascotas.");
            err.setStyle("-fx-font-size: 12px; -fx-text-fill: #c0392b;");
            vboxMascotas.getChildren().add(err);
        }
    }

    private VBox crearTarjetaMascota(Paciente m) {
        // Nombre con emoji de especie
        String emoji = emojiEspecie(m.getEspecie());
        Label lblNombre = new Label(emoji + "  " + orDash(m.getNombre()));
        lblNombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a2e3b;");

        // Especie · Raza
        String especieRaza = orDash(m.getEspecie());
        if (m.getRaza() != null && !m.getRaza().isBlank()) {
            especieRaza += "  ·  " + m.getRaza();
        }
        Label lblEspecieRaza = new Label(especieRaza);
        lblEspecieRaza.setStyle("-fx-font-size: 11px; -fx-text-fill: #5a7585;");

        // Sexo · Edad · Peso
        StringBuilder detalle = new StringBuilder();
        if (m.getSexo() != null && !m.getSexo().isBlank()) {
            detalle.append(m.getSexo());
        }
        int edad = m.calcularEdad();
        if (edad > 0) {
            if (detalle.length() > 0) detalle.append("  ·  ");
            detalle.append(edad).append(edad == 1 ? " año" : " años");
        }
        if (m.getPeso() > 0) {
            if (detalle.length() > 0) detalle.append("  ·  ");
            detalle.append(String.format("%.1f kg", m.getPeso()));
        }
        if (m.getMicrochip() != null && !m.getMicrochip().isBlank()) {
            if (detalle.length() > 0) detalle.append("  ·  ");
            detalle.append("Chip: ").append(m.getMicrochip());
        }

        VBox info = new VBox(3, lblNombre, lblEspecieRaza);
        if (detalle.length() > 0) {
            Label lblDetalle = new Label(detalle.toString());
            lblDetalle.setStyle("-fx-font-size: 11px; -fx-text-fill: #5a7585;");
            info.getChildren().add(lblDetalle);
        }

        VBox card = new VBox(info);
        card.setStyle(
                "-fx-border-color: #2b87a0;" +
                "-fx-border-width: 0 0 0 3;" +
                "-fx-padding: 8 12 8 14;" +
                "-fx-background-color: #f4f8fb;" +
                "-fx-background-radius: 0 6 6 0;");
        return card;
    }

    private String emojiEspecie(String especie) {
        if (especie == null) return "🐾";
        return switch (especie.toLowerCase()) {
            case "canino", "perro" -> "🐕";
            case "felino", "gato"  -> "🐈";
            case "ave", "pájaro"   -> "🐦";
            case "conejo"          -> "🐇";
            case "reptil"          -> "🦎";
            default                -> "🐾";
        };
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblHeaderNombre.getScene().getWindow()).close();
    }

    private String orDash(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }
}
