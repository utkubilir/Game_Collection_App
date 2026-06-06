package Dao;

import Model.Kullanici;
import Util.VeritabaniBaglantisi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data access for the {@code kullanicilar} (users) table.
 */
public class KullaniciDao {

    /** Returns the matching user (id, username, role) when the credentials are valid. */
    public Optional<Kullanici> dogrula(String kullaniciAdi, String sifre) {
        String sql = "SELECT id, is_admin FROM kullanicilar WHERE kullanici_adi = ? AND sifre = ?";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kullaniciAdi);
            pstmt.setString(2, sifre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Kullanici k = new Kullanici(rs.getInt("id"), kullaniciAdi, null);
                    k.setAdmin(rs.getBoolean("is_admin"));
                    return Optional.of(k);
                }
            }
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Giriş doğrulanamadı.", e);
        }
        return Optional.empty();
    }

    public boolean kullaniciAdiVarMi(String kullaniciAdi) {
        String sql = "SELECT COUNT(1) FROM kullanicilar WHERE kullanici_adi = ?";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kullaniciAdi);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Kullanıcı adı sorgulanamadı.", e);
        }
    }

    /** Inserts a new user and returns the generated id. */
    public int ekle(String kullaniciAdi, String sifre) {
        String sql = "INSERT INTO kullanicilar (kullanici_adi, sifre) VALUES (?, ?)";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, kullaniciAdi);
            pstmt.setString(2, sifre);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BenzersizlikHatasi("Bu kullanıcı adı zaten alınmış.", e);
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Kullanıcı kaydedilemedi.", e);
        }
    }

    public List<Kullanici> hepsiniGetir() {
        String sql = "SELECT id, kullanici_adi, kayit_tarihi, is_admin FROM kullanicilar ORDER BY id";
        List<Kullanici> sonuc = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("kayit_tarihi");
                String tarih = (ts == null) ? "N/A" : fmt.format(ts);
                Kullanici k = new Kullanici(rs.getInt("id"), rs.getString("kullanici_adi"), tarih);
                k.setAdmin(rs.getBoolean("is_admin"));
                sonuc.add(k);
            }
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Kullanıcılar yüklenemedi.", e);
        }
        return sonuc;
    }

    public void sil(int id) {
        calistir("DELETE FROM kullanicilar WHERE id = ?", id, "Kullanıcı silinemedi.");
    }

    public void rolGuncelle(int id, boolean admin) {
        String sql = "UPDATE kullanicilar SET is_admin = ? WHERE id = ?";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, admin);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Rol güncellenemedi.", e);
        }
    }

    public void sifreGuncelle(int id, String yeniSifre) {
        String sql = "UPDATE kullanicilar SET sifre = ? WHERE id = ?";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, yeniSifre);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Şifre güncellenemedi.", e);
        }
    }

    public boolean sifreDogruMu(int id, String sifre) {
        String sql = "SELECT COUNT(1) FROM kullanicilar WHERE id = ? AND sifre = ?";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, sifre);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Şifre doğrulanamadı.", e);
        }
    }

    private void calistir(String sql, int id, String hataMesaji) {
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new VeriErisimHatasi(hataMesaji, e);
        }
    }
}
