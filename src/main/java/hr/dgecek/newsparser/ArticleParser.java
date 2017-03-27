package hr.dgecek.newsparser;

import org.jsoup.Jsoup;

/**
 * Created by dgecek on 01.11.16..
 */
public final class ArticleParser {

    public static String parse(String unparsedArticle) {

        unparsedArticle = unparsedArticle.replace("<br>", "").replace("<strong>", "").replace("</strong>", "").replace("<em>", "").replace("</em>", "")
                .replace("<section class=\"articleBody\">", "");

        unparsedArticle = unparsedArticle.split("<a class=")[0];
        unparsedArticle = unparsedArticle.split("<script>")[0];

        unparsedArticle = unparsedArticle.replace("\n", "").replace("  ", " ");

        unparsedArticle = unparsedArticle.replaceAll("(<!-- start:article image -->)(.)*(<!-- end:article image -->)", "");


        return Jsoup.parse(unparsedArticle).text();

    }
}
//em