package hr.dgecek.newsparser;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.util.Pair;
import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.entity.NewsArticle;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class DataClassifier {

    private static final String PROP_FILE_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.prop";
    private static final String CROSS_VALIDATION_PROP = "/home/dgecek/projects/intellij/annotatedNews/cross_validation.prop";

    private static final String TRAINING_SET_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.train";
    private static final String TEST_SET_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.test";
    private static final String ENCODING = "utf-8";

    private final ArticleRepository datastore;

    public DataClassifier(final ArticleRepository datastore) {
        this.datastore = datastore;
    }

    public void classify(final Map<NewsArticle, String> articleLines) {
        final ColumnDataClassifier columnDataClassifier = new ColumnDataClassifier(PROP_FILE_PATH);
        final Classifier<String, String> classifier = columnDataClassifier.makeClassifier(columnDataClassifier.readTrainingExamples(TRAINING_SET_PATH));

        final Integer[] classifiedNews = {0};

        articleLines.forEach((article, line) -> {
            final Datum<String, String> datum = columnDataClassifier.makeDatumFromLine(line);
            final String sentiment = classifier.classOf(datum);

            System.out.println(article.getTitle() + "  ==>  " + sentiment);
            System.out.println(classifier.scoresOf(datum));

            article.setPredictedSentiment(sentiment);
            datastore.update(article);
            classifiedNews[0]++;
        });

        System.out.println("classified news: " + classifiedNews[0]);
    }

    //you should add crossValidationFolds=10, printCrossValidationDecisions=true and shuffleTrainingData=true in your prop file for your features.
    public void crossValidateSigma(){
        final ColumnDataClassifier columnDataClassifier = new ColumnDataClassifier(CROSS_VALIDATION_PROP);
        final Pair<GeneralDataset<String, String>, List<String[]>> crossValidationExamples = columnDataClassifier.readTestExamples(TRAINING_SET_PATH);
        //columnDataClassifier.crossValidate(crossValidationExamples.first(), crossValidationExamples.second());

        final LinearClassifierFactory<String, String> linearClassifierFactory = new LinearClassifierFactory<>();
        linearClassifierFactory.crossValidateSetSigma(crossValidationExamples.first(),5);
    }

    public void trainAndTest() {
        try {
            ColumnDataClassifier.main(new String[]{"-prop", PROP_FILE_PATH});
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
