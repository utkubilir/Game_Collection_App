package Util;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Kullanici {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty kullaniciAdi;

    public Kullanici(int id, String kullaniciAdi) {
        this.id = new SimpleIntegerProperty(id);
        this.kullaniciAdi = new SimpleStringProperty(kullaniciAdi);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }
    
    public void setId(int id) {
        this.id.set(id);
    }


    public String getKullaniciAdi() {
        return kullaniciAdi.get();
    }

    public SimpleStringProperty kullaniciAdiProperty() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi.set(kullaniciAdi);
    }
}
