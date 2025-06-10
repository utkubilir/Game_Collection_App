package Controller;

import Util.LogYoneticisi;
import Util.UserSession;
import Util.VeritabaniBaglantisi;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class AppController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        validateLogin(usernameField.getText(), passwordField.getText());
    }
    
    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        openWindow("/Fxml/KayitFormu.fxml", "Yeni Kullanıcı Kaydı", true);
    }

    private void validateLogin(String username, String password) {
        String sql = "SELECT id, is_admin FROM kullanicilar WHERE kullanici_adi = ? AND sifre = ?";
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
                int userId = rs.getInt("id");
                boolean isAdmin = rs.getBoolean("is_admin");
                
                UserSession.createInstance(userId, username);
                LogYoneticisi.logla(userId, "Sisteme giriş yaptı.");

                ((Stage) loginButton.getScene().getWindow()).close();

                if (isAdmin) {
                    openWindow("/Fxml/AdminPanel.fxml", "Admin Paneli", false);
                } else {
                    openWindow("/Fxml/AnaEkran.fxml", "Oyun Kataloğum", false);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Giriş Hatası", "Kullanıcı adı veya şifre yanlış!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Giriş sırasında bir hata oluştu.");
            e.printStackTrace();
        }
    }

    private void openWindow(String fxmlPath, String title, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            if (modal) {
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } else {
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Arayüz Hatası", "Ekran yüklenemedi: " + fxmlPath);
            e.printStackTrace();
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
