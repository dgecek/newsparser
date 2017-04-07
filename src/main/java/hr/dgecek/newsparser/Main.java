package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.DB.MorphiaManager;
import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.date.DateProvider;
import hr.dgecek.newsparser.date.DateProviderImpl;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.sentimentfilter.SentimentFilter;
import hr.dgecek.newsparser.sentimentfilter.SentimentFilterImpl;
import hr.dgecek.newsparser.stemmer.LjubesicPandzicStemmer;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemoverImpl;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dgecek on 27.10.16..
 */
public final class Main {

    private static final long TIME_BETWEEN_DOWNLOADING = 10 * 60 * 1000;

    public static void main(final String[] args) throws IOException, InterruptedException {
        final DateProvider dateProvider = new DateProviderImpl();
        final ArticleRepository articleRepository = MorphiaManager.getDataStore(dateProvider);
        final SCStemmer stemmer = new LjubesicPandzicStemmer();
        final StopWordsRemover stopWordsRemover = new StopWordsRemoverImpl();
        final Categorizer categorizer = new CategorizerImpl();
        final NewsDownloader downloader = new NewsDownloader(articleRepository, dateProvider);
        final NewsAnnotator annotator = new NewsAnnotator(articleRepository, categorizer);
        final NegationsManager negationsManager = new NegationsManager();
        final SentimentFilter sentimentFilter = new SentimentFilterImpl(stemmer);
        final FeaturesFormatter featuresFormatter = new FeaturesFormatter(articleRepository, stemmer, stopWordsRemover, categorizer, negationsManager, sentimentFilter);
        final DataClassifier dataClassifier = new DataClassifier(articleRepository);
        final NewsGrouper newsGrouper = new NewsGrouper(articleRepository);

        //downloader.downloadNews();
        //annotator.startUserAnnotation();

        //featuresFormatter.saveTrainingAndTestSetsToFile();
        //dataClassifier.crossValidateSigma();
        //dataClassifier.trainAndTest();

        //dataClassifier.classify();
        //dataClassifier.trainAndTest();
        //dataClassifier.crossValidateSigma();

        //final Map<NewsArticle, String> articleLines = featuresFormatter.getFeaturesLinesForNonAnottatedArticles();
        //dataClassifier.classify(articleLines);

        //newsGrouper.start();
    }
}
