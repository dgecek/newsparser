package hr.dgecek.newsparser.newsstatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by dgecek on 29.05.17..
 */
public final class TopicsStatisticsRepositoryMemoryImpl implements TopicsStatisticsRepository {

    final List<TopicStatistics> topicsStatisticsList = new ArrayList<>();

    public void put(final String subject, final String portal, final String sentiment) {
        if (portal == null || sentiment == null || subject == null) {
            return;
        }

        final Optional<TopicStatistics> topicStatisticsOptional = topicsStatisticsList.stream()
                .filter(topicStatistics ->
                        portal.equals(topicStatistics.getPortal()) && subject.equals(topicStatistics.getSubject()))
                .findFirst();


        if (topicStatisticsOptional.isPresent()) {
            topicStatisticsOptional.get().increment(sentiment);
        } else {
            final TopicStatistics topicStatistics = new TopicStatistics(subject, portal);
            topicStatistics.increment(sentiment);
            topicsStatisticsList.add(topicStatistics);
        }

    }

    @Override
    public List<TopicStatistics> getAll() {
        return topicsStatisticsList;
    }
}
