package Controller;

import Util.LogYoneticisi;
import Util.Mesaj;
import Util.Navigasyon;
import Util.Pencere;
import Util.UserSession;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        LogYoneticisi.logla(UserSession.getInstance().getUserId(), "Sistemden çıkış yaptı.");
        UserSession.cleanUserSession();
        Navigasyon.girisEkraniniAc();
        ((Stage) anaIcerikPane.getScene().getWindow()).close();
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
            Pencere.ikonla(stage);
            Scene scene = new Scene(root);
            Util.Tema.uygula(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
             Mesaj.hata("Arayüz Hatası", "Ekran yüklenemedi: " + fxmlPath);
             e.printStackTrace();
        }
    }
}
