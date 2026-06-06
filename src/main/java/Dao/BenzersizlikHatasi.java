package Dao;

/**
 * Thrown when an insert/update violates a unique constraint
 * (e.g. a user already has a game with the same title, or a username is taken).
 */
public class BenzersizlikHatasi extends VeriErisimHatasi {

    public BenzersizlikHatasi(String mesaj, Throwable sebep) {
        super(mesaj, sebep);
    }
}
