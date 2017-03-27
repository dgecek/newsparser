package hr.dgecek.newsparser;

import hr.dgecek.newsparser.entity.NewsArticle;

import java.util.List;

/**
 * Created by dgecek on 20.11.16..
 */
public interface ArticleDAO {

    void addArticle(NewsArticle article);

    List<NewsArticle> getAll();

    String getStatistics();

    void update(NewsArticle article);
}
