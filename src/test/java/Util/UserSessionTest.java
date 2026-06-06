package Util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserSessionTest {

    @BeforeEach
    @AfterEach
    void temizle() {
        UserSession.cleanUserSession();
    }

    @Test
    void createInstance_degerleri_saklar() {
        UserSession.createInstance(42, "ahmet");
        assertEquals(42, UserSession.getInstance().getUserId());
        assertEquals("ahmet", UserSession.getInstance().getUserName());
    }

    @Test
    void createInstance_ayni_ornegi_gunceller() {
        UserSession.createInstance(1, "ilk");
        UserSession.createInstance(2, "ikinci");
        assertEquals(2, UserSession.getInstance().getUserId());
        assertEquals("ikinci", UserSession.getInstance().getUserName());
    }

    @Test
    void getInstance_oturum_yokken_hata_firlatir() {
        assertThrows(IllegalStateException.class, UserSession::getInstance);
    }
}
