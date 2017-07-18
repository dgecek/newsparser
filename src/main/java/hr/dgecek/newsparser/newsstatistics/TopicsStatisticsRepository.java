package hr.dgecek.newsparser.newsstatistics;

import java.util.List;

/**
 * Created by dgecek on 29.05.17..
 */
public interface TopicsStatisticsRepository {

    void put(String subject, String portal, String sentiment);

    List<TopicStatistics> getAll();

    void clear();
}
