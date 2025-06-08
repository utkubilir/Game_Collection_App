package Controller;

import Model.Oyun;
import Util.UserSession;
import Util.VeritabaniBaglantisi;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AnaEkranController implements Initializable {

    @FXML private TableView<Oyun> oyunlarTableView;
    @FXML private TableColumn<Oyun, String> baslikColumn;
    @FXML private TableColumn<Oyun, String> platformColumn;
    @FXML private TableColumn<Oyun, String> turColumn;
    @FXML private TableColumn<Oyun, String> gelistiriciColumn;
    @FXML private TableColumn<Oyun, Integer> puanColumn;
    @FXML private TableColumn<Oyun, String> oynamaSuresiColumn;
    @FXML private TableColumn<Oyun, Integer> cikisYiliColumn;
    @FXML private TextField aramaKutusu;

    private ObservableList<Oyun> oyunListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        baslikColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platforms"));
        turColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        gelistiriciColumn.setCellValueFactory(new PropertyValueFactory<>("developer"));
        puanColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        oynamaSuresiColumn.setCellValueFactory(new PropertyValueFactory<>("playtime"));
        cikisYiliColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

        oyunlariYukle();
        filtreyiAyarla();
    }

    @FXML
    void handleOyunEkle(ActionEvent event) {
        System.out.println("Oyun Ekle formu açılacak.");
    }

    @FXML
    void handleOyunDuzenle(ActionEvent event) {
        Oyun seciliOyun = oyunlarTableView.getSelectionModel().getSelectedItem();
        if (seciliOyun == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen düzenlemek için bir oyun seçin.");
            return;
        }
        System.out.println(seciliOyun.getTitle() + " için Düzenle formu açılacak.");
    }

    @FXML
    void handleOyunSil(ActionEvent event) {
        Oyun seciliOyun = oyunlarTableView.getSelectionModel().getSelectedItem();

        if (seciliOyun == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen silmek için bir oyun seçin.");
            return;
        }

        Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Silme Onayı", 
                "'" + seciliOyun.getTitle() + "' adlı oyunu silmek istediğinizden emin misiniz?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM oyunlar WHERE id = ?";
            try (Connection conn = VeritabaniBaglantisi.baglan();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, seciliOyun.getId());
                int etkilenenSatir = pstmt.executeUpdate();

                if (etkilenenSatir > 0) {
                    oyunListesi.remove(seciliOyun);
                    showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Oyun başarıyla silindi.");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası", "Oyun silinirken bir hata oluştu.");
                e.printStackTrace();
            }
        }
    }

    private void oyunlariYukle() {
        oyunListesi.clear();
        String sql = "SELECT * FROM oyunlar WHERE kullanici_id = ?";
        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, UserSession.getInstance().getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Oyun oyun = new Oyun();
                oyun.setId(rs.getInt("id"));
                oyun.setKullaniciId(rs.getInt("kullanici_id"));
                oyun.setTitle(rs.getString("title"));
                oyun.setGenre(rs.getString("genre"));
                oyun.setDeveloper(rs.getString("developer"));
                oyun.setPublisher(rs.getString("publisher"));
                oyun.setPlatforms(rs.getString("platforms"));
                oyun.setTranslators(rs.getString("translators"));
                oyun.setSteamid(rs.getString("steamid"));
                oyun.setReleaseYear(rs.getInt("release_year"));
                oyun.setPlaytime(rs.getString("playtime"));
                oyun.setFormat(rs.getString("format"));
                oyun.setLanguage(rs.getString("language"));
                oyun.setRating(rs.getInt("rating"));
                oyun.setTags(rs.getString("tags"));
                oyunListesi.add(oyun);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtreyiAyarla() {
        FilteredList<Oyun> filtrelenmisData = new FilteredList<>(oyunListesi, b -> true);
        aramaKutusu.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrelenmisData.setPredicate(oyun -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (oyun.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                if (oyun.getGenre().toLowerCase().contains(lowerCaseFilter)) return true;
                if (oyun.getPlatforms().toLowerCase().contains(lowerCaseFilter)) return true;
                if (oyun.getTags() != null && oyun.getTags().toLowerCase().contains(lowerCaseFilter)) return true;

                return false; 
            });
        });
        SortedList<Oyun> siralanmisData = new SortedList<>(filtrelenmisData);
        siralanmisData.comparatorProperty().bind(oyunlarTableView.comparatorProperty());
        oyunlarTableView.setItems(siralanmisData);
    }
    
    private Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
