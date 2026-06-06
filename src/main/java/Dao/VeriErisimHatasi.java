package Dao;

/**
 * Unchecked exception thrown by the DAO layer when a database operation fails
 * (connection problem, SQL error, ...). Controllers catch this to show a single,
 * user-friendly message instead of leaking {@code SQLException}/{@code NullPointerException}.
 */
public class VeriErisimHatasi extends RuntimeException {

    public VeriErisimHatasi(String mesaj, Throwable sebep) {
        super(mesaj, sebep);
    }

    public VeriErisimHatasi(String mesaj) {
        super(mesaj);
    }
}
