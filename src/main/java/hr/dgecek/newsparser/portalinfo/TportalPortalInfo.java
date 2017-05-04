package hr.dgecek.newsparser.portalinfo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.regex.Pattern;

import static sun.plugin.dom.html.HTMLConstants.ATTR_SRC;


/**
 * Created by dgecek on 27.10.16..
 */
public final class TportalPortalInfo extends PortalInfo {

    // example: http://www.tportal.hr/vijesti/hrvatska/450709/USKOK-se-zainteresirao-za-gradnju-nebodera-u-Maksimiru.html
    //private static final String urlPattern = "\\/\\w+\\/\\d+\\/.+\\.html";
    private static final String URL_PATTERN = ".*www\\.tportal\\.hr/.+/clanak/.+";
    private static final String PORTAL_NAME = "tportal";
    private static final String ATTR_DATA_SRC = "data-src";

    public String getURL() {
        return "";
    }

    @Override
    public boolean checkNewsURLRegex(String url) {
        Pattern pattern = Pattern.compile(URL_PATTERN);
        boolean b = pattern.matcher(url).matches();
        return b;
    }

    @Override
    public String getArticleSelector() {
        return ".articleComponents";
    }

    @Override
    public String getArchiveURL() {
        return "http://m.tportal.hr";
    }

    @Override
    public String getCategoryFromUrl(String url) {
        try {
            while (url.startsWith("/")) {
                url = url.replaceFirst("/", "");
            }
            return url.split("/")[1];
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
                .map(element -> element.attr(ATTR_SRC, element.attr(ATTR_DATA_SRC)))
                .filter(element -> element.attr(ATTR_SRC).contains("/media/thumbnail/"))
                .findFirst();
    }

}
