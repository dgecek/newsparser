package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.newsstatistics.TopicStatistics;
import hr.dgecek.newsparser.newsstatistics.TopicsStatisticsRepository;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.utils.TextUtils;

import java.util.*;

/**
 * Created by dgecek on 27.05.17..
 */
public final class NewsAnalyzer {

    private final ArticleRepository articleRepository;
    private final List<String> topics = new ArrayList<>();
    private final SCStemmer stemmer;

    private TopicsStatisticsRepository topicsStatisticsRepository;

    public NewsAnalyzer(final ArticleRepository articleRepository,
                        final SCStemmer scStemmer,
                        final TopicsStatisticsRepository topicsStatisticsRepository) {
        this.articleRepository = articleRepository;
        this.stemmer = scStemmer;
        this.topicsStatisticsRepository = topicsStatisticsRepository;
        fillSubjects();
    }

    private void fillSubjects() {
        topics.add(stemmer.stem("kitarović"));

        topics.add(stemmer.stem("hdz"));
        topics.add(stemmer.stem("plenković"));
        topics.add(stemmer.stem("hasanbegović"));
        topics.add(stemmer.stem("brkić"));

        topics.add(stemmer.stem("most"));
        topics.add(stemmer.stem("grmoja"));
        topics.add(stemmer.stem("petrov"));
        topics.add(stemmer.stem("bulj"));

        topics.add(stemmer.stem("sdp"));
        topics.add(stemmer.stem("bernardić"));
        topics.add(stemmer.stem("ostojić"));
        topics.add(stemmer.stem("opačić"));

        topics.add(stemmer.stem("živi zid"));
        topics.add(stemmer.stem("sinčić"));
        topics.add(stemmer.stem("pernar"));
        topics.add(stemmer.stem("bunjac"));

        topics.add(stemmer.stem("todorić"));
        topics.add(stemmer.stem("agrokor"));
        topics.add(stemmer.stem("ledo"));

        topics.add(stemmer.stem("trump"));
        topics.add(stemmer.stem("merkel"));
        topics.add(stemmer.stem("vučić"));
        topics.add(stemmer.stem("putin"));
    }

    public void start() {
        final List<NewsArticle> articles = articleRepository.getAll();

        for (final NewsArticle article : articles) {
            final HashSet<String> subjects = article.getSubjects();
            final String title = article.getTitle();
            final String portal = article.getPortal();
            final String sentiment = article.getPredictedSentiment();

            if (subjects != null && title != null) {
                // if it is in the title it is probably the subject
                subjects.addAll(Arrays.asList(stemmer.stem(TextUtils.removeInterpunction(title).toLowerCase()).split(" ")));
                for (final String subject : subjects) {
                    if (topics.contains(subject)) {
                        topicsStatisticsRepository.put(subject, portal, sentiment);
                    }
                }
            }
        }

        printStatistics();
    }

    private void printStatistics() {
        final List<TopicStatistics> topicStatisticsList = topicsStatisticsRepository.getAll();

        for(final TopicStatistics topicStatistics : topicStatisticsList){
            System.out.println();
            System.out.println(topicStatistics.getPortal());
            System.out.println(topicStatistics.getSubject());
            System.out.println(topicStatistics.getStatistics().getPositive());
            System.out.println(topicStatistics.getStatistics().getNegative());
            System.out.println(topicStatistics.getStatistics().getNeutral());
        }
    }
}
