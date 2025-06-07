import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    
    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Giriş Hatası", "Kullanıcı adı ve şifre alanları boş bırakılamaz!");
            return;
        }

        validateLogin(username, password);
    }

   
    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KayitFormu.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setTitle("Yeni Kullanıcı Kaydı");
            registerStage.setScene(new Scene(root));

            // Ana pencereye müdahale edilmesini engellemek için modal yap
            registerStage.initModality(Modality.APPLICATION_MODAL);
            
            // Yeni pencereyi göster ve kapanmasını bekle
            registerStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Kayıt formu açılamadı! FXML dosyasının doğru yerde olduğundan emin olun.");
        }
    }

   
    private void validateLogin(String username, String password) {
       
        String sql = "SELECT * FROM kullanicilar WHERE kullanici_adi = ? AND sifre = ?";

        
        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Bağlantı Hatası", "Veritabanı bağlantısı kurulamadı!");
                return;
            }

           
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Giriş Başarılı", "Hoş geldiniz, " + username + "!");
                System.out.println("Kullanıcı başarıyla giriş yaptı.");
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Giriş Hatası", "Kullanıcı adı veya şifre yanlış!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Giriş sırasında bir veritabanı hatası oluştu.");
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
