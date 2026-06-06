package Controller;

import Dao.BenzersizlikHatasi;
import Dao.OyunDao;
import Dao.VeriErisimHatasi;
import Model.Oyun;
import Util.Dogrulama;
import Util.LogYoneticisi;
import Util.Mesaj;
import Util.UserSession;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OyunFormuController implements Initializable {

    @FXML private Label formBaslikLabel;
    @FXML private TextField titleField, genreField, developerField, publisherField, platformsField, steamidField, releaseYearField, playtimeField, formatField, languageField, ratingField;
    @FXML private TextArea translatorsArea, tagsArea;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button kaydetButton;

    private final OyunDao oyunDao = new OyunDao();
    private Oyun duzenlenecekOyun;
    private Runnable onFormClosedCallback;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusComboBox.setItems(FXCollections.observableArrayList(
            "Kütüphanede", "Oynanıyor", "Bitti", "Platinlendi", "Bırakıldı", "Sonra Oynanacak"
        ));
    }

    public void setDuzenlenecekOyun(Oyun oyun) {
        this.duzenlenecekOyun = oyun;
        formBaslikLabel.setText("Oyunu Düzenle");

        titleField.setText(oyun.getTitle());
        genreField.setText(oyun.getGenre());
        developerField.setText(oyun.getDeveloper());
        publisherField.setText(oyun.getPublisher());
        platformsField.setText(oyun.getPlatforms());
        translatorsArea.setText(oyun.getTranslators());
        steamidField.setText(oyun.getSteamid());
        releaseYearField.setText(oyun.getReleaseYear() == 0 ? "" : String.valueOf(oyun.getReleaseYear()));
        playtimeField.setText(oyun.getPlaytime());
        formatField.setText(oyun.getFormat());
        languageField.setText(oyun.getLanguage());
        ratingField.setText(oyun.getRating() == 0 ? "" : String.valueOf(oyun.getRating()));
        tagsArea.setText(oyun.getTags());
        statusComboBox.setValue(oyun.getStatus());
    }

    public void setOnFormClosed(Runnable callback) {
        this.onFormClosedCallback = callback;
    }

    @FXML
    void handleKaydet(ActionEvent event) {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            Mesaj.hata("Hata", "Başlık alanı boş bırakılamaz.");
            return;
        }

        String puanHata = Dogrulama.puanHatasi(ratingField.getText());
        if (puanHata != null) {
            Mesaj.hata("Hata", puanHata);
            return;
        }

        String yilHata = Dogrulama.yilHatasi(releaseYearField.getText());
        if (yilHata != null) {
            Mesaj.hata("Hata", yilHata);
            return;
        }

        int userId = UserSession.getInstance().getUserId();
        Oyun oyun = formdanOyun();
        try {
            if (duzenlenecekOyun == null) {
                oyunDao.ekle(oyun, userId);
                LogYoneticisi.logla(userId, "'" + oyun.getTitle() + "' adlı yeni bir oyun ekledi.");
                Mesaj.bilgi("Başarılı", "Oyun başarıyla eklendi.");
            } else {
                oyun.setId(duzenlenecekOyun.getId());
                oyunDao.guncelle(oyun, userId);
                LogYoneticisi.logla(userId, "'" + oyun.getTitle() + "' adlı oyunu güncelledi.");
                Mesaj.bilgi("Başarılı", "Oyun başarıyla güncellendi.");
            }
            if (onFormClosedCallback != null) {
                onFormClosedCallback.run();
            }
            closeWindow();
        } catch (BenzersizlikHatasi e) {
            Mesaj.hata("Hata", e.getMessage());
        } catch (VeriErisimHatasi e) {
            Mesaj.hata("Veritabanı Hatası", "İşlem sırasında bir hata oluştu.");
        }
    }

    private Oyun formdanOyun() {
        Oyun oyun = new Oyun();
        oyun.setTitle(titleField.getText());
        oyun.setGenre(genreField.getText());
        oyun.setDeveloper(developerField.getText());
        oyun.setPublisher(publisherField.getText());
        oyun.setPlatforms(platformsField.getText());
        oyun.setTranslators(translatorsArea.getText());
        oyun.setSteamid(steamidField.getText());
        oyun.setReleaseYear(tamSayi(releaseYearField.getText()));
        oyun.setPlaytime(playtimeField.getText());
        oyun.setFormat(formatField.getText());
        oyun.setLanguage(languageField.getText());
        oyun.setRating(tamSayi(ratingField.getText()));
        oyun.setTags(tagsArea.getText());
        oyun.setStatus(statusComboBox.getValue() == null ? "Kütüphanede" : statusComboBox.getValue());
        return oyun;
    }

    /** Parses an already-validated numeric field; empty means 0. */
    private int tamSayi(String metin) {
        return (metin == null || metin.trim().isEmpty()) ? 0 : Integer.parseInt(metin.trim());
    }

    @FXML
    void handleIptal(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) kaydetButton.getScene().getWindow()).close();
    }
}
