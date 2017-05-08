package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.DB.SimilarityRepository;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.entity.Similarity;
import org.bson.types.ObjectId;

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

    private final SimilarityRepository similarityRepository;
    private final ArticleRepository articleRepository;
    private final BufferedReader bufferedReader;
    private List<String> possibleAnswers;

    public SimilarityAnottator(final SimilarityRepository similarityRepository, ArticleRepository articleRepository) {
        this.similarityRepository = similarityRepository;
        this.articleRepository = articleRepository;

        initializePossibleAnswers();
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void initializePossibleAnswers() {
        possibleAnswers = new ArrayList<>();
        possibleAnswers.add("1");
        possibleAnswers.add("2");
    }

    public void printResults() {
        final List<Similarity> similarities = similarityRepository.getAnnotated();
        Collections.shuffle(similarities);

        final List<Similarity> firstPart = similarities.subList(0, similarities.size() / 2);
        final List<Similarity> secondPart = similarities.subList(similarities.size() / 2, similarities.size());

        double firstPartSimilarityFactor = getBestSimilarityFactor(firstPart);
        double secondPartF1 = getMicroF1(secondPart, firstPartSimilarityFactor, true);

        double secondPartSimilarityFactor = getBestSimilarityFactor(secondPart);
        double firstPartF1 = getMicroF1(firstPart, secondPartSimilarityFactor, true);

        System.out.println("firstPartSimilarityFactor: " + firstPartSimilarityFactor + ", secondPartF1: " + secondPartF1);
        System.out.println("secondPartSimilarityFactor: " + secondPartSimilarityFactor + ", firstPartF1: " + firstPartF1);
    }

    private double getBestSimilarityFactor(final List<Similarity> similarities) {
        double bestF1Score = 0d;
        double bestFactor = 0d;

        for (double similarityFactor = 0d; similarityFactor <= 1d; similarityFactor += 0.001d) {
            final double microF1 = getMicroF1(similarities, similarityFactor, false);
            System.out.println(similarityFactor + ": " + microF1);
            if (!Double.isNaN(microF1) && microF1 > bestF1Score) {
                bestF1Score = microF1;
                bestFactor = similarityFactor;
            }
        }

        return bestFactor;
    }

    private double getMicroF1(final List<Similarity> similarities, final double similarityFactor, boolean printFactors) {
        int truePositives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;
        int falseNegatives = 0;

        for (final Similarity similarity : similarities) {
            final boolean machineNotation = similarity.getSimilarity() > similarityFactor;
            final boolean handNotation = similarity.isHandNotation();

            if (machineNotation) {
                if (handNotation) {
                    truePositives++;
                } else {
                    falsePositives++;
                }
            } else {
                if (handNotation) {
                    falseNegatives++;
                } else {
                    trueNegatives++;
                }
            }
        }

        if (printFactors) {
            System.out.println("truePositives=" + truePositives);
            System.out.println("trueNegatives=" + trueNegatives);
            System.out.println("falsePositives=" + falsePositives);
            System.out.println("falseNegatives=" + falseNegatives);
        }

        double precision = (double) truePositives / (double) (truePositives + falsePositives);
        double recall = (double) truePositives / (double) (truePositives + falseNegatives);

        return (2 * precision * recall) / (precision + recall);

    }

    public void startUserAnnotation() {
        final List<Similarity> similarities = similarityRepository.getRecent();
        Collections.shuffle(similarities);

        System.out.println();
        System.out.println(ANNOTATION_STARTED_STRING);
        for (final Similarity similarity : similarities) {
            if (similarity.isHandNotation() == null) {
                final ObjectId firstArticleId = similarity.getFirstArticleId();
                final ObjectId secondArticleId = similarity.getSecondArticleId();

                final NewsArticle firstArticle = articleRepository.get(firstArticleId);
                final NewsArticle secondArticle = articleRepository.get(secondArticleId);

                System.out.println(firstArticle.getTitle());
                System.out.println(secondArticle.getTitle());
                System.out.println(similarity.getSimilarity());

                System.out.println(PLEASE_CHOOSE_STRING);

                String answer;
                Boolean notation = null;
                while (!possibleAnswers.contains(answer = readFromConsole())) {
                    System.out.println(BAD_CHOISE_STRING);
                }
                //update with answer
                switch (Integer.parseInt(answer)) {
                    case 1:
                        notation = true;
                        break;
                    case 2:
                        notation = false;
                        break;
                }
                similarity.setHandNotation(notation);
                similarityRepository.update(similarity);
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
