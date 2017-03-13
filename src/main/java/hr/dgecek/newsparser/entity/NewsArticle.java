package hr.dgecek.newsparser.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * Created by dgecek on 27.10.16..
 */

@Entity("newsarticles")
public class NewsArticle {
    @Id
    private ObjectId id;

    private String title;
    private String body;

    @Indexed(value= IndexDirection.ASC, name="url_unique", unique=true, dropDups=true)
    private String url;     //another id
    private String category;
    private String portal;
    private String sentiment;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSentiment() {
        return sentiment;
    }

    public boolean hasSentiment(){
        return sentiment != null && !sentiment.equals("");
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "\ntitle='" + title + '\'' +
                ", \nurl='" + url + '\'' +
                ", \ncategory='" + category + '\'' +
                ", \nportal='" + portal + '\'' +
                ", \nbody='" + body.replace(".", ".\n" +
                "") + '\'' +
                '}';
    }
}
