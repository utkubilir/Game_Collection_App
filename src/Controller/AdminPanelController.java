package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import Util.VeritabaniBaglantisi;

public class AdminPanelController {

    @FXML
    private AnchorPane anaIcerikPane;

    @FXML
    void kullanicilariYonetButonAction(ActionEvent event) {
        loadPage("/Fxml/KullaniciYonetim.fxml");
    }

    @FXML
    void oyunlariDuzenleButonAction(ActionEvent event) {
        System.out.println("Oyunları düzenle sayfası açılıyor...");
    }

    @FXML
    void cikisYapButonAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void loadPage(String fxmlFileName) {
        try {
            Node page = FXMLLoader.load(getClass().getResource(fxmlFileName));
            
            anaIcerikPane.getChildren().clear();
            anaIcerikPane.getChildren().add(page);

        } catch (IOException e) {
            System.err.println("Sayfa yüklenirken hata oluştu: " + fxmlFileName);
            e.printStackTrace();
        }
    }
}
