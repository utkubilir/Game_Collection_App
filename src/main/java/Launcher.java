/**
 * Plain entry point for the shaded (fat) jar.
 *
 * <p>A JavaFX {@link javafx.application.Application} subclass cannot be the {@code Main-Class}
 * of an executable jar that bundles the JavaFX runtime on the classpath, because the launcher
 * checks for the JavaFX modules before {@code main} runs. Delegating through this non-Application
 * class avoids that check.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
