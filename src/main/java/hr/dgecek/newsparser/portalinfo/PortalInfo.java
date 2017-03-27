package hr.dgecek.newsparser.portalinfo;

/**
 * Created by dgecek on 27.10.16..
 */
public interface PortalInfo {

    String getURL();

    boolean checkNewsURLRegex(String url);

    String getArticleSelector();

    String getArchiveURL();

    String getCategoryFromUrl(String url);

    String getName();
}
