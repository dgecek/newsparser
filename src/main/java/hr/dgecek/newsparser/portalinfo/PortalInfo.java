package hr.dgecek.newsparser.portalinfo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

import static hr.dgecek.newsparser.NewsDownloader.WIDTH_KEY;

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

    public abstract Optional<Element> getMainImage(Elements imageElements);

    public final String getAbsoluteUrl(final String articleUrl) {
        if (articleUrl == null || articleUrl.startsWith("http") || articleUrl.startsWith("www.") ||
                articleUrl.startsWith("//www.")) {
            return articleUrl;
        } else {
            String portalUrl = getArchiveURL();
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

    protected int findMaxWidth(final Element o1, final Element o2) {
        Integer width1, width2;
        try {
            width1 = Integer.valueOf(o1.attr(WIDTH_KEY));
        } catch (final NumberFormatException exception) {
            return -1;
        }
        try {
            width2 = Integer.valueOf(o2.attr(WIDTH_KEY));
        } catch (final NumberFormatException exception) {
            return 1;
        }

        return width1 - width2;
    }
}
