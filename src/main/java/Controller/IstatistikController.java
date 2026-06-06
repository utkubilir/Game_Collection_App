package Controller;

import Model.Oyun;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

/**
 * Read-only statistics dashboard rendered from the user's in-memory game list
 * (no extra database round-trip).
 */
public class IstatistikController {

    @FXML private Label toplamLabel;
    @FXML private Label puanliLabel;
    @FXML private Label ortalamaLabel;
    @FXML private Label favoriLabel;
    @FXML private PieChart durumChart;
    @FXML private BarChart<String, Number> turChart;

    private static final int EN_FAZLA_TUR = 8;

    public void initData(List<Oyun> oyunlar) {
        int toplam = oyunlar.size();
        double toplamPuan = 0;
        int puanli = 0;
        Map<String, Integer> durumlar = new LinkedHashMap<>();
        Map<String, Integer> turler = new LinkedHashMap<>();

        for (Oyun o : oyunlar) {
            if (o.getRating() > 0) {
                toplamPuan += o.getRating();
                puanli++;
            }
            String durum = (o.getStatus() == null || o.getStatus().isBlank()) ? "Belirsiz" : o.getStatus();
            durumlar.merge(durum, 1, Integer::sum);
            if (o.getGenre() != null && !o.getGenre().isBlank()) {
                turler.merge(o.getGenre(), 1, Integer::sum);
            }
        }

        double ortalama = (puanli == 0) ? 0 : toplamPuan / puanli;
        String favori = turler.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        toplamLabel.setText("Toplam Oyun: " + toplam);
        puanliLabel.setText("Puanlanan: " + puanli);
        ortalamaLabel.setText(String.format("Ortalama Puan: %.1f", ortalama));
        favoriLabel.setText("Favori Tür: " + favori);

        durumlar.forEach((ad, adet) ->
                durumChart.getData().add(new PieChart.Data(ad + " (" + adet + ")", adet)));

        XYChart.Series<String, Number> seri = new XYChart.Series<>();
        turler.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(EN_FAZLA_TUR)
                .forEach(e -> seri.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));
        turChart.getData().add(seri);
        turChart.setLegendVisible(false);
    }
}
