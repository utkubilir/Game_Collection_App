package Controller;

import Model.Kullanici;
import Util.VeritabaniBaglantisi;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    @FXML private TableColumn<Kullanici, String> kayitTarihiColumn;
    @FXML private TableColumn<Kullanici, Void> islemlerSutun;
    @FXML private TextField aramaKutusu;

    private final ObservableList<Kullanici> kullaniciListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idSutun.setCellValueFactory(new PropertyValueFactory<>("id"));
        kullaniciAdiSutun.setCellValueFactory(new PropertyValueFactory<>("kullaniciAdi"));
        kayitTarihiColumn.setCellValueFactory(new PropertyValueFactory<>("kayitTarihi"));
        
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
                // DÜZELTME: Buton metinleri kısaltıldı.
                private final Button logBtn = new Button("Loglar");
                private final Button oyunBtn = new Button("Oyunlar");
                private final Button silBtn = new Button("Sil");
                // DÜZELTME: Butonlar arası boşluk azaltıldı (5 -> 3).
                private final HBox pane = new HBox(3, logBtn, oyunBtn, silBtn);

                {
                    pane.setAlignment(Pos.CENTER);
                    // DÜZELTME: Yazı tipi küçültüldü.
                    String buttonStyle = "-fx-font-size: 11px; ";
                    logBtn.setStyle(buttonStyle + "-fx-background-color: #ffc107;");
                    oyunBtn.setStyle(buttonStyle + "-fx-background-color: #17a2b8; -fx-text-fill: white;");
                    silBtn.setStyle(buttonStyle + "-fx-background-color: #dc3545; -fx-text-fill: white;");

                    logBtn.setOnAction(e -> {
                        Kullanici kullanici = getTableView().getItems().get(getIndex());
                        kullaniciLoglariniGoster(kullanici);
                    });
                    
                    oyunBtn.setOnAction(e -> {
                        Kullanici kullanici = getTableView().getItems().get(getIndex());
                        kullanicininOyunlariniGoster(kullanici);
                    });
                    
                    silBtn.setOnAction(e -> {
                        Kullanici kullanici = getTableView().getItems().get(getIndex());
                        kullaniciyiSil(kullanici);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
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
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void kullaniciyiSil(Kullanici kullanici) {
        Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Silme Onayı", "'" + kullanici.getKullaniciAdi() + "' adlı kullanıcıyı ve tüm oyunlarını silmek istediğinizden emin misiniz?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM kullanicilar WHERE id = ?";
            try (Connection conn = VeritabaniBaglantisi.baglan();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, kullanici.getId());
                if(pstmt.executeUpdate() > 0){
                    kullaniciListesi.remove(kullanici);
                    showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kullanıcı başarıyla silindi.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void kullanicilariYukle() {
        kullaniciListesi.clear();
        String sql = "SELECT id, kullanici_adi, kayit_tarihi FROM kullanicilar WHERE is_admin = false";
        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("kayit_tarihi");
                String formattedDate = "N/A"; 
                if (timestamp != null) {
                    formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp);
                }
                
                kullaniciListesi.add(new Kullanici(rs.getInt("id"), rs.getString("kullanici_adi"), formattedDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
