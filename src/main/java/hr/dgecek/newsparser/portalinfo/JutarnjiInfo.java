package hr.dgecek.newsparser.portalinfo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by dgecek on 15.11.16..
 */
public final class JutarnjiInfo extends PortalInfo {

    private static final String URL_PATTERN = "http://www\\.jutarnji\\.hr/.+/.+/\\w+-\\w+-.+/\\d+/";
    private static final String PORTAL_NAME = "Jutarnji";

    @Override
    public String getURL() {
        return "";
    }

    @Override
    public boolean checkNewsURLRegex(final String url) {
        //http://www.jutarnji.hr/vijesti/svijet/iracki-obavjestajci-i-isis-ovi-zarobljenici-detaljno-opisali-sto-se-danas-dogada-unutar-opkoljenog-grada/5272487/
        final Pattern pattern = java.util.regex.Pattern.compile(URL_PATTERN);
        boolean b = pattern.matcher(url).matches();
        return b;
    }

    @Override
    public String getArticleSelector() {
        return "#CImaincontent";
    }

    @Override
    public String getArchiveURL() {
        return "http://www.jutarnji.hr/";
    }

    @Override
    public String getCategoryFromUrl(final String url) {
        try {
            return url.split("http://www\\.jutarnji\\.hr/")[1].split("/")[0];
        } catch (final ArrayIndexOutOfBoundsException e) {
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
                .filter(element -> "LANDSCAPE".equals(element.attr("data-variant")))
                .findFirst();
    }


}
