package Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminPanelController {

    @FXML private AnchorPane anaIcerikPane;

    @FXML
    void kullanicilariYonetButonAction(ActionEvent event) {
        loadPageIntoPane("/Fxml/KullaniciYonetim.fxml");
    }

    @FXML
    void tumOyunlariGoruntuleAction(ActionEvent event) {
        openWindow("/Fxml/TumOyunlar.fxml", "Tüm Kullanıcıların Oyunları");
    }

    @FXML
    void cikisYapButonAction(ActionEvent event) {
        Stage stage = (Stage) anaIcerikPane.getScene().getWindow();
        stage.close();
    }

  
    private void loadPageIntoPane(String fxmlFileName) {
        try {
            Node page = FXMLLoader.load(getClass().getResource(fxmlFileName));
            if (page != null) {
                anaIcerikPane.getChildren().setAll(page);

                AnchorPane.setTopAnchor(page, 0.0);
                AnchorPane.setBottomAnchor(page, 0.0);
                AnchorPane.setLeftAnchor(page, 0.0);
                AnchorPane.setRightAnchor(page, 0.0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
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
