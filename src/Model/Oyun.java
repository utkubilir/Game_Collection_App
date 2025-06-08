package Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Oyun {

    private final IntegerProperty id;
    private final IntegerProperty kullaniciId;
    private final StringProperty title;
    private final StringProperty genre;
    private final StringProperty developer;
    private final StringProperty publisher;
    private final StringProperty platforms;
    private final StringProperty translators;
    private final StringProperty steamid;
    private final IntegerProperty releaseYear;
    private final StringProperty playtime;
    private final StringProperty format;
    private final StringProperty language;
    private final IntegerProperty rating;
    private final StringProperty tags;

    public Oyun() {
        this.id = new SimpleIntegerProperty();
        this.kullaniciId = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.genre = new SimpleStringProperty();
        this.developer = new SimpleStringProperty();
        this.publisher = new SimpleStringProperty();
        this.platforms = new SimpleStringProperty();
        this.translators = new SimpleStringProperty();
        this.steamid = new SimpleStringProperty();
        this.releaseYear = new SimpleIntegerProperty();
        this.playtime = new SimpleStringProperty();
        this.format = new SimpleStringProperty();
        this.language = new SimpleStringProperty();
        this.rating = new SimpleIntegerProperty();
        this.tags = new SimpleStringProperty();
    }


    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public int getKullaniciId() { return kullaniciId.get(); }
    public void setKullaniciId(int kullaniciId) { this.kullaniciId.set(kullaniciId); }
    public IntegerProperty kullaniciIdProperty() { return kullaniciId; }

    public String getGenre() { return genre.get(); }
    public void setGenre(String genre) { this.genre.set(genre); }
    public StringProperty genreProperty() { return genre; }

    public String getDeveloper() { return developer.get(); }
    public void setDeveloper(String developer) { this.developer.set(developer); }
    public StringProperty developerProperty() { return developer; }

    public String getPublisher() { return publisher.get(); }
    public void setPublisher(String publisher) { this.publisher.set(publisher); }
    public StringProperty publisherProperty() { return publisher; }

    public String getPlatforms() { return platforms.get(); }
    public void setPlatforms(String platforms) { this.platforms.set(platforms); }
    public StringProperty platformsProperty() { return platforms; }

    public String getTranslators() { return translators.get(); }
    public void setTranslators(String translators) { this.translators.set(translators); }
    public StringProperty translatorsProperty() { return translators; }

    public String getSteamid() { return steamid.get(); }
    public void setSteamid(String steamid) { this.steamid.set(steamid); }
    public StringProperty steamidProperty() { return steamid; }

    public int getReleaseYear() { return releaseYear.get(); }
    public void setReleaseYear(int releaseYear) { this.releaseYear.set(releaseYear); }
    public IntegerProperty releaseYearProperty() { return releaseYear; }

    public String getPlaytime() { return playtime.get(); }
    public void setPlaytime(String playtime) { this.playtime.set(playtime); }
    public StringProperty playtimeProperty() { return playtime; }

    public String getFormat() { return format.get(); }
    public void setFormat(String format) { this.format.set(format); }
    public StringProperty formatProperty() { return format; }

    public String getLanguage() { return language.get(); }
    public void setLanguage(String language) { this.language.set(language); }
    public StringProperty languageProperty() { return language; }

    public int getRating() { return rating.get(); }
    public void setRating(int rating) { this.rating.set(rating); }
    public IntegerProperty ratingProperty() { return rating; }

    public String getTags() { return tags.get(); }
    public void setTags(String tags) { this.tags.set(tags); }
    public StringProperty tagsProperty() { return tags; }
}
