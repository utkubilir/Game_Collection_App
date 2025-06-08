package Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Util.VeritabaniBaglantisi;

public class KayitController {

    @FXML
    private TextField kullaniciAdiField;

    @FXML
    private PasswordField sifreField;

    @FXML
    private PasswordField sifreTekrarField;

    @FXML
    private Button kayitOlButton;
    
    @FXML
    private Label hataLabel;

    @FXML
    void handleKayitOlButtonAction(ActionEvent event) {
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText(); 
        String sifreTekrar = sifreTekrarField.getText();

        if (kullaniciAdi.isEmpty() || sifre.isEmpty() || sifreTekrar.isEmpty()) {
            hataLabel.setText("Tüm alanlar doldurulmalıdır!");
            return;
        }

        if (!sifre.equals(sifreTekrar)) {
            hataLabel.setText("Şifreler uyuşmuyor!");
            return;
        }
        
        
        kullaniciEkle(kullaniciAdi, sifre);
    }

    private void kullaniciEkle(String kullaniciAdi, String duzSifre) {
        String sql = "INSERT INTO kullanicilar (kullanici_adi, sifre) VALUES (?, ?)";

        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Bağlantı Hatası", "Veritabanı bağlantısı kurulamadı!");
                return;
            }

            pstmt.setString(1, kullaniciAdi);
            pstmt.setString(2, duzSifre);

            int etkilenenSatir = pstmt.executeUpdate();

            if (etkilenenSatir > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kullanıcı kaydı başarıyla oluşturuldu!");
                Stage stage = (Stage) kayitOlButton.getScene().getWindow();
                stage.close();
            }

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                hataLabel.setText("Bu kullanıcı adı zaten alınmış!");
            } else {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Kayıt sırasında bir hata oluştu.");
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