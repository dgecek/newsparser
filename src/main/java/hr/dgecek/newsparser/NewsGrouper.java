package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidgecek on 30/03/17.
 */
public final class NewsGrouper {

    private final ArticleRepository articleRepository;
    private List<NewsArticle> recentArticles = new ArrayList<>();

    public NewsGrouper(final ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void start() {
        this.recentArticles = articleRepository.getRecentArticles();

        for (final NewsArticle newsArticle : recentArticles) {
            if (newsArticle.getTermsTfIdfs() == null && CategorizerImpl.NEWS_CATEGORY.equals(newsArticle.getCategory())) {
                for (final String term : TextUtils.removeInterpunction(newsArticle.getTitleAndText()).split(" ")) {

                }
            }
        }
    }
}
