package hr.dgecek.newsparser.DB;

import hr.dgecek.newsparser.date.DateProvider;
import hr.dgecek.newsparser.entity.Similarity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import java.util.List;

/**
 * Created by dgecek on 20.04.17..
 */
public class SimilarityRepositoryImpl implements SimilarityRepository {

    private final Datastore datastore;
    private final DateProvider dateProvider;

    public SimilarityRepositoryImpl(final Datastore datastore,
                                    final DateProvider dateProvider) {
        this.datastore = datastore;
        this.dateProvider = dateProvider;
    }

    @Override
    public void addSimilarity(final Similarity similarity) {
        datastore.save(similarity);
    }

    @Override
    public List<Similarity> get(final ObjectId firstArticleId) {
        return datastore.find(Similarity.class)
                .field("firstArticleId").equal(firstArticleId)
                .asList();
    }

    @Override
    public List<Similarity> getRecent(){
        return datastore.find(Similarity.class)
                .field("date").greaterThan(dateProvider.getDateNumberOfDaysAgo(3))
                .asList();
    }

    @Override
    public boolean similarityExistsForArticleId(final ObjectId firstArticleId) {
        return !get(firstArticleId).isEmpty();
    }
}
