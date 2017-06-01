package hr.dgecek.newsparser.DB;

import hr.dgecek.newsparser.entity.Similarity;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by dgecek on 20.04.17..
 */
public interface SimilarityRepository {

    void addSimilarity(Similarity similarity);

    void update(Similarity similarity);

    List<Similarity> get(ObjectId firstArticleId);

    List<Similarity> getRecent();

    List<Similarity> getAnnotated();

    boolean similarityExistsForArticleId(final ObjectId firstArticleId);
}
