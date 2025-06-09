package Controller;

import Model.Kullanici;
import Model.Oyun;
import Util.VeritabaniBaglantisi;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TumOyunlarController implements Initializable {

    @FXML private TableView<Oyun> oyunlarTableView;
    @FXML private TableColumn<Oyun, String> ekleyenKullaniciColumn;
    @FXML private TableColumn<Oyun, String> baslikColumn;
    @FXML private TableColumn<Oyun, String> platformColumn;
    @FXML private TableColumn<Oyun, String> turColumn;
    @FXML private TableColumn<Oyun, String> gelistiriciColumn;
    @FXML private TableColumn<Oyun, Integer> puanColumn;
    @FXML private TextField aramaKutusu;
    @FXML private Label baslikLabel;

    private ObservableList<Oyun> tumOyunlarListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ekleyenKullaniciColumn.setCellValueFactory(new PropertyValueFactory<>("ekleyenKullanici"));
        baslikColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platforms"));
        turColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        gelistiriciColumn.setCellValueFactory(new PropertyValueFactory<>("developer"));
        puanColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        
        // Varsayılan olarak tüm oyunları yükle
        oyunlariYukle(null);
        filtreyiAyarla();
    }
    
    /**
     * Dışarıdan bir kullanıcı bilgisi alarak listeyi filtrelemek için kullanılır.
     * @param kullanici Filtrelenecek kullanıcı.
     */
    public void initData(Kullanici kullanici) {
        baslikLabel.setText(kullanici.getKullaniciAdi() + " Adlı Kullanıcının Oyunları");
        oyunlariYukle(kullanici);
    }

    private void oyunlariYukle(Kullanici kullanici) {
        tumOyunlarListesi.clear();
        
        String baseSql = "SELECT o.*, u.kullanici_adi as ekleyen_kullanici FROM oyunlar o JOIN kullanicilar u ON o.kullanici_id = u.id";
        String sql = (kullanici == null) ? baseSql : baseSql + " WHERE u.id = ?";
        
        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (kullanici != null) {
                pstmt.setInt(1, kullanici.getId());
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Oyun oyun = new Oyun();
                oyun.setId(rs.getInt("id"));
                oyun.setTitle(rs.getString("title"));
                oyun.setPlatforms(rs.getString("platforms"));
                oyun.setGenre(rs.getString("genre"));
                oyun.setDeveloper(rs.getString("developer"));
                oyun.setRating(rs.getInt("rating"));
                oyun.setEkleyenKullanici(rs.getString("ekleyen_kullanici"));
                tumOyunlarListesi.add(oyun);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtreyiAyarla() {
        FilteredList<Oyun> filtrelenmisData = new FilteredList<>(tumOyunlarListesi, b -> true);
        aramaKutusu.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrelenmisData.setPredicate(oyun -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (oyun.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                if (oyun.getEkleyenKullanici().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        SortedList<Oyun> siralanmisData = new SortedList<>(filtrelenmisData);
        siralanmisData.comparatorProperty().bind(oyunlarTableView.comparatorProperty());
        oyunlarTableView.setItems(siralanmisData);
    }
}
