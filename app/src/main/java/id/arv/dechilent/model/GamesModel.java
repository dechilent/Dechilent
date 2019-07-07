package id.arv.dechilent.model;

public class GamesModel {
    public String games_id;
    public String image_url;
    public String name;
    public String category;

    public GamesModel() {
    }

    public GamesModel(String games_id, String image_url, String name, String category) {
        this.games_id = games_id;
        this.image_url = image_url;
        this.name = name;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGames_id() {
        return games_id;
    }

    public void setGames_id(String games_id) {
        this.games_id = games_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
