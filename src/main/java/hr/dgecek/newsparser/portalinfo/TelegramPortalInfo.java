package hr.dgecek.newsparser.portalinfo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.regex.Pattern;

import static sun.plugin.dom.html.HTMLConstants.ATTR_SRC;

/**
 * Created by dgecek on 10.11.16..
 */
public final class TelegramPortalInfo extends PortalInfo {

    private static final String PORTAL_NAME = "Telegram";
    private static final String URL_PATTERN = "http://www\\.telegram\\.hr/.+/.+-.+-.+/";

    @Override
    public String getURL() {
        return "";
    }

    @Override
    public boolean checkNewsURLRegex(String url) {
        //http://www.telegram.hr/politika-kriminal/gradonacelnik-dakova-sazvao-je-presicu-da-bi-pokusao-demantirati-telegram-a-onda-nam-je-blago-prijetio/
        Pattern pattern = java.util.regex.Pattern.compile(URL_PATTERN);
        boolean b = pattern.matcher(url).matches();
        return b && !getCategoryFromUrl(url).equals("tema");
    }

    @Override
    public String getArticleSelector() {
        return ".article-content p";
    }

    @Override
    public String getArchiveURL() {
        return "https://www.telegram.hr";
    }

    @Override
    public String getCategoryFromUrl(String url) {
        String category;
        try {
            category = url.split("http://www\\.telegram\\.hr/")[1].split("/")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            category = null;
        }

        return category;
    }

    @Override
    public String getName() {
        return PORTAL_NAME;
    }

    @Override
    public Optional<Element> getMainImage(final Elements imageElements) {
        return imageElements.stream()
                .filter(element -> element.attr(ATTR_SRC).contains("wp-content/uploads"))
                .max(this::findMaxWidth);
    }
}
