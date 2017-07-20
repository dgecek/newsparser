package hr.dgecek.newsparser.stopwordremover;

/**
 * Created by dgecek on 23.12.16..
 */
public interface StopWordsRemover {

    String removeStopWordsLeaveNegations(String string);

    String removeStopWordsAndNegations(String string);
}
