package hr.dgecek.newsparser.portalinfo;

/**
 * Created by dgecek on 27.10.16..
 */
public abstract class PortalInfo {

    public abstract String getURL();

    public abstract boolean checkNewsURLRegex(String url);

    public abstract String getArticleSelector();

    public abstract String getArchiveURL();

    public abstract String getCategoryFromUrl(String url);

    public abstract String getName();

    public final String getAbsoluteUrl(final String articleUrl) {
        if (articleUrl == null || articleUrl.startsWith("http") || articleUrl.startsWith("www.")) {
            return articleUrl;
        } else {
            String portalUrl = getURL();
            if (portalUrl != null && portalUrl.length() > 1 && portalUrl.endsWith("/")) {
                portalUrl = portalUrl.substring(0, portalUrl.length() - 1);
            }

            String onlyArticleUrl = articleUrl;
            if (articleUrl.length() > 1 && articleUrl.startsWith("/")) {
                onlyArticleUrl = articleUrl.substring(1, articleUrl.length());
            }

            String absoultePath = portalUrl + "/" + onlyArticleUrl;
            while (absoultePath.startsWith("/")) {
                absoultePath = absoultePath.replaceFirst("/", "");
            }
            return absoultePath;
        }
    }
}
