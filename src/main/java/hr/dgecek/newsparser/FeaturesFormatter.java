package hr.dgecek.newsparser;

import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.sentimentfilter.SentimentFilter;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;

import java.io.*;
import java.util.*;

import static hr.dgecek.newsparser.NewsAnnotator.NEGATIVE;
import static hr.dgecek.newsparser.NewsAnnotator.NEUTRAL;
import static hr.dgecek.newsparser.NewsAnnotator.POSITIVE;

/**
 * Created by dgecek on 17.12.16..
 */
public final class FeaturesFormatter {

    public static final String TRAIN_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.train";
    public static final String TEST_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.test";
    public static final float PERCENTAGE_FOR_TRAINING = 0.7f;

    private final List<String> legitSentiments;
    private final ArticleDAO datastore;
    private final SCStemmer stemmer;
    private final StopWordsRemover stopWordsRemover;
    private final Categorizer categorizer;
    private final NegationsManager negationsManager;
    private final SentimentFilter sentimentFilter;
    private final Statistics statistics = new Statistics();

    public FeaturesFormatter(final ArticleDAO datastore,
                             final SCStemmer stemmer,
                             final StopWordsRemover stopWordsRemover,
                             final Categorizer categorizer,
                             final NegationsManager negationsManager,
                             final SentimentFilter sentimentFilter) {
        this.datastore = datastore;
        this.stemmer = stemmer;
        this.stopWordsRemover = stopWordsRemover;
        this.categorizer = categorizer;
        this.negationsManager = negationsManager;
        this.sentimentFilter = sentimentFilter;

        legitSentiments = new ArrayList<>();
        legitSentiments.add(NEUTRAL);
        legitSentiments.add(NEGATIVE);
        legitSentiments.add(POSITIVE);
    }


    public void saveTrainingAndTestSetsToFile() throws IOException {
        final List<NewsArticle> news = datastore.getAll();
        Collections.shuffle(news);

        //test
        /*Collections.sort(news, (o1, o2) -> {
                    final boolean firstIsNews = "vijesti".equals(categorizer.getCategory(o1.getCategory()));
                    final boolean secondIsNews = "vijesti".equals(categorizer.getCategory(o2.getCategory()));

                    if (firstIsNews && secondIsNews) {
                        return 0;
                    } else if (firstIsNews) {
                        return 1;
                    } else {
                        return -1;
                    }

                }
        );*/

        final BufferedWriter trainBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(TRAIN_PATH))));
        final BufferedWriter testBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(TEST_PATH))));

        final ArrayList<String> stringsToWrite = new ArrayList<>(1500);

        for (final NewsArticle newsArticle : news) {
            //number of positive/negative sentiment words?
            if (legitSentiments.contains(newsArticle.getSentiment())) {  // && "vijesti".equals(categorizer.getCategory(newsArticle.getCategory()))) {
                final String stringToWrite = getStringToWrite(newsArticle);
                stringsToWrite.add(stringToWrite);
            }
        }

        for (int i = 0; i < stringsToWrite.size(); i++) {
            final String stringToWrite = stringsToWrite.get(i);
            if (((float) i / (float) stringsToWrite.size()) <= PERCENTAGE_FOR_TRAINING) {
                trainBufferedWriter.write(stringToWrite);
                trainBufferedWriter.newLine();
            } else {
                testBufferedWriter.write(stringToWrite);
                testBufferedWriter.newLine();
            }
        }

        trainBufferedWriter.close();
        testBufferedWriter.close();

        System.out.println(statistics.toString());
    }

    public Map<NewsArticle, String> getFeaturesLinesForNonAnottatedArticles(){
        //TODO
        return null;
    }

    private String getStringToWrite(final NewsArticle newsArticle) {
        final String title = (removeQuotes(format(newsArticle.getTitle())));
        final String body = filterSentiment(removeQuotes(format(newsArticle.getBody())));
        //final String category = categorizer.getCategory(newsArticle.getCategory());
        //number of words
        final String negations = negationsManager.getNegationsInArticle(title + " " + body);

        final float numOfNegations = (float) negations.split(",").length - 1;
        final float percentageOfNegations = numOfNegations / (float) (body.split(" ").length + title.split(" ").length);

        statistics.increaseCounter(newsArticle.getSentiment());

        final String stringToWrite = (newsArticle.getSentiment() + "\t" + title + "" +
                "\t" + body + "\t" +
                percentageOfNegations).toLowerCase();

        return stringToWrite;
    }

    private String filterSentiment(final String string) {
        final String newString = sentimentFilter.filter(string);
        return newString;
        //return string;
    }

    private String removeQuotes(final String string) {
        final String[] parts = string.split("\"|''|'|`");
        for (int i = 0; i < parts.length; i++) {
            //we only want to remove quotes

            if (i % 2 == 1 && parts[i].length() > 2) {
                parts[i] = "";
            }
        }

        final String newString = String.join("", (CharSequence[]) parts).replace("  ", " ");
        //System.out.println(newString);
        return newString;
    }

    private String format(final String string) {
        String newString = stopWordsRemover.removeStopWords(string);
        newString = stemmer.stemLine(newString);
        newString = formatInterpunction(newString);
        //newString = removeInterpunction(newString);


        //polozaj recenice u tekstu?
        //remove all names?
        //remove every sentence that has " or rekl, kaž,... dvotocka
        //what about words that start with pre, razdvoji?

        return newString;
    }

    private String removeInterpunction(final String string) {
        final String newString = string.replace("\t", "")
                .replace("\n", "")
                .replace(".", "")
                .replace("?", "")
                .replace("!", "")
                .replace(",", "")
                .replace(":", "")
                .replace(";", "")
                .replace("'", "")
                .replace("\"", "")
                .replace("  ", "");

        return newString;
    }

    private String formatInterpunction(final String string) {
        String newString = string.replace("\t", " ")
                .replace("\n", " ")
                .replace(".", " .")
                .replace("?", " ?")
                .replace("!", " !")
                .replace(",", " ,")
                .replace(":", " :")
                .replace(";", " ;")
                .replace("'", " ' ")
                .replace("\"", " \" ")
                .replace("  ", " ");

        return newString;
    }

    private static class Statistics {
        private int numOfNegatives = 0;
        private int numOfPositives = 0;
        private int numOfNeutrals = 0;

        void increaseCounter(final String sentiment) {
            switch (sentiment) {
                case NewsAnnotator.NEGATIVE:
                    numOfNegatives++;
                    break;
                case NewsAnnotator.POSITIVE:
                    numOfPositives++;
                    break;
                case NewsAnnotator.NEUTRAL:
                    numOfNeutrals++;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "numOfNegatives=" + numOfNegatives +
                    ", numOfPositives=" + numOfPositives +
                    ", numOfNeutrals=" + numOfNeutrals +
                    ", sum=" + (numOfNeutrals + numOfPositives + numOfNegatives) +
                    '}';
        }
    }
}
