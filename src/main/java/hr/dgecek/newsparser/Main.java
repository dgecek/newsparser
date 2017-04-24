package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.*;
import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.date.DateProvider;
import hr.dgecek.newsparser.date.DateProviderImpl;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.idf.IdfComputer;
import hr.dgecek.newsparser.idf.IdfComputerImpl;
import hr.dgecek.newsparser.sentimentfilter.SentimentFilter;
import hr.dgecek.newsparser.sentimentfilter.SentimentFilterImpl;
import hr.dgecek.newsparser.stemmer.LjubesicPandzicStemmer;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemoverImpl;
import org.mongodb.morphia.Datastore;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dgecek on 27.10.16..
 */
public final class Main {

    private static final long TIME_BETWEEN_DOWNLOADING = 20 * 60 * 1000;

    public static void main(final String[] args) throws IOException, InterruptedException {
        final DateProvider dateProvider = new DateProviderImpl();
        final Datastore datastore = MorphiaManager.getDataStore();
        final ArticleRepository articleRepository = new ArticleRepositoryImpl(datastore, dateProvider);
        final SimilarityRepository similarityRepository = new SimilarityRepositoryImpl(datastore);

        final SCStemmer stemmer = new LjubesicPandzicStemmer();
        final StopWordsRemover stopWordsRemover = new StopWordsRemoverImpl();
        final Categorizer categorizer = new CategorizerImpl();
        final NewsDownloader downloader = new NewsDownloader(articleRepository, dateProvider);
        final NewsAnnotator annotator = new NewsAnnotator(articleRepository, categorizer);
        final NegationsManager negationsManager = new NegationsManager();
        final SentimentFilter sentimentFilter = new SentimentFilterImpl(stemmer);
        final FeaturesFormatter featuresFormatter = new FeaturesFormatter(articleRepository, stemmer, stopWordsRemover, categorizer, negationsManager, sentimentFilter);
        final DataClassifier dataClassifier = new DataClassifier(articleRepository);
        final IdfComputer idfComputer = new IdfComputerImpl(articleRepository, stopWordsRemover, stemmer);
        final NewsGrouper newsGrouper = new NewsGrouper(articleRepository, similarityRepository, stopWordsRemover, idfComputer, stemmer);

        //featuresFormatter.saveTrainingAndTestSetsToFile();

        //downloader.downloadNews();
        //annotator.startUserAnnotation();
        //dataClassifier.crossValidateSigma();
        //dataClassifier.trainAndTest();

        //newsGrouper.start();

        while (true) {
            downloader.downloadNews();

            final Map<NewsArticle, String> articleLines = featuresFormatter.getFeaturesLinesForNonAnottatedArticles();
            dataClassifier.classify(articleLines);

            newsGrouper.start();

            Thread.sleep(TIME_BETWEEN_DOWNLOADING);
        }
    }
}
