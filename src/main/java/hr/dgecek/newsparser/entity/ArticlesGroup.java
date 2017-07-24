package hr.dgecek.newsparser.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dgecek on 21.07.17..
 */

@Entity("articlesgroup")
public final class ArticlesGroup {

    @Id
    private ObjectId id;

    private Set<NewsArticle> articles = new HashSet<>();

    public Set<NewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(final Set<NewsArticle> articles) {
        this.articles = articles;
    }

    public void addToGroup(final NewsArticle secondArticle) {
        articles.add(secondArticle);
    }
}
