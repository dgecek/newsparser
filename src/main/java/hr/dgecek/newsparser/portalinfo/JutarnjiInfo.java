package hr.dgecek.newsparser.portalinfo;

import javax.sound.sampled.Port;
import java.util.regex.Pattern;

/**
 * Created by dgecek on 15.11.16..
 */
public final class JutarnjiInfo implements PortalInfo {
    private String urlPattern = "http://www\\.jutarnji\\.hr/.+/.+/\\w+-\\w+-.+/\\d+/";

    @Override
    public String getURL() {
        return "";
    }

    @Override
    public boolean checkNewsURLRegex(String url) {
        //http://www.jutarnji.hr/vijesti/svijet/iracki-obavjestajci-i-isis-ovi-zarobljenici-detaljno-opisali-sto-se-danas-dogada-unutar-opkoljenog-grada/5272487/
        Pattern pattern = java.util.regex.Pattern.compile(urlPattern);
        boolean b = pattern.matcher(url).matches();
        return b;
    }

    @Override
    public String getArticleSelector() {
        return "#CImaincontent" ;
    }

    @Override
    public String getArchiveURL() {
        return "http://www.jutarnji.hr/";
    }

    @Override
    public String getCategoryFromUrl(String url) {
        try {
            return url.split("http://www\\.jutarnji\\.hr/")[1].split("/")[0];
        } catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    }

    @Override
    public String getName() {
        return "Jutarnji";
    }
}
