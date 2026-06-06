package Controller;

import Dao.OyunDao;
import Dao.VeriErisimHatasi;
import Model.Oyun;
import Util.LogYoneticisi;
import Util.Mesaj;
import Util.Navigasyon;
import Util.Pencere;
import Util.UserSession;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AnaEkranController implements Initializable {

    @FXML private TableView<Oyun> oyunlarTableView;
    @FXML private TableColumn<Oyun, String> baslikColumn, platformColumn, turColumn, statusColumn;
    @FXML private TableColumn<Oyun, Integer> puanColumn;
    @FXML private TextField aramaKutusu;
    @FXML private ComboBox<String> durumFiltreCombo, turFiltreCombo;

    @FXML private VBox detayPaneli;
    @FXML private ImageView kapakImage;
    @FXML private Label titleLabel, statusLabel, genreLabel, developerLabel, publisherLabel, platformsLabel, translatorsLabel, steamidLabel, releaseYearLabel, playtimeLabel, formatLabel, languageLabel, ratingLabel, tagsLabel;

    @FXML private Label toplamOyunLabel, ortalamaPuanLabel, enCokOynananTurLabel;

    private static final String TUM_DURUMLAR = "Tüm Durumlar";
    private static final String TUM_TURLER = "Tüm Türler";
    private static final List<String> DURUMLAR = List.of(
            "Kütüphanede", "Oynanıyor", "Bitti", "Platinlendi", "Bırakıldı", "Sonra Oynanacak");

    private final OyunDao oyunDao = new OyunDao();
    private final ObservableList<Oyun> oyunListesi = FXCollections.observableArrayList();
    private FilteredList<Oyun> filtrelenmisData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        baslikColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platforms"));
        turColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        puanColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        setupRatingCellFactory();
        setupContextMenu();
        setupShortcuts();

        oyunlarTableView.setPlaceholder(new Label("Henüz oyun yok. 'Yeni Oyun Ekle' ile başlayın."));

        oyunlarTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showGameDetails(newValue)
        );

        durumFiltreCombo.setItems(FXCollections.observableArrayList());
        durumFiltreCombo.getItems().add(TUM_DURUMLAR);
        durumFiltreCombo.getItems().addAll(DURUMLAR);
        durumFiltreCombo.setValue(TUM_DURUMLAR);

        oyunlariYukle();
        filtreyiAyarla();
        // Default ordering: by title, ascending.
        oyunlarTableView.getSortOrder().add(baslikColumn);
        showGameDetails(null);
    }

    private void setupRatingCellFactory() {
        puanColumn.setCellFactory(column -> new TableCell<Oyun, Integer>() {
            @Override
            protected void updateItem(Integer rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty || rating == null || rating <= 0) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int puan = Math.max(0, Math.min(10, rating));
                    setText("★".repeat(puan) + "☆".repeat(10 - puan));
                    setStyle("-fx-text-fill: #f5c518; -fx-alignment: CENTER;");
                }
            }
        });
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem duzenleItem = new MenuItem("Düzenle");
        MenuItem silItem = new MenuItem("Sil");

        duzenleItem.setOnAction(this::handleOyunDuzenle);
        silItem.setOnAction(this::handleOyunSil);

        contextMenu.getItems().addAll(duzenleItem, silItem);

        oyunlarTableView.setRowFactory(tv -> {
            TableRow<Oyun> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            // Double-click a row to edit it.
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openOyunForm(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupShortcuts() {
        // Delete key removes the selected game.
        oyunlarTableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                handleOyunSil(null);
            }
        });
    }

    private void updateStatistics() {
        int toplamOyun = oyunListesi.size();
        double toplamPuan = 0;
        int puanliOyunSayisi = 0;
        Map<String, Integer> genreCounts = new HashMap<>();

        for (Oyun oyun : oyunListesi) {
            if (oyun.getRating() > 0) {
                toplamPuan += oyun.getRating();
                puanliOyunSayisi++;
            }
            if (oyun.getGenre() != null && !oyun.getGenre().isEmpty()) {
                genreCounts.put(oyun.getGenre(), genreCounts.getOrDefault(oyun.getGenre(), 0) + 1);
            }
        }

        double ortalamaPuan = (puanliOyunSayisi == 0) ? 0 : toplamPuan / puanliOyunSayisi;
        String favoriTur = genreCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        toplamOyunLabel.setText("Toplam Oyun: " + toplamOyun);
        ortalamaPuanLabel.setText(String.format("Ortalama Puan: %.1f", ortalamaPuan));
        enCokOynananTurLabel.setText("Favori Tür: " + favoriTur);
    }

    private void showGameDetails(Oyun oyun) {
        if (oyun != null) {
            detayPaneli.setVisible(true);
            detayPaneli.setManaged(true);
            titleLabel.setText(oyun.getTitle());
            statusLabel.setText(oyun.getStatus() == null ? "-" : oyun.getStatus());
            genreLabel.setText(oyun.getGenre());
            developerLabel.setText(oyun.getDeveloper());
            publisherLabel.setText(oyun.getPublisher());
            platformsLabel.setText(oyun.getPlatforms());
            translatorsLabel.setText(oyun.getTranslators());
            steamidLabel.setText(oyun.getSteamid());
            releaseYearLabel.setText(oyun.getReleaseYear() == 0 ? "-" : String.valueOf(oyun.getReleaseYear()));
            playtimeLabel.setText(oyun.getPlaytime());
            formatLabel.setText(oyun.getFormat());
            languageLabel.setText(oyun.getLanguage());
            ratingLabel.setText(oyun.getRating() == 0 ? "-" : String.valueOf(oyun.getRating()));
            tagsLabel.setText(oyun.getTags());
            kapagiYukle(oyun.getSteamid());
        } else {
            detayPaneli.setVisible(false);
            detayPaneli.setManaged(false);
        }
    }

    /** Loads a Steam header image (cover art) when a numeric Steam app id is available. */
    private void kapagiYukle(String steamid) {
        if (steamid != null && steamid.trim().matches("\\d+")) {
            String url = "https://cdn.cloudflare.steamstatic.com/steam/apps/" + steamid.trim() + "/header.jpg";
            kapakImage.setImage(new Image(url, true)); // background loading; failures stay blank
            kapakImage.setVisible(true);
            kapakImage.setManaged(true);
        } else {
            kapakImage.setImage(null);
            kapakImage.setVisible(false);
            kapakImage.setManaged(false);
        }
    }

    @FXML void handleOyunEkle(ActionEvent event) {
        openOyunForm(null);
    }

    @FXML void handleOyunDuzenle(ActionEvent event) {
        Oyun seciliOyun = oyunlarTableView.getSelectionModel().getSelectedItem();
        if (seciliOyun == null) {
            Mesaj.uyari("Uyarı", "Lütfen düzenlemek için bir oyun seçin.");
            return;
        }
        openOyunForm(seciliOyun);
    }

    @FXML void handleOyunSil(ActionEvent event) {
        Oyun seciliOyun = oyunlarTableView.getSelectionModel().getSelectedItem();
        if (seciliOyun == null) {
            Mesaj.uyari("Uyarı", "Lütfen silmek için bir oyun seçin.");
            return;
        }
        if (!Mesaj.onay("Silme Onayı", "'" + seciliOyun.getTitle() + "' adlı oyunu silmek istediğinizden emin misiniz?")) {
            return;
        }
        try {
            oyunDao.sil(seciliOyun.getId(), UserSession.getInstance().getUserId());
            oyunListesi.remove(seciliOyun);
            turFiltresiniDoldur();
            updateStatistics();
            LogYoneticisi.logla(UserSession.getInstance().getUserId(),
                    "'" + seciliOyun.getTitle() + "' adlı oyunu sildi.");
            Mesaj.bilgi("Başarılı", "Oyun başarıyla silindi.");
        } catch (VeriErisimHatasi e) {
            Mesaj.hata("Veritabanı Hatası", "Oyun silinirken bir hata oluştu.");
        }
    }

    private void openOyunForm(Oyun oyun) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/OyunFormu.fxml"));
            Parent root = loader.load();
            OyunFormuController controller = loader.getController();
            controller.setOnFormClosed(() -> {
                oyunlariYukle();
                updateStatistics();
            });
            if (oyun != null) {
                controller.setDuzenlenecekOyun(oyun);
            }
            Stage stage = new Stage();
            stage.setTitle(oyun == null ? "Yeni Oyun Ekle" : "Oyunu Düzenle");
            Pencere.ikonla(stage);
            Scene scene = new Scene(root);
            Util.Tema.uygula(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Mesaj.hata("Arayüz Hatası", "Oyun formu yüklenemedi.");
        }
    }

    private void oyunlariYukle() {
        try {
            oyunListesi.setAll(oyunDao.kullaniciOyunlari(UserSession.getInstance().getUserId()));
        } catch (VeriErisimHatasi e) {
            oyunListesi.clear();
            Mesaj.hata("Veritabanı Hatası", "Oyunlar yüklenemedi. Veritabanına bağlanılamıyor olabilir.");
        }
        turFiltresiniDoldur();
        updateStatistics();
    }

    @FXML void handleImportJson(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("JSON Dosyasını İçe Aktar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Dosyaları", "*.json"));
        File file = fileChooser.showOpenDialog(oyunlarTableView.getScene().getWindow());
        if (file == null) {
            return;
        }
        List<Oyun> iceAktarilanOyunlar;
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type oyunListesiTipi = new TypeToken<List<Oyun>>(){}.getType();
            iceAktarilanOyunlar = gson.fromJson(reader, oyunListesiTipi);
        } catch (Exception e) {
            e.printStackTrace();
            Mesaj.hata("Hata", "JSON dosyası okunurken veya işlenirken bir hata oluştu.");
            return;
        }
        if (iceAktarilanOyunlar == null) {
            return;
        }
        try {
            int sayi = oyunDao.iceAktar(iceAktarilanOyunlar, UserSession.getInstance().getUserId());
            oyunlariYukle();
            LogYoneticisi.logla(UserSession.getInstance().getUserId(), sayi + " oyunu JSON'dan içe aktardı.");
            Mesaj.bilgi("Başarılı", sayi + " oyun başarıyla içe aktarıldı.");
        } catch (VeriErisimHatasi e) {
            Mesaj.hata("Hata", "İçe aktarma sırasında bir hata oluştu, hiçbir oyun eklenmedi.");
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
                LogYoneticisi.logla(UserSession.getInstance().getUserId(),
                        "Oyun listesini JSON'a dışa aktardı (" + oyunListesi.size() + " oyun).");
                Mesaj.bilgi("Başarılı", "Oyun listeniz başarıyla dışa aktarıldı.");
            } catch (IOException e) {
                e.printStackTrace();
                Mesaj.hata("Hata", "Dosya yazılırken bir hata oluştu.");
            }
        }
    }

    // ----- Filtering -------------------------------------------------------

    private void filtreyiAyarla() {
        filtrelenmisData = new FilteredList<>(oyunListesi, oyun -> true);
        aramaKutusu.textProperty().addListener((observable, oldValue, newValue) -> filtreyiUygula());
        durumFiltreCombo.valueProperty().addListener((observable, oldValue, newValue) -> filtreyiUygula());
        turFiltreCombo.valueProperty().addListener((observable, oldValue, newValue) -> filtreyiUygula());

        SortedList<Oyun> siralanmisData = new SortedList<>(filtrelenmisData);
        siralanmisData.comparatorProperty().bind(oyunlarTableView.comparatorProperty());
        oyunlarTableView.setItems(siralanmisData);
    }

    private void filtreyiUygula() {
        String arama = aramaKutusu.getText() == null ? "" : aramaKutusu.getText().toLowerCase().trim();
        String durum = durumFiltreCombo.getValue();
        String tur = turFiltreCombo.getValue();

        filtrelenmisData.setPredicate(oyun -> {
            if (!arama.isEmpty()) {
                boolean eslesme = oyun.getTitle().toLowerCase().contains(arama)
                        || (oyun.getGenre() != null && oyun.getGenre().toLowerCase().contains(arama))
                        || (oyun.getPlatforms() != null && oyun.getPlatforms().toLowerCase().contains(arama))
                        || (oyun.getTags() != null && oyun.getTags().toLowerCase().contains(arama));
                if (!eslesme) {
                    return false;
                }
            }
            if (durum != null && !TUM_DURUMLAR.equals(durum) && !durum.equals(oyun.getStatus())) {
                return false;
            }
            if (tur != null && !TUM_TURLER.equals(tur) && !tur.equals(oyun.getGenre())) {
                return false;
            }
            return true;
        });
    }

    /** Rebuilds the genre filter from the distinct genres currently loaded. */
    private void turFiltresiniDoldur() {
        String onceki = turFiltreCombo.getValue();
        TreeSet<String> turler = new TreeSet<>();
        for (Oyun oyun : oyunListesi) {
            if (oyun.getGenre() != null && !oyun.getGenre().isBlank()) {
                turler.add(oyun.getGenre());
            }
        }
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add(TUM_TURLER);
        items.addAll(turler);
        turFiltreCombo.setItems(items);
        turFiltreCombo.setValue(items.contains(onceki) ? onceki : TUM_TURLER);
    }

    @FXML void handleFiltreTemizle(ActionEvent event) {
        aramaKutusu.clear();
        durumFiltreCombo.setValue(TUM_DURUMLAR);
        turFiltreCombo.setValue(TUM_TURLER);
    }

    // ----- Account / tools -------------------------------------------------

    @FXML void handleSifreDegistir(ActionEvent event) {
        openModal("/Fxml/SifreDegistir.fxml", "Şifre Değiştir", null);
    }

    @FXML void handleIstatistikler(ActionEvent event) {
        openModal("/Fxml/Istatistik.fxml", "İstatistikler",
                loader -> ((IstatistikController) loader.getController()).initData(oyunListesi));
    }

    @FXML void handleHakkinda(ActionEvent event) {
        Mesaj.bilgi("Hakkında", Util.Uygulama.hakkindaMetni());
    }

    @FXML void handleCikis(ActionEvent event) {
        if (!Mesaj.onay("Çıkış", "Oturumu kapatmak istediğinizden emin misiniz?")) {
            return;
        }
        LogYoneticisi.logla(UserSession.getInstance().getUserId(), "Sistemden çıkış yaptı.");
        UserSession.cleanUserSession();
        Navigasyon.girisEkraniniAc();
        ((Stage) oyunlarTableView.getScene().getWindow()).close();
    }

    private interface LoaderInit {
        void init(FXMLLoader loader) throws Exception;
    }

    private void openModal(String fxmlPath, String title, LoaderInit init) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if (init != null) {
                init.init(loader);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            Pencere.ikonla(stage);
            Scene scene = new Scene(root);
            Util.Tema.uygula(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Mesaj.hata("Arayüz Hatası", "Ekran yüklenemedi: " + fxmlPath);
        }
    }
}
