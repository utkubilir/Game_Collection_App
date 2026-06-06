package Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Window navigation helpers shared by controllers (which live in named packages and therefore
 * cannot reference the default-package {@code App} class directly).
 */
public final class Navigasyon {

    private Navigasyon() {
    }

    /** Opens a fresh login window — used by the logout flow. */
    public static void girisEkraniniAc() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Navigasyon.class.getResource("/Fxml/LoginScreen.fxml"), I18n.bundle());
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Game Collection App");
            Pencere.ikonla(stage);
            Scene scene = new Scene(root);
            Tema.uygula(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
