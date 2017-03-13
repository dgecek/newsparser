package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.MorphiaManager;
import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.stemmer.LjubesicPandzicStemmer;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemoverImpl;

import java.io.IOException;

/**
 * Created by dgecek on 27.10.16..
 */
public final class Main {

    private static final long TIME_BETWEEN_DOWNLOADING = 10 * 60 * 1000;

    public static void main(final String[] args) throws IOException, InterruptedException {
        final ArticleDAO datastore = MorphiaManager.getDataStore();
        final SCStemmer stemmer = new LjubesicPandzicStemmer();
        final StopWordsRemover stopWordsRemover = new StopWordsRemoverImpl();
        final Categorizer categorizer = new CategorizerImpl();
        final NewsDownloader downloader = new NewsDownloader(datastore);
        final NewsAnnotator annotator = new NewsAnnotator(datastore, categorizer);
        final NegationsManager negationsManager = new NegationsManager();
        final FeaturesFormatter featuresFormatter = new FeaturesFormatter(datastore, stemmer, stopWordsRemover, categorizer, negationsManager);

        downloader.downloadNews();
        annotator.startUserAnnotation();
        featuresFormatter.saveToFile();
    }
}
