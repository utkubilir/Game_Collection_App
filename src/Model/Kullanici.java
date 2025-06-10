package Model;

public class Kullanici {

    private int id;
    private String kullaniciAdi;
    private String kayitTarihi;

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
}
