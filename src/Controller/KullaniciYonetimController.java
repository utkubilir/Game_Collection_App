package Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Util.Kullanici;
import Util.VeritabaniBaglantisi;


public class KullaniciYonetimController implements Initializable {

    @FXML
    private TableView<Kullanici> kullaniciTableView;

    @FXML
    private TableColumn<Kullanici, Integer> idSutun;

    @FXML
    private TableColumn<Kullanici, String> kullaniciAdiSutun;
    
    @FXML
    private TextField aramaKutusu;

    @FXML
    private Button yenileButton;

    private final ObservableList<Kullanici> kullaniciListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idSutun.setCellValueFactory(new PropertyValueFactory<>("id"));
        kullaniciAdiSutun.setCellValueFactory(new PropertyValueFactory<>("kullaniciAdi"));
        
        kullanicilariYukle();

        FilteredList<Kullanici> filtrelenmisData = new FilteredList<>(kullaniciListesi, b -> true);

        aramaKutusu.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrelenmisData.setPredicate(kullanici -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String aramaMetni = newValue.toLowerCase();
                if (kullanici.getKullaniciAdi().toLowerCase().contains(aramaMetni)) {
                    return true;
                }
                if (String.valueOf(kullanici.getId()).contains(aramaMetni)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Kullanici> siralanmisData = new SortedList<>(filtrelenmisData);
        siralanmisData.comparatorProperty().bind(kullaniciTableView.comparatorProperty());
        kullaniciTableView.setItems(siralanmisData);
    }

  
    @FXML
    void yenileButonAction(ActionEvent event) {
        kullaniciListesi.clear();
        kullanicilariYukle();
    }

    private void kullanicilariYukle() {
        String sql = "SELECT id, kullanici_adi FROM kullanicilar";

        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                kullaniciListesi.add(new Kullanici(
                    rs.getInt("id"),
                    rs.getString("kullanici_adi")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcılar yüklenirken veritabanı hatası oluştu.");
            e.printStackTrace();
        }
    }
}
