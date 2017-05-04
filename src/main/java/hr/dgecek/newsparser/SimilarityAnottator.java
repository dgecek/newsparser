package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.DB.SimilarityRepository;
import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.entity.Similarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dgecek on 04.05.17..
 */
public class SimilarityAnottator {


    private static final String ANNOTATION_STARTED_STRING = "ANNOTATION STARTED!";
    private static final String PLEASE_CHOOSE_STRING = "Please choose:\n (1) connected\n (2) not connected";
    private static final String BAD_CHOISE_STRING = "Please choose correctly.";

    private final SimilarityRepository datastore;
    private final BufferedReader bufferedReader;
    private List<String> possibleAnswers;

    public SimilarityAnottator(final SimilarityRepository datastore) {
        this.datastore = datastore;

        initializePossibleAnswers();
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void initializePossibleAnswers() {
        possibleAnswers = new ArrayList<>();
        possibleAnswers.add("1");
        possibleAnswers.add("2");
    }

    public void startUserAnnotation() {
        final List<Similarity> similarities = datastore.getRecent();
        Collections.shuffle(similarities);

        System.out.println();
        System.out.println(ANNOTATION_STARTED_STRING);
        for (final Similarity similarity : similarities) {
            if (similarity.isHandNotation() == null) {
                System.out.println(simi.toString());
                System.out.println(PLEASE_CHOOSE_STRING);

                String answer;
                String sentiment = "";
                while (!possibleAnswers.contains(answer = readFromConsole())) {
                    System.out.println(BAD_CHOISE_STRING);
                }
                //update with answer
                switch (Integer.parseInt(answer)) {
                    case 1:
                        sentiment = POSITIVE;
                        break;
                    case 2:
                        sentiment = NEGATIVE;
                        break;
                    case 3:
                        sentiment = NEUTRAL;
                        break;
                    case 4:
                        sentiment = PASS;
                        break;
                }
                article.setSentiment(sentiment);
                datastore.update(article);
            }
        }
    }

    private String readFromConsole() {
        try {
            return bufferedReader.readLine();
        } catch (final IOException e) {
            e.printStackTrace();
            return "";
        }

    }


}
