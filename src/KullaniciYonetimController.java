import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

/**
 * Kullanıcı yönetim sayfasının (KullaniciYonetim.fxml) controller'ı.
 * Veritabanından kullanıcıları çeker, tabloya doldurur ve arama işlevini yönetir.
 */
public class KullaniciYonetimController implements Initializable {

    @FXML
    private TableView<Kullanici> kullaniciTableView;

    @FXML
    private TableColumn<Kullanici, Integer> idSutun;

    @FXML
    private TableColumn<Kullanici, String> kullaniciAdiSutun;
    
    @FXML
    private TextField aramaKutusu;

    // Tüm kullanıcıları tutacak olan ana liste.
    private final ObservableList<Kullanici> kullaniciListesi = FXCollections.observableArrayList();

    /**
     * Bu metot, FXML dosyası yüklendikten sonra otomatik olarak çağrılır.
     * Tablo sütunlarını ve arama filtresini ayarlar.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Sütunları Model sınıfındaki property'ler ile eşleştir.
        idSutun.setCellValueFactory(new PropertyValueFactory<>("id"));
        kullaniciAdiSutun.setCellValueFactory(new PropertyValueFactory<>("kullaniciAdi"));
        
        // 2. Veritabanından verileri yükle.
        kullanicilariYukle();

        // 3. Arama kutusu için filtreleme mantığını ayarla.
        FilteredList<Kullanici> filtrelenmisData = new FilteredList<>(kullaniciListesi, b -> true);

        aramaKutusu.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrelenmisData.setPredicate(kullanici -> {
                // Eğer arama kutusu boş ise tüm kullanıcıları göster.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String aramaMetni = newValue.toLowerCase();

                // Kullanıcı adında arama metni geçiyorsa göster.
                if (kullanici.getKullaniciAdi().toLowerCase().contains(aramaMetni)) {
                    return true;
                }
                
                // ID'ye göre arama yapmak için.
                if (String.valueOf(kullanici.getId()).contains(aramaMetni)) {
                    return true;
                }

                return false; // Eşleşme yoksa gösterme.
            });
        });

        // 4. Filtrelenmiş listeyi sıralanabilir bir listeye sar.
        SortedList<Kullanici> siralanmisData = new SortedList<>(filtrelenmisData);

        // 5. Sıralanmış listenin karşılaştırıcısını TableView'in karşılaştırıcısı ile bağla.
        siralanmisData.comparatorProperty().bind(kullaniciTableView.comparatorProperty());

        // 6. Son olarak, sıralanmış ve filtrelenmiş veriyi tabloya ata.
        kullaniciTableView.setItems(siralanmisData);
    }

    /**
     * Veritabanından tüm kullanıcıları çeker ve 'kullaniciListesi' listesine ekler.
     */
    private void kullanicilariYukle() {
        String sql = "SELECT id, kullanici_adi FROM kullanicilar";

        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // Her bir satır için bir Kullanici nesnesi oluştur ve listeye ekle.
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
