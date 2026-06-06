package Dao;

import Model.Oyun;
import Util.Dogrulama;
import Util.VeritabaniBaglantisi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data access for the {@code oyunlar} (games) table.
 */
public class OyunDao {

    private static final Logger log = LoggerFactory.getLogger(OyunDao.class);

    private static final String SUTUNLAR =
            "kullanici_id, title, genre, developer, publisher, platforms, translators, steamid, "
            + "release_year, playtime, format, language, rating, tags, status";

    private static final String INSERT =
            "INSERT INTO oyunlar (" + SUTUNLAR + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPSERT = INSERT
            + " ON DUPLICATE KEY UPDATE genre=VALUES(genre), developer=VALUES(developer), "
            + "publisher=VALUES(publisher), platforms=VALUES(platforms), translators=VALUES(translators), "
            + "steamid=VALUES(steamid), release_year=VALUES(release_year), playtime=VALUES(playtime), "
            + "format=VALUES(format), language=VALUES(language), rating=VALUES(rating), tags=VALUES(tags), "
            + "status=VALUES(status)";

    public List<Oyun> kullaniciOyunlari(int kullaniciId) {
        String sql = "SELECT * FROM oyunlar WHERE kullanici_id = ?";
        List<Oyun> sonuc = new ArrayList<>();
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kullaniciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sonuc.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Oyunlar yüklenemedi.", e);
        }
        return sonuc;
    }

    /** All games joined with the owner's name; pass {@code null} for every user, or a user id. */
    public List<Oyun> tumOyunlar(Integer kullaniciId) {
        String base = "SELECT o.*, u.kullanici_adi AS ekleyen_kullanici "
                + "FROM oyunlar o JOIN kullanicilar u ON o.kullanici_id = u.id";
        String sql = (kullaniciId == null) ? base : base + " WHERE u.id = ?";
        List<Oyun> sonuc = new ArrayList<>();
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (kullaniciId != null) {
                pstmt.setInt(1, kullaniciId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Oyun oyun = map(rs);
                    oyun.setEkleyenKullanici(rs.getString("ekleyen_kullanici"));
                    sonuc.add(oyun);
                }
            }
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Oyunlar yüklenemedi.", e);
        }
        return sonuc;
    }

    public void ekle(Oyun oyun, int kullaniciId) {
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(INSERT)) {
            parametreleriAyarla(pstmt, oyun, kullaniciId);
            pstmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BenzersizlikHatasi("Bu başlıkta bir oyun zaten kütüphanenizde var.", e);
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Oyun eklenemedi.", e);
        }
    }

    public void guncelle(Oyun oyun, int kullaniciId) {
        String sql = "UPDATE oyunlar SET title=?, genre=?, developer=?, publisher=?, platforms=?, "
                + "translators=?, steamid=?, release_year=?, playtime=?, format=?, language=?, "
                + "rating=?, tags=?, status=? WHERE id = ? AND kullanici_id = ?";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = oyunAlanlariniAyarla(pstmt, oyun, 1);
            pstmt.setInt(i++, oyun.getId());
            pstmt.setInt(i, kullaniciId);
            pstmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BenzersizlikHatasi("Bu başlıkta bir oyun zaten kütüphanenizde var.", e);
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Oyun güncellenemedi.", e);
        }
    }

    public void sil(int oyunId, int kullaniciId) {
        String sql = "DELETE FROM oyunlar WHERE id = ? AND kullanici_id = ?";
        try (Connection conn = VeritabaniBaglantisi.ac();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, oyunId);
            pstmt.setInt(2, kullaniciId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new VeriErisimHatasi("Oyun silinemedi.", e);
        }
    }

    /**
     * Imports games in a single transaction (all-or-nothing). Invalid ratings/years are
     * sanitized, untitled rows are skipped, and existing (user, title) rows are updated
     * instead of duplicated.
     *
     * @return the number of rows sent to the database
     */
    public int iceAktar(List<Oyun> oyunlar, int kullaniciId) {
        int sayac = 0;
        Connection conn = null;
        try {
            conn = VeritabaniBaglantisi.ac();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(UPSERT)) {
                for (Oyun oyun : oyunlar) {
                    if (oyun == null || oyun.getTitle() == null || oyun.getTitle().isBlank()) {
                        continue;
                    }
                    temizle(oyun);
                    parametreleriAyarla(pstmt, oyun, kullaniciId);
                    pstmt.addBatch();
                    sayac++;
                }
                pstmt.executeBatch();
            }
            conn.commit();
            return sayac;
        } catch (SQLException e) {
            geriAl(conn);
            throw new VeriErisimHatasi("İçe aktarma başarısız oldu, değişiklikler geri alındı.", e);
        } finally {
            kapat(conn);
        }
    }

    /** Clamps imported values into valid ranges instead of rejecting the whole file. */
    static void temizle(Oyun oyun) {
        oyun.setRating(Math.max(0, Math.min(Dogrulama.PUAN_MAX, oyun.getRating())));
        int yil = oyun.getReleaseYear();
        if (yil != 0 && (yil < Dogrulama.YIL_MIN || yil > Dogrulama.gelecekYilSiniri())) {
            oyun.setReleaseYear(0);
        }
    }

    private void parametreleriAyarla(PreparedStatement pstmt, Oyun oyun, int kullaniciId) throws SQLException {
        pstmt.setInt(1, kullaniciId);
        oyunAlanlariniAyarla(pstmt, oyun, 2);
    }

    /** Sets the 14 game columns starting at {@code start}; returns the next free index. */
    private int oyunAlanlariniAyarla(PreparedStatement pstmt, Oyun oyun, int start) throws SQLException {
        int i = start;
        pstmt.setString(i++, oyun.getTitle());
        pstmt.setString(i++, oyun.getGenre());
        pstmt.setString(i++, oyun.getDeveloper());
        pstmt.setString(i++, oyun.getPublisher());
        pstmt.setString(i++, oyun.getPlatforms());
        pstmt.setString(i++, oyun.getTranslators());
        pstmt.setString(i++, oyun.getSteamid());
        pstmt.setInt(i++, oyun.getReleaseYear());
        pstmt.setString(i++, oyun.getPlaytime());
        pstmt.setString(i++, oyun.getFormat());
        pstmt.setString(i++, oyun.getLanguage());
        pstmt.setInt(i++, oyun.getRating());
        pstmt.setString(i++, oyun.getTags());
        pstmt.setString(i++, oyun.getStatus() == null ? "Kütüphanede" : oyun.getStatus());
        return i;
    }

    private Oyun map(ResultSet rs) throws SQLException {
        Oyun oyun = new Oyun();
        oyun.setId(rs.getInt("id"));
        oyun.setKullaniciId(rs.getInt("kullanici_id"));
        oyun.setTitle(rs.getString("title"));
        oyun.setGenre(rs.getString("genre"));
        oyun.setDeveloper(rs.getString("developer"));
        oyun.setPublisher(rs.getString("publisher"));
        oyun.setPlatforms(rs.getString("platforms"));
        oyun.setTranslators(rs.getString("translators"));
        oyun.setSteamid(rs.getString("steamid"));
        oyun.setReleaseYear(rs.getInt("release_year"));
        oyun.setPlaytime(rs.getString("playtime"));
        oyun.setFormat(rs.getString("format"));
        oyun.setLanguage(rs.getString("language"));
        oyun.setRating(rs.getInt("rating"));
        oyun.setTags(rs.getString("tags"));
        oyun.setStatus(rs.getString("status"));
        return oyun;
    }

    private void geriAl(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Geri alma (rollback) başarısız.", ex);
            }
        }
    }

    private void kapat(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                log.warn("Bağlantı kapatılamadı.", ex);
            }
        }
    }
}
