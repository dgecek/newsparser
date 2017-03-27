package hr.dgecek.newsparser.date;

import java.util.Date;

/**
 * Created by dgecek on 26.03.17..
 */
public interface DateProvider {

    Date getCurrentDate();

    Date getDateNumberOfDaysAgo(int recentDays);
}
