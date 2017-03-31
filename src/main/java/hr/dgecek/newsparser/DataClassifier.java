package hr.dgecek.newsparser;

import edu.stanford.nlp.classify.*;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.util.Pair;
import hr.dgecek.newsparser.DB.ArticleRepository;

import java.io.IOException;
import java.util.List;

public final class DataClassifier {

    private static final String PROP_FILE_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.prop";
    private static final String CROSS_VALIDATION_PROP = "/home/dgecek/projects/intellij/annotatedNews/cross_validation.prop";

    private static final String TRAINING_SET_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.train";
    private static final String TEST_SET_PATH = "/home/dgecek/projects/intellij/annotatedNews/news.test";
    private static final String ENCODING = "utf-8";

    public DataClassifier(final ArticleRepository datastore) {

    }

    public void classify() {
        final ColumnDataClassifier columnDataClassifier = new ColumnDataClassifier(PROP_FILE_PATH);
        final Classifier<String, String> classifier = columnDataClassifier.makeClassifier(columnDataClassifier.readTrainingExamples(TRAINING_SET_PATH));
        for (final String line : ObjectBank.getLineIterator(TEST_SET_PATH, ENCODING)) {
            // instead of the method in the line below, if you have the individual elements
            // already you can use columnDataClassifier.makeDatumFromStrings(String[])
            final Datum<String, String> datum = columnDataClassifier.makeDatumFromLine(line);
            System.out.println(line + "  ==>  " + classifier.classOf(datum));
            System.out.println(classifier.scoresOf(datum));
        }
    }

    //you should add crossValidationFolds=10, printCrossValidationDecisions=true and shuffleTrainingData=true in your prop file for your features.
    public void crossValidate(){
        final ColumnDataClassifier columnDataClassifier = new ColumnDataClassifier(CROSS_VALIDATION_PROP);
        final Pair<GeneralDataset<String, String>, List<String[]>> crossValidationExamples = columnDataClassifier.readTestExamples(TRAINING_SET_PATH);
        columnDataClassifier.crossValidate(crossValidationExamples.first(), crossValidationExamples.second());


        final LinearClassifierFactory<String, String> linearClassifierFactory = new LinearClassifierFactory<>();
        linearClassifierFactory.crossValidateSetSigma(crossValidationExamples.first(),10);

        //final CrossValidator<String, String> crossValidator = new CrossValidator<>(crossValidationExamples.first());

    }

    public void trainAndTest() {
        try {
            ColumnDataClassifier.main(new String[]{"-prop", PROP_FILE_PATH});
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
