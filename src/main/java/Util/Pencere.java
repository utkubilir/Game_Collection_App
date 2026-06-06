package Util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Small helpers for application windows (e.g. attaching the app icon).
 */
public final class Pencere {

    private static Image ikon;

    private Pencere() {
    }

    /** Adds the application icon to the given stage (no-op if the icon can't be loaded). */
    public static void ikonla(Stage stage) {
        try {
            if (ikon == null) {
                var in = Pencere.class.getResourceAsStream("/images/app-icon.png");
                if (in != null) {
                    ikon = new Image(in);
                }
            }
            if (ikon != null && stage != null) {
                stage.getIcons().add(ikon);
            }
        } catch (Exception ignored) {
            // An icon is cosmetic; never let it break window creation.
        }
    }
}
