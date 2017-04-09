package hr.dgecek.newsparser.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by dgecek on 27.10.16..
 */

@Entity("newsarticles")
public final class NewsArticle {

    @Id
    private ObjectId id;

    @Indexed(value = IndexDirection.ASC, name = "url_unique", unique = true, dropDups = true)
    private String url;     //another id

    private String title;
    private String body;
    private String category;
    private String portal;
    private String sentiment;
    private String predictedSentiment;
    private Date date;
    private String urlToImage;
    private HashMap<String, Double> tfIdfs;

    public HashMap<String, Double> getTfIdfs() {
        return tfIdfs;
    }

    public void setTfIdfs(HashMap<String, Double> tfIdfs) {
        this.tfIdfs = tfIdfs;
    }

    public void putTermTfIdf(final String term, final Double tfIdf) {
        this.tfIdfs.put(term, tfIdf);
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPredictedSentiment() {
        return predictedSentiment;
    }

    public void setPredictedSentiment(String predictedSentiment) {
        this.predictedSentiment = predictedSentiment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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

    public String getTitleAndText(){
        return getTitle() + " " + getBody();
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

    public boolean hasSentiment() {
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
                "\nurlToImage=" + urlToImage + "\n" +
                ", \ncategory='" + category + '\'' +
                ", \nportal='" + portal + '\'' +
                ", \nsentiment='" + sentiment + '\'' +
                ", \npredictedSentiment='" + predictedSentiment + '\'' +
                ", \ndate=" + date +
                ", \nbody='" + body.replace(".", ".\n") + '\'' +
                '}';
    }
}
