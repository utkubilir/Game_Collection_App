package Model;

public class Kullanici {

    private int id;
    private String kullaniciAdi;
    private String kayitTarihi;
    private boolean admin;

    public Kullanici(int id, String kullaniciAdi, String kayitTarihi) {
        this.id = id;
        this.kullaniciAdi = kullaniciAdi;
        this.kayitTarihi = kayitTarihi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }

    public String getKayitTarihi() { return kayitTarihi; }
    public void setKayitTarihi(String kayitTarihi) { this.kayitTarihi = kayitTarihi; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    /** Display value for the "Rol" table column. */
    public String getRol() { return admin ? "Admin" : "Kullanıcı"; }
}
