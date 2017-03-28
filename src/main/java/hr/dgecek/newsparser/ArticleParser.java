package hr.dgecek.newsparser;

import org.jsoup.Jsoup;

/**
 * Created by dgecek on 01.11.16..
 */
public final class ArticleParser {

    public static String parse(final String unparsedArticle) {
        String parsedArticle = "";

        parsedArticle = unparsedArticle.replace("<br>", "").replace("<strong>", "").replace("</strong>", "").replace("<em>", "").replace("</em>", "")
                .replace("<section class=\"articleBody\">", "");

        parsedArticle = parsedArticle.split("<a class=")[0];
        parsedArticle = parsedArticle.split("<script>")[0];

        parsedArticle = parsedArticle.replace("\n", "").replace("  ", " ");

        parsedArticle = parsedArticle.replaceAll("(<!-- start:article image -->)(.)*(<!-- end:article image -->)", "");

        //maybe this is enough?
        return Jsoup.parse(parsedArticle).text();

    }
}
