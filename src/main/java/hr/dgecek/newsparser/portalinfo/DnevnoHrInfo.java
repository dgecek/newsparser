package hr.dgecek.newsparser.portalinfo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.regex.Pattern;

import static hr.dgecek.newsparser.NewsDownloader.SRC_ATTR;

/**
 * Created by dgecek on 19.11.16..
 */
public final class DnevnoHrInfo extends PortalInfo {

    private static final String PORTAL_NAME = "DNEVNO";
    private final String URL_PATTERN = "http://www\\.dnevno\\.hr/.+/\\w+-\\w+-.+-\\d+.*";

    @Override
    public String getURL() {
        return getArchiveURL();
    }

    @Override
    public boolean checkNewsURLRegex(final String url) {
        //http://www.dnevno.hr/vijesti/svijet/trumpovih-prvih-100-dana-evo-sto-kani-uciniti-974582#axzz4QSqEOd9z
        final Pattern pattern = Pattern.compile(URL_PATTERN);
        boolean b = pattern.matcher(url).matches();
        return b;
    }

    @Override
    public String getArticleSelector() {
        return "#main #main #content .description p";
    }

    @Override
    public String getArchiveURL() {
        return "http://www.dnevno.hr/";
    }

    @Override
    public String getCategoryFromUrl(final String url) {
        try {
            return url.split("http://www\\.dnevno\\.hr/")[1].split("/")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String getName() {
        return PORTAL_NAME;
    }

    @Override
    public Optional<Element> getMainImage(final Elements imageElements) {
        return imageElements.stream()
                .filter(element -> element.attr(SRC_ATTR).contains(getName().toLowerCase()))
                .max(this::findMaxWidth);
    }
}
