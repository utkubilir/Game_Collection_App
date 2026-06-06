package Util;

import Dao.LogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thin facade over {@link LogDao} for writing activity-log entries.
 * Logging must never break the user's action, so failures are logged, not propagated.
 */
public class LogYoneticisi {

    private static final Logger log = LoggerFactory.getLogger(LogYoneticisi.class);
    private static final LogDao logDao = new LogDao();

    private LogYoneticisi() {
    }

    public static void logla(int kullaniciId, String mesaj) {
        try {
            logDao.ekle(kullaniciId, mesaj);
        } catch (RuntimeException e) {
            log.warn("Aktivite logu kaydedilemedi (kullanıcı {}): {}", kullaniciId, mesaj, e);
        }
    }
}
