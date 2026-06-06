package Util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Year;
import org.junit.jupiter.api.Test;

class DogrulamaTest {

    @Test
    void kullaniciAdi_gecerli() {
        assertNull(Dogrulama.kullaniciAdiHatasi("kullanici_1"));
        assertNull(Dogrulama.kullaniciAdiHatasi("Ali.Veli-2"));
    }

    @Test
    void kullaniciAdi_bos_veya_null_hata() {
        assertNotNull(Dogrulama.kullaniciAdiHatasi(null));
        assertNotNull(Dogrulama.kullaniciAdiHatasi("   "));
    }

    @Test
    void kullaniciAdi_cok_kisa_hata() {
        assertNotNull(Dogrulama.kullaniciAdiHatasi("ab"));
    }

    @Test
    void kullaniciAdi_gecersiz_karakter_hata() {
        assertNotNull(Dogrulama.kullaniciAdiHatasi("ali veli"));
        assertNotNull(Dogrulama.kullaniciAdiHatasi("öğrenci"));
    }

    @Test
    void sifre_gecerli() {
        assertNull(Dogrulama.sifreHatasi("abcd"));
        assertNull(Dogrulama.sifreHatasi("uzunSifre123"));
    }

    @Test
    void sifre_bos_veya_kisa_hata() {
        assertNotNull(Dogrulama.sifreHatasi(null));
        assertNotNull(Dogrulama.sifreHatasi(""));
        assertNotNull(Dogrulama.sifreHatasi("abc"));
    }

    @Test
    void puan_bos_gecerli() {
        assertNull(Dogrulama.puanHatasi(null));
        assertNull(Dogrulama.puanHatasi("   "));
    }

    @Test
    void puan_aralik_ici_gecerli() {
        assertNull(Dogrulama.puanHatasi("1"));
        assertNull(Dogrulama.puanHatasi("10"));
        assertNull(Dogrulama.puanHatasi(" 7 "));
    }

    @Test
    void puan_aralik_disi_veya_sayisal_olmayan_hata() {
        assertNotNull(Dogrulama.puanHatasi("0"));
        assertNotNull(Dogrulama.puanHatasi("11"));
        assertNotNull(Dogrulama.puanHatasi("-3"));
        assertNotNull(Dogrulama.puanHatasi("abc"));
        assertNotNull(Dogrulama.puanHatasi("5.5"));
    }

    @Test
    void yil_bos_gecerli() {
        assertNull(Dogrulama.yilHatasi(null));
        assertNull(Dogrulama.yilHatasi(""));
    }

    @Test
    void yil_makul_gecerli() {
        assertNull(Dogrulama.yilHatasi("1999"));
        assertNull(Dogrulama.yilHatasi(String.valueOf(Year.now().getValue())));
    }

    @Test
    void yil_aralik_disi_veya_sayisal_olmayan_hata() {
        assertNotNull(Dogrulama.yilHatasi("1900"));
        assertNotNull(Dogrulama.yilHatasi(String.valueOf(Year.now().getValue() + 10)));
        assertNotNull(Dogrulama.yilHatasi("yil"));
    }
}
