package hr.dgecek.newsparser.DB;

import hr.dgecek.newsparser.entity.NewsArticle;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by dgecek on 20.11.16..
 */
public interface ArticleRepository {

    void addArticle(NewsArticle article);

    List<NewsArticle> getAll();

    NewsArticle get(ObjectId id);

    List<NewsArticle> getNonPredictedArticles();

    String getStatistics();

    void update(NewsArticle article);

    List<NewsArticle> getRecentArticles();

    void clearStatistics();
}
