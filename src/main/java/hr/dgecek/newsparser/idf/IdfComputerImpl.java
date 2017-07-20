package hr.dgecek.newsparser.idf;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;
import hr.dgecek.newsparser.utils.TextUtils;

import java.util.*;

/**
 * Created by dgecek on 09.04.17..
 */
public final class IdfComputerImpl implements IdfComputer {

    private final SCStemmer stemmer;
    private final ArticleRepository articleRepository;

    private int numberOfDocuments;
    private Map<String, Integer> numberOfDocumentsWithTerms;

    public IdfComputerImpl(final ArticleRepository articleRepository, final StopWordsRemover stopWordsRemover, final SCStemmer stemmer) {
        this.stemmer = stemmer;
        this.articleRepository = articleRepository;
        initialize();
    }

    public void initialize() {
        numberOfDocumentsWithTerms = new HashMap<>();
        final List<NewsArticle> articles = articleRepository.getAll();
        numberOfDocuments = 0;

        for (final NewsArticle article : articles) {
            if (CategorizerImpl.NEWS_CATEGORY.equals(article.getCategory())) {
                numberOfDocuments++;
                final Set<String> termsInArticle = new HashSet<>();
                for (final String term : (TextUtils.removeInterpunction(article.getTitleAndText())).split(" ")) {
                    if (term.trim().length() > 0 && TextUtils.isTermAlphaWord(term)) {
                        termsInArticle.add(stemmer.stem(term).trim().toLowerCase());
                    }
                }

                for (final String term : termsInArticle) {
                    final int termNumber = numberOfDocumentsWithTerms.getOrDefault(term, 0);
                    numberOfDocumentsWithTerms.put(term, termNumber + 1);
                }
            }
        }
    }

    @Override
    public double getIdfFor(final String term) {
        return Math.log(numberOfDocuments / (1 + numberOfDocumentsWithTerms.getOrDefault(stemmer.stem(term).toLowerCase(), 0)));
    }
}
