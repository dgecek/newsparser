package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.newsstatistics.TopicStatistics;
import hr.dgecek.newsparser.newsstatistics.TopicsStatisticsRepository;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.utils.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        topics.add(stemmer.stem("hns"));
        topics.add(stemmer.stem("taritaš"));
        topics.add(stemmer.stem("vrdoljak"));

        topics.add(stemmer.stem("kerum"));
        topics.add(stemmer.stem("bandić"));

        topics.add(stemmer.stem("todorić"));
        topics.add(stemmer.stem("agrokor"));
        topics.add(stemmer.stem("ledo"));

        topics.add(stemmer.stem("trump"));
        topics.add(stemmer.stem("merkel"));
        topics.add(stemmer.stem("vučić"));
        topics.add(stemmer.stem("putin"));
    }

    public void start() throws ParseException {
        final List<NewsArticle> articles = articleRepository.getAll();

        topicsStatisticsRepository.clear();

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final Calendar calendar = new GregorianCalendar();
        //the date when subjects gathering algorithm is implemented
        calendar.setTime(sdf.parse("2017-05-15T18:16:00"));

        final Date time = calendar.getTime();

        for (final NewsArticle article : articles) {
            final Set<String> subjects = article.getSubjects();
            final String title = article.getTitle();
            final String portal = article.getPortal();
            final String sentiment = article.getPredictedSentiment();
            final Date date = article.getDate();

            final Set<String> subjectsAndTitle = new HashSet<>();

            if (date != null && date.after(time) && title != null) {
                if (subjects != null) {
                    subjectsAndTitle.addAll(subjects);
                }
                // if it is in the title it is probably the subject
                subjectsAndTitle.addAll(Arrays.asList(stemmer.stemLine(TextUtils.removeInterpunction(title).toLowerCase()).split(" ")));
                subjectsAndTitle.stream()
                        .filter(topics::contains)
                        .forEach(subject ->
                                topicsStatisticsRepository.put(subject, portal, sentiment)
                        );
            }

        }

        printStatistics();
    }

    private void printStatistics() {
        final List<TopicStatistics> topicStatisticsList = topicsStatisticsRepository.getAll();
        Collections.sort(topicStatisticsList, (topicStatistics1, topicStatistics2) ->
                (topicStatistics1.getPortal().charAt(0) - topicStatistics2.getPortal().charAt(0)) * 1000
                        + topicStatistics1.getSubject().charAt(0) - topicStatistics2.getSubject().charAt(0));

        String oldPortal = "";

        for (final TopicStatistics topicStatistics : topicStatisticsList) {
            final String newPortal = topicStatistics.getPortal();
            if (!oldPortal.equals(newPortal)) {
                System.out.println();
                System.out.println(newPortal);
                oldPortal = newPortal;
            }
            System.out.println();
            System.out.print(topicStatistics.getSubject());
            System.out.print(
                    ", pos=" + topicStatistics.getStatistics().getPositive() + ", " +
                            "neg=" + topicStatistics.getStatistics().getNegative() + ", " +
                            "neu=" + topicStatistics.getStatistics().getNeutral()
            );
        }
    }
}
