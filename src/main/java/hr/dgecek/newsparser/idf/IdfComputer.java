package hr.dgecek.newsparser.idf;

/**
 * Created by dgecek on 09.04.17..
 */
public interface IdfComputer {

    double getIdfFor(String term);

    void initialize();
}
