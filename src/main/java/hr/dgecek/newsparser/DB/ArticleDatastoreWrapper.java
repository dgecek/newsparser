package hr.dgecek.newsparser.DB;

import com.mongodb.DuplicateKeyException;
import hr.dgecek.newsparser.ArticleDAO;
import hr.dgecek.newsparser.entity.NewsArticle;
import org.mongodb.morphia.Datastore;

import java.util.List;

/**
 * Created by dgecek on 16.11.16..
 */
public final class ArticleDatastoreWrapper implements ArticleDAO {
    public static final String SAVING_STRING = "saving...";
    public static final String NOT_AN_ARTICLE_STRING = "Not an article. Not saving.";
    public static final String DUPLICATE_STRING = "Duplicate. Not saving.";
    public static final String DONE_STRING = "Done";
    private final Datastore dataStore;
    private final DataStoreStatistics statistics = new DataStoreStatistics();

    public ArticleDatastoreWrapper(final Datastore datastore) {
        this.dataStore = datastore;
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
    public void update(final NewsArticle article) {
        dataStore.save(article);
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
    }
}
