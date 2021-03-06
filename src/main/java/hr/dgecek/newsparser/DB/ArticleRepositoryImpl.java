package hr.dgecek.newsparser.DB;

import com.mongodb.DuplicateKeyException;
import hr.dgecek.newsparser.date.DateProvider;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.entity.Similarity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Criteria;

import java.util.List;

/**
 * Created by dgecek on 16.11.16..
 */
public final class ArticleRepositoryImpl implements ArticleRepository {

    public static final String SAVING_STRING = "saving...";
    public static final String NOT_AN_ARTICLE_STRING = "Not an article. Not saving.";
    public static final String DUPLICATE_STRING = "Duplicate. Not saving.";
    public static final String DONE_STRING = "Done";
    public static final int RECENT_DAYS = 3;

    private final Datastore dataStore;
    private final DataStoreStatistics statistics = new DataStoreStatistics();
    private final DateProvider dateProvider;

    public ArticleRepositoryImpl(final Datastore datastore,
                                 final DateProvider dateProvider) {
        this.dataStore = datastore;
        this.dateProvider = dateProvider;
    }

    @Override
    public void addArticle(final NewsArticle article) {
        System.out.println(article.toString());
        System.out.println(SAVING_STRING);
        try {
            if (articleLegit(article)) {
                dataStore.save(article);
                statistics.saved++;
            } else {
                System.out.println(NOT_AN_ARTICLE_STRING);
                statistics.notAnArticles++;
            }
        } catch (final DuplicateKeyException e) {
            System.out.println(DUPLICATE_STRING);
            statistics.duplicates++;
        }
        System.out.println(DONE_STRING);
    }

    private boolean articleLegit(final NewsArticle article) {
        return !article.getBody().trim().equals("") && article.getCategory() != null && article.getTitle().length() > 3 && article.getCategory().length() > 2;
    }

    @Override
    public List<NewsArticle> getAll() {
        return dataStore.find(NewsArticle.class).asList();
    }

    @Override
    public NewsArticle get(final ObjectId id) {
        return dataStore.find(NewsArticle.class)
                .field("_id").equal(id)
                .get();
    }

    @Override
    public List<NewsArticle> getNonPredictedArticles() {
        return dataStore.find(NewsArticle.class)
                .field("predictedSentiment").doesNotExist()
                .asList();
    }

    @Override
    public void update(final NewsArticle article) {
        dataStore.save(article);
    }

    @Override
    public List<NewsArticle> getRecentArticles() {
        return dataStore.find(NewsArticle.class)
                .field("date").greaterThan(dateProvider.getDateNumberOfDaysAgo(RECENT_DAYS))
                .asList();
    }

    @Override
    public void clearStatistics() {
        statistics.clear();
    }

    @Override
    public String getStatistics() {
        return statistics.toString();
    }

    private class DataStoreStatistics {
        private int saved = 0;
        private int duplicates = 0;
        private int notAnArticles = 0;

        @Override
        public String toString() {
            return "DataStoreStatistics{" +
                    "saved=" + saved +
                    ", duplicates=" + duplicates +
                    ", notAnArticles=" + notAnArticles +
                    '}';
        }

        public void clear() {
            saved = 0;
            duplicates = 0;
            notAnArticles = 0;
        }
    }
}
