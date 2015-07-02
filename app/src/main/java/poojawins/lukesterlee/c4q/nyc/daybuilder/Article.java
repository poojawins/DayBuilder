package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Luke on 7/1/2015.
 */
public class Article  implements Parcelable, Serializable {

    private String title;
    private String description;
    private String published_date;
    private String articleUrl;
    private String thumbnailUrl;

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel parcel) {
            return new Article(parcel);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public Article() {
        title = "";
        description = "";
        published_date = "";
        articleUrl = "";
        thumbnailUrl = "";
    }

    public Article(Parcel parcel) {
        title = parcel.readString();
        description = parcel.readString();
        published_date = parcel.readString();
        articleUrl = parcel.readString();
        thumbnailUrl = parcel.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(published_date);
        parcel.writeString(articleUrl);
        parcel.writeString(thumbnailUrl);
    }
}
