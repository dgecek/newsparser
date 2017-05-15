package hr.dgecek.newsparser.portalinfo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.regex.Pattern;

import static sun.plugin.dom.html.HTMLConstants.ATTR_SRC;

/**
 * Created by dgecek on 01.11.16..
 */
public final class IndexPortalInfo extends PortalInfo {

    private static final String URL_PATTERN = "clanak\\.aspx\\?category=\\w+&id=\\w+";
    private static final String PORTAL_NAME = "Index";
    //example: clanak.aspx?category=sport&id=929321

    @Override
    public String getURL() {
        return "http://www.index.hr/mobile/";
    }

    @Override
    public boolean checkNewsURLRegex(final String url) {
        final Pattern pattern = java.util.regex.Pattern.compile(URL_PATTERN);
        return pattern.matcher(url).matches();
    }

    @Override
    public String getArticleSelector() {
        return "#articleWrapper";
    }

    @Override
    public String getArchiveURL() {
        return getURL();
    }

    @Override
    public String getCategoryFromUrl(final String url) {
        //clanak.aspx?category=vijesti&id=931228
        try {
            return url.split("category=")[1].split("&")[0];
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
        final Optional<Element> mainImageOptional = imageElements.stream()
                .filter(element -> element.attr(ATTR_SRC).contains("index.hr/thumbnail.ashx"))
                .max(this::findMaxWidth);
        mainImageOptional.ifPresent(element -> element.attr(ATTR_SRC, getAbsoluteUrl(element.attr(ATTR_SRC)).replace("/mobile", "")));
        return mainImageOptional;
    }
}
