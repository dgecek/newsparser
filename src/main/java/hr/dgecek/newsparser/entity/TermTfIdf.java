package hr.dgecek.newsparser.entity;

/**
 * Created by davidgecek on 31/03/17.
 */
public final class TermTfIdf {

    private String term;
    private double tfIdf;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(double tfIdf) {
        this.tfIdf = tfIdf;
    }
}
