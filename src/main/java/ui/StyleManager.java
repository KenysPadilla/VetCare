package ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.util.Objects;

public final class StyleManager {

    private static final String APP_CSS = "/css/app.css";

    private static final String CHIP_BASE =
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 7;" +
            "-fx-padding: 3 14 3 14; -fx-cursor: hand; -fx-border-color: transparent;" +
            "-fx-pref-height: 28; -fx-min-height: 28; -fx-max-height: 28;";

    /** Ver / Historia → sólido teal (acción principal) */
    public static final String CHIP_VER =
            "-fx-background-color: #2b87a0; -fx-text-fill: white; " + CHIP_BASE;

    /** Editar / Realizado / Resultado / Pagar / Egreso → relleno lavanda 12% + contorno morado */
    public static final String CHIP_EDITAR =
            "-fx-background-color: rgba(139, 92, 246, 0.12); -fx-text-fill: #8b5cf6; " + CHIP_BASE +
            " -fx-border-color: #8b5cf6; -fx-border-width: 1; -fx-border-radius: 7;";

    /** Eliminar / Anular / Cancelar → sólido rojo (acción destructiva) */
    public static final String CHIP_ELIMINAR =
            "-fx-background-color: #e53e3e; -fx-text-fill: white; " + CHIP_BASE;

    /** Modificar → relleno teal 10% + contorno teal (acción de edición, paleta primaria) */
    public static final String CHIP_MODIFICAR =
            "-fx-background-color: rgba(43, 135, 160, 0.10); -fx-text-fill: #2b87a0; " + CHIP_BASE +
            " -fx-border-color: #2b87a0; -fx-border-width: 1; -fx-border-radius: 7;";

    /** Devuelve el estilo inline para un chip de acción por su cssClass. */
    public static String chipStyle(String cssClass) {
        return switch (cssClass) {
            case "action-chip action-chip-ver",
                 "action-chip action-chip-historia" -> CHIP_VER;
            case "action-chip action-chip-editar"   -> CHIP_EDITAR;
            case "action-chip action-chip-eliminar" -> CHIP_ELIMINAR;
            case "action-chip action-chip-modificar" -> CHIP_MODIFICAR;
            default -> null;
        };
    }

    /**
     * Aplica efecto hover de opacidad a un chip creado con setStyle() inline.
     * Al pasar el cursor el botón se suaviza levemente (mismo patrón que tarjetas).
     */
    public static void applyHover(Button btn, String cssClass) {
        if (chipStyle(cssClass) == null) return; // solo chips con estilo inline conocido
        btn.setOnMouseEntered(e -> btn.setOpacity(0.82));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
    }

    private StyleManager() {
    }

    public static void apply(Scene scene) {
        if (scene == null) {
            return;
        }
        String css = Objects.requireNonNull(
                StyleManager.class.getResource(APP_CSS),
                "No se encontró " + APP_CSS
        ).toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }
}