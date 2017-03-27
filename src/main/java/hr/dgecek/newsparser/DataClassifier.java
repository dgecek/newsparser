package hr.dgecek.newsparser;

import edu.stanford.nlp.classify.ColumnDataClassifier;

import java.io.IOException;

public final class DataClassifier {
    public void clasify() {
        /*
        ColumnDataClassifier cdc = new ColumnDataClassifier("/home/dgecek/projects/intellij/annotatedNews/news.prop");
        Classifier<String, String> cl = cdc.makeClassifier(cdc.readTrainingExamples("/home/dgecek/projects/intellij/annotatedNews/news.train"));
        for (final String line : ObjectBank.getLineIterator("/home/dgecek/projects/intellij/annotatedNews/news.test", "utf-8")) {
            // instead of the method in the line below, if you have the individual elements
            // already you can use cdc.makeDatumFromStrings(String[])
            Datum<String, String> d = cdc.makeDatumFromLine(line);
            System.out.println(line + "  ==>  " + cl.classOf(d));
            System.out.println(cl.scoresOf(d));
        }
*/

        try {
            ColumnDataClassifier.main(new String[]{"-prop", "/home/dgecek/projects/intellij/annotatedNews/news.prop"});
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
