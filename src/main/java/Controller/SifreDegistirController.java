package Controller;

import Dao.KullaniciDao;
import Dao.VeriErisimHatasi;
import Util.Dogrulama;
import Util.LogYoneticisi;
import Util.Mesaj;
import Util.UserSession;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class SifreDegistirController implements Initializable {

    @FXML private PasswordField mevcutSifreField;
    @FXML private PasswordField yeniSifreField;
    @FXML private PasswordField yeniSifreTekrarField;
    @FXML private Button kaydetButton;
    @FXML private Label durumLabel;

    private final KullaniciDao kullaniciDao = new KullaniciDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        yeniSifreField.textProperty().addListener((o, a, b) -> eslesmeyiGoster());
        yeniSifreTekrarField.textProperty().addListener((o, a, b) -> eslesmeyiGoster());
    }

    @FXML
    void handleKaydet(ActionEvent event) {
        String mevcut = mevcutSifreField.getText();
        String yeni = yeniSifreField.getText();
        String yeniTekrar = yeniSifreTekrarField.getText();

        String yeniHata = Dogrulama.sifreHatasi(yeni);
        if (yeniHata != null) {
            Mesaj.hata("Hata", yeniHata);
            return;
        }
        if (!yeni.equals(yeniTekrar)) {
            Mesaj.hata("Hata", "Yeni şifreler uyuşmuyor.");
            return;
        }

        int userId = UserSession.getInstance().getUserId();
        try {
            if (!kullaniciDao.sifreDogruMu(userId, mevcut)) {
                Mesaj.hata("Hata", "Mevcut şifreniz hatalı.");
                return;
            }
            kullaniciDao.sifreGuncelle(userId, yeni);
            LogYoneticisi.logla(userId, "Şifresini değiştirdi.");
            Mesaj.bilgi("Başarılı", "Şifreniz güncellendi.");
            closeWindow();
        } catch (VeriErisimHatasi e) {
            Mesaj.hata("Veritabanı Hatası", "Şifre güncellenirken bir hata oluştu.");
        }
    }

    private void eslesmeyiGoster() {
        String yeni = yeniSifreField.getText();
        String tekrar = yeniSifreTekrarField.getText();
        if (yeni.isEmpty() && tekrar.isEmpty()) {
            durumLabel.setVisible(false);
            durumLabel.setManaged(false);
            return;
        }
        durumLabel.setVisible(true);
        durumLabel.setManaged(true);
        if (yeni.equals(tekrar)) {
            durumLabel.setText("✓ Şifreler uyuşuyor");
            durumLabel.setStyle("-fx-text-fill: green;");
        } else {
            durumLabel.setText("✗ Şifreler uyuşmuyor");
            durumLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    void handleIptal(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) kaydetButton.getScene().getWindow()).close();
    }
}
