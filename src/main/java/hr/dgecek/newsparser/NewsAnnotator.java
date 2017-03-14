package hr.dgecek.newsparser;

import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.entity.NewsArticle;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dgecek on 20.11.16..
 */
public final class NewsAnnotator {
    public static final String ANNOTATION_STARTED_STRING = "ANNOTATION STARTED!";
    public static final String PLEASE_CHOOSE_STRING = "Please choose:\n (1) positive\n (2) negative\n (3) neutral\n (4) pass";
    public static final String BAD_CHOISE_STRING = "Please choose correctly.";
    public static final String POSITIVE = "positive";
    public static final String NEGATIVE = "negative";
    public static final String NEUTRAL = "neutral";
    public static final String PASS = "pass";
    private final ArticleDAO datastore;
    private final BufferedReader bufferedReader;
    private final Categorizer categorizer;
    private List<String> possibleAnswers;

    public NewsAnnotator(ArticleDAO datastore, Categorizer categorizer) {
        this.datastore = datastore;
        this.categorizer = categorizer;

        initializePossibleAnswers();
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void initializePossibleAnswers() {
        possibleAnswers = new ArrayList<>();
        possibleAnswers.add("1");
        possibleAnswers.add("2");
        possibleAnswers.add("3");
        possibleAnswers.add("4");
    }

    public void startUserAnnotation() {
        List<NewsArticle> articles = datastore.getAll();
        Collections.shuffle(articles);

        System.out.println();
        System.out.println(ANNOTATION_STARTED_STRING);
        for (NewsArticle article : articles) {
            if (!article.hasSentiment() && ("vijesti".equals(categorizer.getCategory(article.getCategory())))) {
                System.out.println(article.toString());
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
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }
}
