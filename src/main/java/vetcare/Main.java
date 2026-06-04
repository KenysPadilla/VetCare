package vetcare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.FirebaseService;
import ui.StyleManager;
import util.FirebasePolling;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 960, 600);
        StyleManager.apply(scene);
        stage.setTitle("VetCare");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Thread firebaseInit = new Thread(() -> {
            FirebaseService firebase = new FirebaseService();
            firebase.inicializar();
            firebase.publicarProximos30Dias();
            FirebasePolling.iniciar();
        }, "firebase-init");
        firebaseInit.setDaemon(true);
        firebaseInit.start();
    }

    @Override
    public void stop() throws Exception {
        FirebasePolling.detener();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
