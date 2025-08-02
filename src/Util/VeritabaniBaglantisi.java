package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VeritabaniBaglantisi {

    private static String getEnvOrThrow(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }

    private static String buildUrl() {
        String host = getEnvOrThrow("DB_HOST");
        String port = getEnvOrThrow("DB_PORT");
        String dbName = getEnvOrThrow("DB_NAME");
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL";
    }

    public static Connection baglan() {
        try {
            String url = buildUrl();
            String kullaniciAdi = getEnvOrThrow("DB_USER");
            String sifre = getEnvOrThrow("DB_PASSWORD");

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, kullaniciAdi, sifre);
        } catch (IllegalStateException e) {
            System.err.println("Veritabanı yapılandırma hatası: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Veritabanına bağlanılamadı! Hata: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
