package hr.dgecek.newsparser.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dgecek on 26.03.17..
 */
public final class DateProviderImpl implements DateProvider {

    @Override
    public Date getCurrentDate() {
        return new Date();
    }

    @Override
    public Date getDateNumberOfDaysAgo(final int recentDays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -recentDays);
        return calendar.getTime();
    }
}
