package Util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Central access point for localized text.
 *
 * <p>The active locale defaults to Turkish and can be overridden with the system property
 * {@code -Dapp.locale=en} (or via {@link #setLocale(Locale)}). FXML views pull static text from
 * the bundle returned by {@link #bundle()} using {@code %key} references.
 */
public final class I18n {

    private static final String BASE = "i18n.messages";
    private static Locale locale = Locale.forLanguageTag(System.getProperty("app.locale", "tr"));

    private I18n() {
    }

    public static ResourceBundle bundle() {
        return ResourceBundle.getBundle(BASE, locale);
    }

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
    }

    public static Locale getLocale() {
        return locale;
    }

    /** Returns the translated string for {@code key}, or the key itself if it is missing. */
    public static String t(String key) {
        try {
            return bundle().getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
