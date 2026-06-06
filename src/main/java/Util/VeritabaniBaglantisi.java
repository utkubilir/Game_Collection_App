package Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides JDBC connections to the application database.
 *
 * <p>Connection settings are read once, in this order of precedence:
 * <ol>
 *     <li>Environment variables (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)</li>
 *     <li>A {@code config.properties} file in the working directory</li>
 *     <li>A {@code config.properties} file on the classpath</li>
 * </ol>
 * No credentials are hard-coded in source control. See {@code config.properties.example}.
 */
public class VeritabaniBaglantisi {

    private static final Properties CONFIG = loadConfig();

    private static final String HOST = get("DB_HOST", "localhost");
    private static final String PORT = get("DB_PORT", "3306");
    private static final String DB_NAME = get("DB_NAME", "");
    private static final String KULLANICI_ADI = get("DB_USER", "");
    private static final String SIFRE = get("DB_PASSWORD", "");

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL";

    public static Connection baglan() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Veritabanına bağlanılamadı! Hata: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static Properties loadConfig() {
        Properties props = new Properties();

        // 1) config.properties in the current working directory (local dev, not committed).
        Path local = Path.of("config.properties");
        if (Files.exists(local)) {
            try (InputStream in = Files.newInputStream(local)) {
                props.load(in);
                return props;
            } catch (IOException e) {
                System.err.println("config.properties okunamadı: " + e.getMessage());
            }
        }

        // 2) config.properties bundled on the classpath (fallback).
        try (InputStream in = VeritabaniBaglantisi.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Sınıf yolundaki config.properties okunamadı: " + e.getMessage());
        }
        return props;
    }

    /** Environment variable wins over the properties file, which wins over the default. */
    private static String get(String key, String defaultValue) {
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return CONFIG.getProperty(key, defaultValue);
    }
}
