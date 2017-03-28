package hr.dgecek.newsparser.portalinfo;

import java.util.regex.Pattern;


/**
 * Created by dgecek on 27.10.16..
 */
public final class TportalPortalInfo implements PortalInfo {

    // example: http://www.tportal.hr/vijesti/hrvatska/450709/USKOK-se-zainteresirao-za-gradnju-nebodera-u-Maksimiru.html
    //private static final String urlPattern = "\\/\\w+\\/\\d+\\/.+\\.html";
    private static final String URL_PATTERN = ".*www\\.tportal\\.hr/.+/clanak/.+";
    private static final String PORTAL_NAME = "tportal";

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

}
