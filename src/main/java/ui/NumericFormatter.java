package ui;

import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumericFormatter {

    private static final DecimalFormat CURRENCY_FMT;
    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("es", "CO"));
        CURRENCY_FMT = new DecimalFormat("#,##0.00", sym);
    }

    /** Formatea un valor como precio colombiano: $48.000,00 */
    public static String formatCurrency(double value) {
        return "$" + CURRENCY_FMT.format(value);
    }

    public static void apply(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            String digits = newVal.replaceAll("[^0-9]", "");
            String formatted = digits.isEmpty() ? "" : addThousandSeparators(digits);
            if (!formatted.equals(newVal)) {
                field.setText(formatted);
                field.positionCaret(formatted.length());
            }
        });
    }

    public static double toDouble(TextField field) {
        String raw = raw(field);
        if (raw.isEmpty()) return 0.0;
        try { return Double.parseDouble(raw); }
        catch (NumberFormatException e) { return 0.0; }
    }

    public static long toLong(TextField field) {
        String raw = raw(field);
        if (raw.isEmpty()) return 0L;
        try { return Long.parseLong(raw); }
        catch (NumberFormatException e) { return 0L; }
    }

    public static String raw(TextField field) {
        String t = field.getText();
        return t == null ? "" : t.replaceAll("\\.", "");
    }

    private static String addThousandSeparators(String digits) {
        StringBuilder sb  = new StringBuilder();
        int           len = digits.length();
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) sb.append('.');
            sb.append(digits.charAt(i));
        }
        return sb.toString();
    }
}