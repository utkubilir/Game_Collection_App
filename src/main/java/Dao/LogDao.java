package Dao;

import Util.VeritabaniBaglantisi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the {@code kullanici_loglari} (activity log) table.
 */
public class LogDao {

    public void ekle(int kullaniciId, String mesaj) {
        String sql = "INSERT INTO kullanici_loglari (kullanici_id, log_mesaji) VALUES (?, ?)";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kullaniciId);
            pstmt.setString(2, mesaj);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Log kaydedilemedi.", e);
        }
    }

    /** Returns the user's activity log as preformatted "date - message" lines, newest first. */
    public List<String> kullaniciLoglari(int kullaniciId) {
        String sql = "SELECT log_mesaji, log_tarihi FROM kullanici_loglari WHERE kullanici_id = ? ORDER BY log_tarihi DESC";
        List<String> sonuc = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kullaniciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tarih = rs.getTimestamp("log_tarihi") == null
                            ? "" : fmt.format(rs.getTimestamp("log_tarihi"));
                    sonuc.add(tarih + " - " + rs.getString("log_mesaji"));
                }
            }
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Loglar yüklenemedi.", e);
        }
        return sonuc;
    }
}
