package Controller;

import Model.Oyun;
import Util.LogYoneticisi;
import Util.UserSession;
import Util.VeritabaniBaglantisi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AnaEkranController implements Initializable {

    @FXML private TableView<Oyun> oyunlarTableView;
    @FXML private TableColumn<Oyun, String> baslikColumn, platformColumn, turColumn, gelistiriciColumn, oynamaSuresiColumn;
    @FXML private TableColumn<Oyun, Integer> puanColumn, cikisYiliColumn;
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
        openOyunForm(null);
    }

    @FXML
    void handleOyunDuzenle(ActionEvent event) {
        Oyun seciliOyun = oyunlarTableView.getSelectionModel().getSelectedItem();
        if (seciliOyun == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen düzenlemek için bir oyun seçin.");
            return;
        }
        openOyunForm(seciliOyun);
    }
    
    private void openOyunForm(Oyun oyun) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/OyunFormu.fxml"));
            Parent root = loader.load();

            OyunFormuController controller = loader.getController();
            controller.setOnFormClosed(this::oyunlariYukle);
            if (oyun != null) {
                controller.setDuzenlenecekOyun(oyun);
            }

            Stage stage = new Stage();
            stage.setTitle(oyun == null ? "Yeni Oyun Ekle" : "Oyunu Düzenle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Arayüz Hatası", "Oyun formu yüklenemedi.");
        }
    }

    @FXML
    void handleOyunSil(ActionEvent event) {
        Oyun seciliOyun = oyunlarTableView.getSelectionModel().getSelectedItem();
        if (seciliOyun == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen silmek için bir oyun seçin.");
            return;
        }
        Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Silme Onayı", "'" + seciliOyun.getTitle() + "' adlı oyunu silmek istediğinizden emin misiniz?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM oyunlar WHERE id = ? AND kullanici_id = ?";
            try (Connection conn = VeritabaniBaglantisi.baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, seciliOyun.getId());
                pstmt.setInt(2, UserSession.getInstance().getUserId());
                if (pstmt.executeUpdate() > 0) {
                    LogYoneticisi.logla(UserSession.getInstance().getUserId(), "'" + seciliOyun.getTitle() + "' adlı oyunu sildi.");
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
        try (Connection conn = VeritabaniBaglantisi.baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, UserSession.getInstance().getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                oyunListesi.add(oyunFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Oyun oyunFromResultSet(ResultSet rs) throws SQLException {
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
        return oyun;
    }
    
    @FXML void handleImportJson(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("JSON Dosyasını İçe Aktar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Dosyaları", "*.json"));
        File file = fileChooser.showOpenDialog(oyunlarTableView.getScene().getWindow());

        if (file != null) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type oyunListesiTipi = new TypeToken<List<Oyun>>(){}.getType();
                List<Oyun> iceAktarilanOyunlar = gson.fromJson(reader, oyunListesiTipi);

                if (iceAktarilanOyunlar != null) {
                    iceAktarilanOyunlar.forEach(this::oyunuVeritabaninaKaydet);
                    oyunlariYukle();
                    showAlert(Alert.AlertType.INFORMATION, "Başarılı", iceAktarilanOyunlar.size() + " oyun başarıyla içe aktarıldı.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Hata", "JSON dosyası okunurken veya işlenirken bir hata oluştu.");
                e.printStackTrace();
            }
        }
    }
    
    @FXML void handleExportJson(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Oyun Listesini Dışa Aktar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Dosyaları", "*.json"));
        fileChooser.setInitialFileName("oyun_katalogum.json");
        File file = fileChooser.showSaveDialog(oyunlarTableView.getScene().getWindow());

        if (file != null) {
            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(oyunListesi, writer);
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Oyun listeniz başarıyla dışa aktarıldı.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Dosya yazılırken bir hata oluştu.");
                e.printStackTrace();
            }
        }
    }

    private void oyunuVeritabaninaKaydet(Oyun oyun) {
        String sql = "INSERT INTO oyunlar (kullanici_id, title, genre, developer, publisher, platforms, translators, steamid, release_year, playtime, format, language, rating, tags) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = VeritabaniBaglantisi.baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, UserSession.getInstance().getUserId());
            pstmt.setString(2, oyun.getTitle());
            pstmt.setString(3, oyun.getGenre());
            pstmt.setString(4, oyun.getDeveloper());
            pstmt.setString(5, oyun.getPublisher());
            pstmt.setString(6, oyun.getPlatforms());
            pstmt.setString(7, oyun.getTranslators());
            pstmt.setString(8, oyun.getSteamid());
            pstmt.setInt(9, oyun.getReleaseYear());
            pstmt.setString(10, oyun.getPlaytime());
            pstmt.setString(11, oyun.getFormat());
            pstmt.setString(12, oyun.getLanguage());
            pstmt.setInt(13, oyun.getRating());
            pstmt.setString(14, oyun.getTags());
            pstmt.executeUpdate();
        } catch (SQLException e){
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
                if (oyun.getGenre() != null && oyun.getGenre().toLowerCase().contains(lowerCaseFilter)) return true;
                if (oyun.getPlatforms() != null && oyun.getPlatforms().toLowerCase().contains(lowerCaseFilter)) return true;
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
