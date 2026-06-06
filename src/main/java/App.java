import Util.I18n;
import Util.Pencere;
import Util.VeritabaniBaglantisi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Fxml/LoginScreen.fxml"), I18n.bundle());
            Parent root = loader.load();

            primaryStage.setTitle("Game Collection App");
            Pencere.ikonla(primaryStage);
            Scene scene = new Scene(root);
            Util.Tema.uygula(scene);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Başlatma Hatası");
            alert.setHeaderText(null);
            alert.setContentText("Giriş ekranı yüklenemedi. Uygulama kapatılacak.");
            alert.showAndWait();
        }
    }

    @Override
    public void stop() {
        // Release pooled database connections cleanly on shutdown.
        VeritabaniBaglantisi.kapat();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
