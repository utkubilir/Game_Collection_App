package Util;

import java.time.Year;

/**
 * Pure input-validation helpers (no UI, no database) so they can be unit-tested.
 * Each method returns {@code null} when the value is valid, or a human-readable
 * Turkish error message when it is not.
 */
public final class Dogrulama {

    public static final int KULLANICI_ADI_MIN = 3;
    public static final int KULLANICI_ADI_MAX = 50;
    public static final int SIFRE_MIN = 4;
    public static final int PUAN_MIN = 1;
    public static final int PUAN_MAX = 10;
    public static final int YIL_MIN = 1950;

    private Dogrulama() {
    }

    public static int gelecekYilSiniri() {
        return Year.now().getValue() + 5;
    }

    /** @return error message, or {@code null} if the username is valid. */
    public static String kullaniciAdiHatasi(String kullaniciAdi) {
        if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
            return "Kullanıcı adı boş bırakılamaz.";
        }
        String ad = kullaniciAdi.trim();
        if (ad.length() < KULLANICI_ADI_MIN || ad.length() > KULLANICI_ADI_MAX) {
            return "Kullanıcı adı " + KULLANICI_ADI_MIN + "-" + KULLANICI_ADI_MAX + " karakter olmalıdır.";
        }
        if (!ad.matches("[A-Za-z0-9_.-]+")) {
            return "Kullanıcı adı yalnızca harf, rakam ve . _ - içerebilir.";
        }
        return null;
    }

    /** @return error message, or {@code null} if the password is valid. */
    public static String sifreHatasi(String sifre) {
        if (sifre == null || sifre.isEmpty()) {
            return "Şifre boş bırakılamaz.";
        }
        if (sifre.length() < SIFRE_MIN) {
            return "Şifre en az " + SIFRE_MIN + " karakter olmalıdır.";
        }
        return null;
    }

    /**
     * Validates the rating text field. Empty is allowed (means "no rating", stored as 0).
     * @return error message, or {@code null} if valid.
     */
    public static String puanHatasi(String metin) {
        if (metin == null || metin.trim().isEmpty()) {
            return null;
        }
        int puan;
        try {
            puan = Integer.parseInt(metin.trim());
        } catch (NumberFormatException e) {
            return "Puan bir tam sayı olmalıdır.";
        }
        if (puan < PUAN_MIN || puan > PUAN_MAX) {
            return "Puan " + PUAN_MIN + " ile " + PUAN_MAX + " arasında olmalıdır.";
        }
        return null;
    }

    /**
     * Validates the release-year text field. Empty is allowed (stored as 0).
     * @return error message, or {@code null} if valid.
     */
    public static String yilHatasi(String metin) {
        if (metin == null || metin.trim().isEmpty()) {
            return null;
        }
        int yil;
        try {
            yil = Integer.parseInt(metin.trim());
        } catch (NumberFormatException e) {
            return "Çıkış yılı bir tam sayı olmalıdır.";
        }
        if (yil < YIL_MIN || yil > gelecekYilSiniri()) {
            return "Çıkış yılı " + YIL_MIN + " ile " + gelecekYilSiniri() + " arasında olmalıdır.";
        }
        return null;
    }
}
