package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public final class ModalHelper {

    private ModalHelper() {
    }

    public static <T> T open(String fxmlPath, String title, Consumer<T> onLoaded) throws IOException {
        FXMLLoader loader = new FXMLLoader(ModalHelper.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();
        if (onLoaded != null) {
            onLoaded.accept(controller);
        }
        Stage stage = new Stage();
        stage.setTitle(title);
        Scene scene = new Scene(root);
        StyleManager.apply(scene);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
        return controller;
    }
}
