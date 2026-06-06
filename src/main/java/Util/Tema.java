package Util;

import javafx.scene.Scene;

/**
 * Applies the shared external stylesheet to a scene, so theming lives in one
 * {@code css/styles.css} file instead of scattered inline styles.
 */
public final class Tema {

    private static final String CSS = "/css/styles.css";

    private Tema() {
    }

    public static void uygula(Scene scene) {
        var url = Tema.class.getResource(CSS);
        if (url != null && scene != null) {
            scene.getStylesheets().add(url.toExternalForm());
        }
    }
}
