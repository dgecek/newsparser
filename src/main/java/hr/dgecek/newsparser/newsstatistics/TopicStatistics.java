package hr.dgecek.newsparser.newsstatistics;

import hr.dgecek.newsparser.NewsAnnotator;

/**
 * Created by dgecek on 29.05.17..
 */
public final class TopicStatistics {

    private final String subject;
    private final String portal;
    private final Statistics statistics;

    public String getSubject() {
        return subject;
    }

    public String getPortal() {
        return portal;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public TopicStatistics(final String subject, final String portal) {
        this.subject = subject;
        this.portal = portal;
        this.statistics = new Statistics();
    }

    public void increment(final String sentiment) {
        switch (sentiment) {
            case NewsAnnotator.POSITIVE:
                statistics.positive++;
                break;
            case NewsAnnotator.NEGATIVE:
                statistics.negative++;
                break;
            case NewsAnnotator.NEUTRAL:
                statistics.neutral++;
                break;
            default:
                break;
        }
    }

    public static final class Statistics {
        int positive = 0;
        int negative = 0;
        int neutral = 0;

        public int getPositive() {
            return positive;
        }

        public int getNegative() {
            return negative;
        }

        public int getNeutral() {
            return neutral;
        }
    }
}
