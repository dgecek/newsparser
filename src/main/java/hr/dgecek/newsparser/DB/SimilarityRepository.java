package hr.dgecek.newsparser.DB;

import hr.dgecek.newsparser.entity.Similarity;
import org.bson.types.ObjectId;

/**
 * Created by dgecek on 20.04.17..
 */
public interface SimilarityRepository {

    void addSimilarity(Similarity similarity);

    void get(ObjectId firstArticleId);
}
