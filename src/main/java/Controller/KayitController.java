package Controller;

import Util.LogYoneticisi;
import Util.VeritabaniBaglantisi;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

        if (kullaniciAdi.trim().isEmpty() || sifre.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı adı ve şifre alanları boş bırakılamaz.");
            return;
        }

        if (!sifre.equals(sifreTekrar)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Girdiğiniz şifreler uyuşmuyor.");
            return;
        }

        try {
            if(kullaniciAdiVarMi(kullaniciAdi)){
                showAlert(Alert.AlertType.ERROR, "Hata", "Bu kullanıcı adı zaten alınmış. Lütfen başka bir tane seçin.");
                return;
            }

            kullaniciEkle(kullaniciAdi, sifre);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Kayıt sırasında bir hata oluştu.");
        }
    }
    
    @FXML
    void handleGirisEkraninaDon(ActionEvent event) {
        ((Stage) kayitOlButton.getScene().getWindow()).close();
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

    private boolean kullaniciAdiVarMi(String kullaniciAdi) throws SQLException {
        String sql = "SELECT count(1) FROM kullanicilar WHERE kullanici_adi = ?";
        try(Connection conn = VeritabaniBaglantisi.baglan();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, kullaniciAdi);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private void kullaniciEkle(String kullaniciAdi, String sifre) throws SQLException {
        String sql = "INSERT INTO kullanicilar (kullanici_adi, sifre) VALUES (?, ?)";

        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, kullaniciAdi);
            pstmt.setString(2, sifre);
            
            int etkilenenSatir = pstmt.executeUpdate();

            if (etkilenenSatir > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int yeniKullaniciId = generatedKeys.getInt(1);
                    LogYoneticisi.logla(yeniKullaniciId, "Yeni hesap oluşturuldu.");
                }

                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kullanıcı kaydı başarıyla oluşturuldu! Giriş yapabilirsiniz.");
                ((Stage) kayitOlButton.getScene().getWindow()).close();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
