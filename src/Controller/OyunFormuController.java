package Controller;

import Model.Oyun;
import Util.LogYoneticisi;
import Util.UserSession;
import Util.VeritabaniBaglantisi;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
            showAlert(Alert.AlertType.ERROR, "Hata", "Başlık alanı boş bırakılamaz.");
            return;
        }

        try {
            if (duzenlenecekOyun == null) {
                yeniOyunEkle();
            } else {
                oyunuGuncelle();
            }

            if (onFormClosedCallback != null) {
                onFormClosedCallback.run();
            }
            closeWindow();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void yeniOyunEkle() throws SQLException {
        String sql = "INSERT INTO oyunlar (kullanici_id, title, genre, developer, publisher, platforms, translators, steamid, release_year, playtime, format, language, rating, tags, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = VeritabaniBaglantisi.baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            prepareStatament(pstmt, false);
            pstmt.executeUpdate();
            LogYoneticisi.logla(UserSession.getInstance().getUserId(), "'" + titleField.getText() + "' adlı yeni bir oyun ekledi.");
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Oyun başarıyla eklendi.");
        }
    }

    private void oyunuGuncelle() throws SQLException {
        String sql = "UPDATE oyunlar SET title=?, genre=?, developer=?, publisher=?, platforms=?, translators=?, steamid=?, release_year=?, playtime=?, format=?, language=?, rating=?, tags=?, status=? WHERE id = ?";
        try (Connection conn = VeritabaniBaglantisi.baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            prepareStatament(pstmt, true);
            pstmt.executeUpdate();
            LogYoneticisi.logla(UserSession.getInstance().getUserId(), "'" + titleField.getText() + "' adlı oyunu güncelledi.");
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Oyun başarıyla güncellendi.");
        }
    }
    
    private void prepareStatament(PreparedStatement pstmt, boolean isUpdate) throws SQLException {
        int year = releaseYearField.getText().trim().isEmpty() ? 0 : Integer.parseInt(releaseYearField.getText().trim());
        int rating = ratingField.getText().trim().isEmpty() ? 0 : Integer.parseInt(ratingField.getText().trim());
        String status = statusComboBox.getValue() == null ? "Kütüphanede" : statusComboBox.getValue();
        int paramIndex = 1;

        if (!isUpdate) {
            pstmt.setInt(paramIndex++, UserSession.getInstance().getUserId());
        }

        pstmt.setString(paramIndex++, titleField.getText());
        pstmt.setString(paramIndex++, genreField.getText());
        pstmt.setString(paramIndex++, developerField.getText());
        pstmt.setString(paramIndex++, publisherField.getText());
        pstmt.setString(paramIndex++, platformsField.getText());
        pstmt.setString(paramIndex++, translatorsArea.getText());
        pstmt.setString(paramIndex++, steamidField.getText());
        pstmt.setInt(paramIndex++, year);
        pstmt.setString(paramIndex++, playtimeField.getText());
        pstmt.setString(paramIndex++, formatField.getText());
        pstmt.setString(paramIndex++, languageField.getText());
        pstmt.setInt(paramIndex++, rating);
        pstmt.setString(paramIndex++, tagsArea.getText());
        pstmt.setString(paramIndex++, status);

        if (isUpdate) {
            pstmt.setInt(paramIndex, duzenlenecekOyun.getId());
        }
    }

    @FXML
    void handleIptal(ActionEvent event) {
        closeWindow();
    }
    
    private void closeWindow() {
        ((Stage) kaydetButton.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
