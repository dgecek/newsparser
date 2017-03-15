package hr.dgecek.newsparser;

import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.sentimentfilter.SentimentFilter;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static hr.dgecek.newsparser.NewsAnnotator.NEGATIVE;
import static hr.dgecek.newsparser.NewsAnnotator.NEUTRAL;
import static hr.dgecek.newsparser.NewsAnnotator.POSITIVE;

/**
 * Created by dgecek on 17.12.16..
 */
public final class FeaturesFormatter {

    private static final String TRAIN_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.train";
    private static final String TEST_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.test";

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
        legitSentiments.add(NewsAnnotator.NEUTRAL);
        legitSentiments.add(NEGATIVE);
        legitSentiments.add(NewsAnnotator.POSITIVE);
    }


    public void saveToFile() throws IOException {
        final List<NewsArticle> news = datastore.getAll();
        //Collections.shuffle(news);

        final BufferedWriter trainBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(TRAIN_PATH))));
        final BufferedWriter testBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(TEST_PATH))));

        final ArrayList<String> stringsToWrite = new ArrayList<>(1000);

        for (final NewsArticle newsArticle : news) {
            //number of positive/negative sentiment words?
            if (legitSentiments.contains(newsArticle.getSentiment())) {//&& "vijesti".equals(categorizer.getCategory(newsArticle.getCategory()))) { //&& "vijesti".equals(categorizer.getCategory(newsArticle.getCategory())
                final String title = (removeQuotes(format(newsArticle.getTitle())));
                final String body = filterSentiment(removeQuotes(format(newsArticle.getBody())));
                //final String category = categorizer.getCategory(newsArticle.getCategory());
                //number of words
                final String negations = negationsManager.getNegationsInArticle(title + " " + body);
                //final int testnum = newsArticle.getSentiment().equals(NEGATIVE) ? 0 : newsArticle.getSentiment().equals(POSITIVE) ? 1 : 2;
                final float percentageOfNegations = ((float) negations.split(",").length - 1) / (float) (body.split(" ").length + title.split(" ").length);

                statistics.increaseCounter(newsArticle.getSentiment());
                final String stringToWrite = (newsArticle.getSentiment() + "\t" + title + "" +
                        "\t" + body + "\t" +
                        percentageOfNegations).toLowerCase();
                stringsToWrite.add(stringToWrite);
            }
        }

        for (int i = 0; i < stringsToWrite.size(); i++) {
            final String stringToWrite = stringsToWrite.get(i);
            if (((float) i / (float) stringsToWrite.size()) <= 0.7f) {
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

    private String filterSentiment(final String string) {
        final String newString = sentimentFilter.filter(string);
        return newString;
    }

    private String removeQuotes(final String string) {
        String[] parts = string.split("\"|''|'|`");
        for (int i = 0; i < parts.length; i++) {
            //we only want to remove quotes

            if (i % 2 == 1 && parts[i].length() > 2) {
                parts[i] = "";
            }
        }

        final String newString = String.join("", (CharSequence[]) parts).replace("  ", " ");
        System.out.println(newString);
        return newString;
    }

    private String format(final String string) {
        String newString = stopWordsRemover.removeStopWords(string);
        newString = stemmer.stemLine(newString);

        newString = newString.replace("\t", " ")
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

        //remove every sentence that has " or rekl, kaž,...
        //what about words that start with pre, razdvoji?

        return newString;
    }

    public void proba() {
        String probni = "Francuska predsjednička kandidatkinja i liderica stranke krajnje desnice Marine Le Pen pozvala je na okončanje besplatnog obrazovanja za djecu stranaca, javlja BBC. Le Pen je u svom govoru u Parizu rekla kako zapravo nema ništa protiv stranaca. \"Ali ja im kažem, ako ste došli u našu zemlju, nemojte očekivati da ćete biti zbrinuti i da će vaša djeca biti educirana bez naknade,\" rekla je Le Pen koja je liderica Nacionalne fronte.";
        System.out.println(probni);
        removeQuotes(format(probni));
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
                    '}';
        }
    }
}
