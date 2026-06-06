package Controller;

import Dao.KullaniciDao;
import Dao.VeriErisimHatasi;
import Model.Kullanici;
import Util.LogYoneticisi;
import Util.Mesaj;
import Util.Pencere;
import Util.UserSession;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class KullaniciYonetimController implements Initializable {
    @FXML private TableView<Kullanici> kullaniciTableView;
    @FXML private TableColumn<Kullanici, Integer> idSutun;
    @FXML private TableColumn<Kullanici, String> kullaniciAdiSutun;
    @FXML private TableColumn<Kullanici, String> rolSutun;
    @FXML private TableColumn<Kullanici, String> kayitTarihiColumn;
    @FXML private TableColumn<Kullanici, Void> islemlerSutun;
    @FXML private TextField aramaKutusu;

    private final KullaniciDao kullaniciDao = new KullaniciDao();
    private final ObservableList<Kullanici> kullaniciListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idSutun.setCellValueFactory(new PropertyValueFactory<>("id"));
        kullaniciAdiSutun.setCellValueFactory(new PropertyValueFactory<>("kullaniciAdi"));
        rolSutun.setCellValueFactory(new PropertyValueFactory<>("rol"));
        kayitTarihiColumn.setCellValueFactory(new PropertyValueFactory<>("kayitTarihi"));

        kullaniciTableView.setPlaceholder(new Label("Kayıtlı kullanıcı bulunmuyor."));

        kullanicilariYukle();
        islemlerSutununuAyarla();
        filtreyiAyarla();
    }

    @FXML
    void yenileButonAction(ActionEvent event) {
        kullanicilariYukle();
    }

    private void islemlerSutununuAyarla() {
        Callback<TableColumn<Kullanici, Void>, TableCell<Kullanici, Void>> cellFactory = param -> {
            final TableCell<Kullanici, Void> cell = new TableCell<>() {
                private final Button logBtn = new Button("Loglar");
                private final Button oyunBtn = new Button("Oyunlar");
                private final Button rolBtn = new Button("Admin Yap");
                private final Button silBtn = new Button("Sil");
                private final HBox pane = new HBox(3, logBtn, oyunBtn, rolBtn, silBtn);

                {
                    pane.setAlignment(Pos.CENTER);
                    String buttonStyle = "-fx-font-size: 11px; ";
                    logBtn.setStyle(buttonStyle + "-fx-background-color: #ffc107;");
                    oyunBtn.setStyle(buttonStyle + "-fx-background-color: #17a2b8; -fx-text-fill: white;");
                    rolBtn.setStyle(buttonStyle + "-fx-background-color: #6f42c1; -fx-text-fill: white;");
                    silBtn.setStyle(buttonStyle + "-fx-background-color: #dc3545; -fx-text-fill: white;");

                    logBtn.setOnAction(e -> kullaniciLoglariniGoster(getKullanici()));
                    oyunBtn.setOnAction(e -> kullanicininOyunlariniGoster(getKullanici()));
                    rolBtn.setOnAction(e -> roluDegistir(getKullanici()));
                    silBtn.setOnAction(e -> kullaniciyiSil(getKullanici()));
                }

                private Kullanici getKullanici() {
                    return getTableView().getItems().get(getIndex());
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        setGraphic(null);
                        return;
                    }
                    Kullanici kullanici = getKullanici();
                    boolean kendisi = kullanici.getId() == UserSession.getInstance().getUserId();
                    rolBtn.setText(kullanici.isAdmin() ? "Adminlikten Çıkar" : "Admin Yap");
                    // A user must not lock themselves out by demoting/deleting their own account.
                    rolBtn.setDisable(kendisi);
                    silBtn.setDisable(kendisi);
                    setGraphic(pane);
                }
            };
            return cell;
        };
        islemlerSutun.setCellFactory(cellFactory);
    }

    private void kullaniciLoglariniGoster(Kullanici kullanici) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/KullaniciLoglari.fxml"));
            Parent root = loader.load();

            KullaniciLoglariController controller = loader.getController();
            controller.initData(kullanici);

            Stage stage = new Stage();
            stage.setTitle(kullanici.getKullaniciAdi() + " Logları");
            Pencere.ikonla(stage);
            Scene scene = new Scene(root);
            Util.Tema.uygula(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Mesaj.hata("Arayüz Hatası", "Log ekranı yüklenemedi.");
        }
    }

    private void kullanicininOyunlariniGoster(Kullanici kullanici) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/TumOyunlar.fxml"));
            Parent root = loader.load();
            TumOyunlarController controller = loader.getController();
            controller.initData(kullanici);
            Stage stage = new Stage();
            stage.setTitle(kullanici.getKullaniciAdi() + " Adlı Kullanıcının Oyunları");
            Pencere.ikonla(stage);
            Scene scene = new Scene(root);
            Util.Tema.uygula(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Mesaj.hata("Arayüz Hatası", "Oyun ekranı yüklenemedi.");
        }
    }

    private void roluDegistir(Kullanici kullanici) {
        boolean yeniRol = !kullanici.isAdmin();
        String islem = yeniRol ? "admin yapmak" : "adminlikten çıkarmak";
        if (!Mesaj.onay("Rol Değişikliği",
                "'" + kullanici.getKullaniciAdi() + "' adlı kullanıcıyı " + islem + " istediğinizden emin misiniz?")) {
            return;
        }
        try {
            kullaniciDao.rolGuncelle(kullanici.getId(), yeniRol);
            kullanici.setAdmin(yeniRol);
            kullaniciTableView.refresh();
            LogYoneticisi.logla(UserSession.getInstance().getUserId(),
                    "'" + kullanici.getKullaniciAdi() + "' kullanıcısının rolünü '" + kullanici.getRol() + "' yaptı.");
        } catch (VeriErisimHatasi e) {
            Mesaj.hata("Veritabanı Hatası", "Rol güncellenirken bir hata oluştu.");
        }
    }

    private void kullaniciyiSil(Kullanici kullanici) {
        if (!Mesaj.onay("Silme Onayı",
                "'" + kullanici.getKullaniciAdi() + "' adlı kullanıcıyı ve tüm oyunlarını silmek istediğinizden emin misiniz?")) {
            return;
        }
        try {
            kullaniciDao.sil(kullanici.getId());
            kullaniciListesi.remove(kullanici);
            LogYoneticisi.logla(UserSession.getInstance().getUserId(),
                    "'" + kullanici.getKullaniciAdi() + "' adlı kullanıcıyı sildi.");
            Mesaj.bilgi("Başarılı", "Kullanıcı başarıyla silindi.");
        } catch (VeriErisimHatasi e) {
            Mesaj.hata("Veritabanı Hatası", "Kullanıcı silinirken bir hata oluştu.");
        }
    }

    private void kullanicilariYukle() {
        try {
            kullaniciListesi.setAll(kullaniciDao.hepsiniGetir());
        } catch (VeriErisimHatasi e) {
            kullaniciListesi.clear();
            Mesaj.hata("Veritabanı Hatası", "Kullanıcılar yüklenemedi.");
        }
    }

    private void filtreyiAyarla() {
        FilteredList<Kullanici> filtrelenmisData = new FilteredList<>(kullaniciListesi, b -> true);
        aramaKutusu.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrelenmisData.setPredicate(kullanici -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return kullanici.getKullaniciAdi().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        SortedList<Kullanici> siralanmisData = new SortedList<>(filtrelenmisData);
        siralanmisData.comparatorProperty().bind(kullaniciTableView.comparatorProperty());
        kullaniciTableView.setItems(siralanmisData);
    }
}
