package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.*;
import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.date.DateProvider;
import hr.dgecek.newsparser.date.DateProviderImpl;
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

/**
 * Created by dgecek on 20.07.17..
 */
public final class App {

    // 20 minutes
    private static final long TIME_BETWEEN_DOWNLOADING = 20 * 60 * 1000;

    private NewsDownloader downloader = null;
    private FeaturesFormatter featuresFormatter = null;
    private DataClassifier dataClassifier = null;
    private IdfComputer idfComputer = null;
    private NewsGrouper newsGrouper = null;

    public void start() throws IOException, InterruptedException {
        //featuresFormatter.saveTrainingAndTestSetsToFile();
        //downloader.downloadNews();
        //annotator.startUserAnnotation();
        //dataClassifier.crossValidateSigma();
        //dataClassifier.trainAndTest();
        //newsGrouper.start();
        //similarityAnottator.printResults();

        while (true) {
            // do it every time bc it is more economical for server RAM to do it every time and lose some time, than
            // do it just once and hold everything in memory
            initializeEverything();

            downloader.downloadNews();
            dataClassifier.classify(featuresFormatter.getFeaturesLinesForNonAnottatedArticles());
            newsGrouper.start();
            //analyzer.start();
            idfComputer.initialize();
            Thread.sleep(TIME_BETWEEN_DOWNLOADING);

            clearMemory();
        }
    }

    private void clearMemory() {
        downloader = null;
        featuresFormatter = null;
        dataClassifier = null;
        idfComputer = null;
        newsGrouper = null;
    }

    private void initializeEverything() {
        final DateProvider dateProvider = new DateProviderImpl();
        final Datastore datastore = MorphiaManager.getDataStore();
        final ArticleRepository articleRepository = new ArticleRepositoryImpl(datastore, dateProvider);
        final SimilarityRepository similarityRepository = new SimilarityRepositoryImpl(datastore, dateProvider);

        final NegationsManager negationsManager = new NegationsManager();
        final SCStemmer stemmer = new LjubesicPandzicStemmer();
        final StopWordsRemover stopWordsRemover = new StopWordsRemoverImpl(negationsManager);
        final Categorizer categorizer = new CategorizerImpl();
        final GroupedArticlesRepository groupedArticlesRepository = new GroupedArticlesRepositoryImpl(datastore);

        downloader = new NewsDownloader(articleRepository, dateProvider, categorizer);
        //final NewsAnnotator annotator = new NewsAnnotator(articleRepository, categorizer);
        final SentimentFilter sentimentFilter = new SentimentFilterImpl(stemmer);
        featuresFormatter = new FeaturesFormatter(articleRepository, stemmer, stopWordsRemover, categorizer, negationsManager, sentimentFilter);
        dataClassifier = new DataClassifier(articleRepository);
        idfComputer = new IdfComputerImpl(articleRepository, stopWordsRemover, stemmer);
        newsGrouper = new NewsGrouper(articleRepository, similarityRepository, groupedArticlesRepository, stopWordsRemover, idfComputer, stemmer);
        //final SimilarityAnottator similarityAnottator = new SimilarityAnottator(similarityRepository, articleRepository);
        //final TopicsStatisticsRepository topicsStatisticsRepository = new TopicsStatisticsRepositoryMemoryImpl();
        //final NewsAnalyzer analyzer = new NewsAnalyzer(articleRepository, stemmer, topicsStatisticsRepository);
    }
}
