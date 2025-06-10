package Controller;

import Model.Kullanici;
import Util.VeritabaniBaglantisi;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class KullaniciLoglariController implements Initializable {

    @FXML private Label logBaslikLabel;
    @FXML private ListView<String> logListView;
    
    private Kullanici secilenKullanici;
    private ObservableList<String> logListesi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logListView.setItems(logListesi);
    }
    
    public void initData(Kullanici kullanici) {
        this.secilenKullanici = kullanici;
        logBaslikLabel.setText(kullanici.getKullaniciAdi() + " Adlı Kullanıcının Aktivite Logları");
        loglariYukle();
    }
    
    private void loglariYukle() {
        logListesi.clear();
        String sql = "SELECT log_mesaji, log_tarihi FROM kullanici_loglari WHERE kullanici_id = ? ORDER BY log_tarihi DESC";
        
        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, secilenKullanici.getId());
            ResultSet rs = pstmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            
            while(rs.next()) {
                String tarih = dateFormat.format(rs.getTimestamp("log_tarihi"));
                String mesaj = rs.getString("log_mesaji");
                logListesi.add(tarih + " - " + mesaj);
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı logları yüklenirken bir hata oluştu.");
            e.printStackTrace();
        }
    }
}
