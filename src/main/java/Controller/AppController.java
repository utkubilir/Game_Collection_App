package Controller;

import Dao.KullaniciDao;
import Dao.VeriErisimHatasi;
import Model.Kullanici;
import Util.LogYoneticisi;
import Util.Mesaj;
import Util.UserSession;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordButton;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final String errorStyle = "-fx-border-color: #d9534f; -fx-border-width: 1.5; -fx-border-radius: 5;";
    private final String defaultStyle = "";

    private final KullaniciDao kullaniciDao = new KullaniciDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usernameField.textProperty().addListener((obs, oldText, newText) -> clearErrorState());
        passwordField.textProperty().addListener((obs, oldText, newText) -> clearErrorState());
        visiblePasswordField.textProperty().addListener((obs, oldText, newText) -> clearErrorState());
        javafx.application.Platform.runLater(() -> usernameField.requestFocus());
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        clearErrorState();
        showLoading(true);

        String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
        String username = usernameField.getText();

        Task<LoginResult> loginTask = new Task<>() {
            @Override
            protected LoginResult call() throws Exception {
                return performLoginValidation(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            LoginResult result = loginTask.getValue();
            if (result.isSuccess()) {
                ((Stage) loginButton.getScene().getWindow()).close(); 
                if (result.isAdmin()) {
                    openWindow("/Fxml/AdminPanel.fxml", "Admin Paneli", false);
                } else {
                    openWindow("/Fxml/AnaEkran.fxml", "Oyun Kataloğum", false);
                }
            } else {
                showError(result.getMessage());
            }
            showLoading(false); 
        });

        loginTask.setOnFailed(e -> {
            showError("Beklenmedik bir hata oluştu.");
            showLoading(false); 
            loginTask.getException().printStackTrace();
        });

        new Thread(loginTask).start();
    }

    private LoginResult performLoginValidation(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return new LoginResult(false, false, "Kullanıcı adı ve şifre boş bırakılamaz.");
        }
        try {
            Optional<Kullanici> kullanici = kullaniciDao.dogrula(username, password);
            if (kullanici.isPresent()) {
                UserSession.createInstance(kullanici.get().getId(), username);
                LogYoneticisi.logla(kullanici.get().getId(), "Sisteme giriş yaptı.");
                return new LoginResult(true, kullanici.get().isAdmin(), "Başarılı");
            }
            return new LoginResult(false, false, "Kullanıcı adı veya şifre yanlış!");
        } catch (VeriErisimHatasi e) {
            return new LoginResult(false, false, "Veritabanına bağlanılamadı. Lütfen daha sonra tekrar deneyin.");
        }
    }
    
    @FXML
    private void togglePasswordVisibility() {
        if (passwordField.isVisible()) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("Gizle");
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            togglePasswordButton.setText("Göster");
        }
    }

    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        openWindow("/Fxml/KayitFormu.fxml", "Yeni Kullanıcı Kaydı", true);
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loginButton.fire();
        }
    }

    private void showLoading(boolean isLoading) {
        loadingIndicator.setVisible(isLoading);
        loadingIndicator.setManaged(isLoading);
        loginButton.setDisable(isLoading);
        registerButton.setDisable(isLoading);
        togglePasswordButton.setDisable(isLoading);
        usernameField.setDisable(isLoading);
        passwordField.setDisable(isLoading);
        visiblePasswordField.setDisable(isLoading);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        usernameField.setStyle(errorStyle);
        passwordField.setStyle(errorStyle);
        visiblePasswordField.setStyle(errorStyle);
    }

    private void clearErrorState() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        usernameField.setStyle(defaultStyle);
        passwordField.setStyle(defaultStyle);
        visiblePasswordField.setStyle(defaultStyle);
    }

    private void openWindow(String fxmlPath, String title, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), Util.I18n.bundle());
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            Util.Pencere.ikonla(stage);
            Scene scene = new Scene(root);
            Util.Tema.uygula(scene);
            stage.setScene(scene);
            if (modal) {
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } else {
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Mesaj.hata("Arayüz Hatası", "Ekran yüklenemedi: " + fxmlPath);
        }
    }
}

class LoginResult {
    private final boolean success;
    private final boolean isAdmin;
    private final String message;

    public LoginResult(boolean success, boolean isAdmin, String message) {
        this.success = success;
        this.isAdmin = isAdmin;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public boolean isAdmin() { return isAdmin; }
    public String getMessage() { return message; }
}
