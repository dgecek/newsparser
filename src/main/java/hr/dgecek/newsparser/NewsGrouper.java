package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.idf.IdfComputer;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;
import hr.dgecek.newsparser.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by davidgecek on 30/03/17.
 */
public final class NewsGrouper {

    private static final double K = 0.5;

    private final ArticleRepository articleRepository;
    private final StopWordsRemover stopWordsRemover;
    private final IdfComputer idfComputer;
    private final SCStemmer stemmer;

    public NewsGrouper(final ArticleRepository articleRepository,
                       final StopWordsRemover stopWordsRemover,
                       final IdfComputer idfComputer,
                       final SCStemmer stemmer) {
        this.articleRepository = articleRepository;
        this.stopWordsRemover = stopWordsRemover;
        this.idfComputer = idfComputer;
        this.stemmer = stemmer;
    }

    public void start() {
        final List<NewsArticle> recentArticles = articleRepository.getRecentArticles();

        writeTfIdfs(recentArticles);
        writeSimilarities(recentArticles);
    }

    private void writeSimilarities(final List<NewsArticle> recentArticles) {
        
    }

    private void writeTfIdfs(final List<NewsArticle> recentArticles) {
        for (final NewsArticle newsArticle : recentArticles) {
            if (newsArticle.getTfIdfs() == null && CategorizerImpl.NEWS_CATEGORY.equals(newsArticle.getCategory())) {
                int maximumFrequency = 0;
                final Map<String, Integer> termNumber = new HashMap<>();
                for (String term : stopWordsRemover.removeStopWords(TextUtils.removeInterpunction(newsArticle.getTitleAndText())).split(" ")) {
                    term = stemmer.stem(term.trim().toLowerCase());
                    if (term.length() > 0 && isAlphaNumeric(term)) {
                        final int frequency = termNumber.getOrDefault(term, 0);
                        final int newFrequency = frequency + 1;
                        termNumber.put(term, newFrequency);
                        if (newFrequency > maximumFrequency) {
                            maximumFrequency = newFrequency;
                        }
                    }
                }
                final Map<String, Double> tfidfs = new HashMap<>();
                int finalMaximumFrequency = maximumFrequency;
                termNumber.forEach(
                        (term, number) -> {
                            final double tf = K + (K * (termNumber.getOrDefault(term, 0) / finalMaximumFrequency));
                            final double idf = idfComputer.getIdfFor(term);
                            tfidfs.put(term, tf * idf);
                        }
                );
                newsArticle.setTfIdfs((HashMap<String, Double>) tfidfs);
                articleRepository.update(newsArticle);
            }
        }
    }

    private boolean isAlphaNumeric(final String term) {
        boolean hasNonAlpha = term.matches("^.*[^a-zA-Z0-9 ].*$");
        return !hasNonAlpha;
    }
}
