package Controller;

import Dao.BenzersizlikHatasi;
import Dao.KullaniciDao;
import Dao.VeriErisimHatasi;
import Util.Dogrulama;
import Util.LogYoneticisi;
import Util.Mesaj;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KayitController implements Initializable {

    @FXML private TextField kullaniciAdiField;
    @FXML private PasswordField sifreField;
    @FXML private PasswordField sifreTekrarField;
    @FXML private Button kayitOlButton;
    @FXML private Label validationStatusLabel;

    private final KullaniciDao kullaniciDao = new KullaniciDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sifreField.textProperty().addListener((obs, oldVal, newVal) -> validatePasswords());
        sifreTekrarField.textProperty().addListener((obs, oldVal, newVal) -> validatePasswords());
    }

    @FXML
    void handleKayitOlButtonAction(ActionEvent event) {
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText();
        String sifreTekrar = sifreTekrarField.getText();

        String adHata = Dogrulama.kullaniciAdiHatasi(kullaniciAdi);
        if (adHata != null) {
            Mesaj.hata("Hata", adHata);
            return;
        }

        String sifreHata = Dogrulama.sifreHatasi(sifre);
        if (sifreHata != null) {
            Mesaj.hata("Hata", sifreHata);
            return;
        }

        if (!sifre.equals(sifreTekrar)) {
            Mesaj.hata("Hata", "Girdiğiniz şifreler uyuşmuyor.");
            return;
        }

        try {
            if (kullaniciDao.kullaniciAdiVarMi(kullaniciAdi)) {
                Mesaj.hata("Hata", "Bu kullanıcı adı zaten alınmış. Lütfen başka bir tane seçin.");
                return;
            }
            int yeniId = kullaniciDao.ekle(kullaniciAdi, sifre);
            LogYoneticisi.logla(yeniId, "Yeni hesap oluşturuldu.");
            Mesaj.bilgi("Başarılı", "Kullanıcı kaydı başarıyla oluşturuldu! Giriş yapabilirsiniz.");
            closeWindow();
        } catch (BenzersizlikHatasi e) {
            Mesaj.hata("Hata", "Bu kullanıcı adı zaten alınmış. Lütfen başka bir tane seçin.");
        } catch (VeriErisimHatasi e) {
            Mesaj.hata("Veritabanı Hatası", "Kayıt sırasında bir hata oluştu.");
        }
    }

    @FXML
    void handleGirisEkraninaDon(ActionEvent event) {
        closeWindow();
    }

    private void validatePasswords() {
        String sifre = sifreField.getText();
        String sifreTekrar = sifreTekrarField.getText();

        if (sifre.isEmpty() && sifreTekrar.isEmpty()) {
            validationStatusLabel.setVisible(false);
            validationStatusLabel.setManaged(false);
            return;
        }

        validationStatusLabel.setVisible(true);
        validationStatusLabel.setManaged(true);

        if (sifre.equals(sifreTekrar)) {
            validationStatusLabel.setText("✓ Şifreler uyuşuyor");
            validationStatusLabel.setStyle("-fx-text-fill: green;");
        } else {
            validationStatusLabel.setText("✗ Şifreler uyuşmuyor");
            validationStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void closeWindow() {
        ((Stage) kayitOlButton.getScene().getWindow()).close();
    }
}
