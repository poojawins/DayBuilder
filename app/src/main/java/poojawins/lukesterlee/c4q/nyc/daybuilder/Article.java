package poojawins.lukesterlee.c4q.nyc.daybuilder;

/**
 * Created by Luke on 7/1/2015.
 */
public class Article {

    private String title;
    private String description;
    private String published_date;
    private String articleUrl;
    private String thumbnailUrl;

    public Article(String title, String description, String published_date, String articleUrl, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.published_date = published_date;
        this.articleUrl = articleUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublished_date() {
        return published_date;
    }

    public void setPublished_date(String published_date) {
        this.published_date = published_date;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
