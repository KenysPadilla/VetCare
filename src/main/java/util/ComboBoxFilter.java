package util;

import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class ComboBoxFilter {

    private ComboBoxFilter() {}

    
    public static <T> void apply(ComboBox<T> cb, List<T> allItems,
                                 Function<T, String> display) {
        cb.setEditable(true);

        cb.setConverter(new StringConverter<T>() {
            @Override public String toString(T item) {
                return item == null ? "" : display.apply(item);
            }
            @Override public T fromString(String text) {
                if (text == null || text.isBlank()) return null;
                String lower = text.toLowerCase();
                // exacto primero, luego parcial
                return allItems.stream()
                        .filter(i -> display.apply(i).equalsIgnoreCase(text))
                        .findFirst()
                        .orElseGet(() -> allItems.stream()
                                .filter(i -> display.apply(i).toLowerCase().contains(lower))
                                .findFirst().orElse(null));
            }
        });

        cb.getItems().setAll(allItems);
        cb.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, ev -> {
            if (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.ESCAPE) return;
            String text  = cb.getEditor().getText();
            String lower = text == null ? "" : text.toLowerCase();
            List<T> filtered = allItems.stream()
                    .filter(item -> display.apply(item).toLowerCase().contains(lower))
                    .collect(Collectors.toList());
            cb.getItems().setAll(filtered);
            cb.getEditor().setText(text);
            cb.getEditor().positionCaret(text == null ? 0 : text.length());
            if (!filtered.isEmpty()) cb.show();
        });
    }

    public static void apply(ComboBox<String> cb, List<String> allItems) {
        apply(cb, allItems, s -> s);
    }
}