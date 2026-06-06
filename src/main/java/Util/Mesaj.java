package Util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Centralized dialog helpers, replacing the per-controller {@code showAlert} copies.
 */
public final class Mesaj {

    private Mesaj() {
    }

    public static void bilgi(String baslik, String mesaj) {
        goster(Alert.AlertType.INFORMATION, baslik, mesaj);
    }

    public static void uyari(String baslik, String mesaj) {
        goster(Alert.AlertType.WARNING, baslik, mesaj);
    }

    public static void hata(String baslik, String mesaj) {
        goster(Alert.AlertType.ERROR, baslik, mesaj);
    }

    /** Yes/No confirmation; returns true only if the user pressed OK. */
    public static boolean onay(String baslik, String mesaj) {
        Optional<ButtonType> sonuc = goster(Alert.AlertType.CONFIRMATION, baslik, mesaj);
        return sonuc.isPresent() && sonuc.get() == ButtonType.OK;
    }

    private static Optional<ButtonType> goster(Alert.AlertType tip, String baslik, String mesaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        return alert.showAndWait();
    }
}
