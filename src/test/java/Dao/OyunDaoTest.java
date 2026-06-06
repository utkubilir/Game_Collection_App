package Dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import Model.Oyun;
import Util.Dogrulama;
import java.time.Year;
import org.junit.jupiter.api.Test;

/** Tests the import-sanitization logic (no database needed). */
class OyunDaoTest {

    private Oyun oyun(int rating, int yil) {
        Oyun o = new Oyun();
        o.setTitle("Test");
        o.setRating(rating);
        o.setReleaseYear(yil);
        return o;
    }

    @Test
    void puan_ust_sinira_clamplenir() {
        Oyun o = oyun(999, 2020);
        OyunDao.temizle(o);
        assertEquals(Dogrulama.PUAN_MAX, o.getRating());
    }

    @Test
    void negatif_puan_sifirlanir() {
        Oyun o = oyun(-5, 2020);
        OyunDao.temizle(o);
        assertEquals(0, o.getRating());
    }

    @Test
    void gecerli_puan_korunur() {
        Oyun o = oyun(7, 2020);
        OyunDao.temizle(o);
        assertEquals(7, o.getRating());
    }

    @Test
    void aralik_disi_yil_sifirlanir() {
        Oyun eski = oyun(5, 1800);
        OyunDao.temizle(eski);
        assertEquals(0, eski.getReleaseYear());

        Oyun gelecek = oyun(5, Year.now().getValue() + 50);
        OyunDao.temizle(gelecek);
        assertEquals(0, gelecek.getReleaseYear());
    }

    @Test
    void gecerli_yil_korunur() {
        Oyun o = oyun(5, 2015);
        OyunDao.temizle(o);
        assertEquals(2015, o.getReleaseYear());
    }

    @Test
    void bos_yil_korunur() {
        Oyun o = oyun(5, 0);
        OyunDao.temizle(o);
        assertEquals(0, o.getReleaseYear());
    }
}
