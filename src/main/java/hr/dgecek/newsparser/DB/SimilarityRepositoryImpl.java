package hr.dgecek.newsparser.DB;

import hr.dgecek.newsparser.entity.Similarity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import java.util.List;

/**
 * Created by dgecek on 20.04.17..
 */
public class SimilarityRepositoryImpl implements SimilarityRepository {

    private final Datastore datastore;

    public SimilarityRepositoryImpl(final Datastore datastore) {
        this.datastore = datastore;
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
    public boolean similarityExistsForArticleId(final ObjectId firstArticleId) {
        return !get(firstArticleId).isEmpty();
    }
}
