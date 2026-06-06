package Util;

/**
 * Application metadata shown in the "About" dialog and window titles.
 */
public final class Uygulama {

    public static final String AD = "Game Collection App";
    public static final String SURUM = "1.0.0";
    public static final String GELISTIRICI = "utkubilir";

    private Uygulama() {
    }

    public static String hakkindaMetni() {
        return AD + "\nSürüm: " + SURUM + "\nGeliştirici: " + GELISTIRICI
                + "\n\nJavaFX + MySQL ile geliştirilmiş kişisel oyun kütüphanesi uygulaması.";
    }
}
