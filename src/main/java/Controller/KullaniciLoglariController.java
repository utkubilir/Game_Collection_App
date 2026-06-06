package Controller;

import Dao.LogDao;
import Dao.VeriErisimHatasi;
import Model.Kullanici;
import Util.Mesaj;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class KullaniciLoglariController implements Initializable {

    @FXML private Label logBaslikLabel;
    @FXML private ListView<String> logListView;

    private final LogDao logDao = new LogDao();
    private final ObservableList<String> logListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logListView.setItems(logListesi);
        logListView.setPlaceholder(new Label("Bu kullanıcı için kayıt bulunmuyor."));
    }

    public void initData(Kullanici kullanici) {
        logBaslikLabel.setText(kullanici.getKullaniciAdi() + " Adlı Kullanıcının Aktivite Logları");
        loglariYukle(kullanici.getId());
    }

    private void loglariYukle(int kullaniciId) {
        try {
            logListesi.setAll(logDao.kullaniciLoglari(kullaniciId));
        } catch (VeriErisimHatasi e) {
            logListesi.clear();
            Mesaj.hata("Veritabanı Hatası", "Loglar yüklenemedi.");
        }
    }
}
