package hr.dgecek.newsparser.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * Created by dgecek on 20.04.17..
 */

@Entity("similarities")
public final class Similarity {

    @Id
    private ObjectId similarityId;

    @Indexed(value = IndexDirection.ASC, name = "similarity_first_article", unique = false)
    private ObjectId firstArticleId;
    private ObjectId secondArticleId;
    private double similarity;

    public ObjectId getFirstArticleId() {
        return firstArticleId;
    }

    public void setFirstArticleId(ObjectId firstArticleId) {
        this.firstArticleId = firstArticleId;
    }

    public ObjectId getSecondArticleId() {
        return secondArticleId;
    }

    public void setSecondArticleId(ObjectId secondArticleId) {
        this.secondArticleId = secondArticleId;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public ObjectId getSimilarityId() {
        return similarityId;
    }

    public void setSimilarityId(ObjectId similarityId) {
        this.similarityId = similarityId;
    }
}
