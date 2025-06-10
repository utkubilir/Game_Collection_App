package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VeritabaniBaglantisi {

    private static final String HOST = "sql7.freesqldatabase.com";
    private static final String PORT = "3306";
    private static final String DB_NAME = "sql7783460";
    private static final String KULLANICI_ADI = "sql7783460";
    private static final String SIFRE = "nRwAu5WH44";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL";

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
}
