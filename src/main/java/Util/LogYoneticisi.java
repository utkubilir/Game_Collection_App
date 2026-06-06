package Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogYoneticisi {

    public static void logla(int kullaniciId, String mesaj) {
        String sql = "INSERT INTO kullanici_loglari (kullanici_id, log_mesaji) VALUES (?, ?)";
        
        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, kullaniciId);
            pstmt.setString(2, mesaj);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Log kaydedilirken veritabanı hatası oluştu.");
            e.printStackTrace();
        }
    }
}
