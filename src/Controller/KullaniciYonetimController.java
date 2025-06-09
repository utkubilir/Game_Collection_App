package Controller;

import Model.Kullanici;
import Util.VeritabaniBaglantisi;
import java.io.IOException;
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
    @FXML private TableColumn<Kullanici, Void> islemlerSutun;
    @FXML private TextField aramaKutusu;

    private final ObservableList<Kullanici> kullaniciListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idSutun.setCellValueFactory(new PropertyValueFactory<>("id"));
        kullaniciAdiSutun.setCellValueFactory(new PropertyValueFactory<>("kullaniciAdi"));
        
        kullanicilariYukle();
        islemlerSutununuAyarla();
        filtreyiAyarla();
    }

    // EKSİK OLAN VE YENİ EKLENEN METOT
    @FXML
    void yenileButonAction(ActionEvent event) {
        kullanicilariYukle();
    }
    
    private void islemlerSutununuAyarla() {
        Callback<TableColumn<Kullanici, Void>, TableCell<Kullanici, Void>> cellFactory = param -> {
            final TableCell<Kullanici, Void> cell = new TableCell<>() {
                private final Button goruntuleBtn = new Button("Oyunları Gör");
                private final Button silBtn = new Button("Sil");
                private final HBox pane = new HBox(10, goruntuleBtn, silBtn);
                {
                    pane.setAlignment(Pos.CENTER);
                    goruntuleBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
                    silBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                    goruntuleBtn.setOnAction(e -> {
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
        Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Silme Onayı", 
                "'" + kullanici.getKullaniciAdi() + "' adlı kullanıcıyı ve tüm oyunlarını silmek istediğinizden emin misiniz?");
        
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
        String sql = "SELECT id, kullanici_adi FROM kullanicilar WHERE is_admin = false"; // Adminleri göstermiyoruz
        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                kullaniciListesi.add(new Kullanici(rs.getInt("id"), rs.getString("kullanici_adi")));
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
