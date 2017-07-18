package hr.dgecek.newsparser.utils;

import org.jsoup.Jsoup;

/**
 * Created by dgecek on 01.11.16..
 */
public final class TextUtils {

    public static String removeHTMLAndJS(final String unparsedArticle) {
        String parsedArticle = "";

        parsedArticle = Jsoup.parse(unparsedArticle).text();

        parsedArticle = parsedArticle.replace("<br>", "").replace("<strong>", "").replace("</strong>", "").replace("<em>", "").replace("</em>", "")
                .replace("<section class=\"articleBody\">", "");

        parsedArticle = parsedArticle.split("<a class=")[0];
        parsedArticle = parsedArticle.split("<script>")[0];
        parsedArticle = parsedArticle.split("PROČITAJTE JOŠ")[0]; //index

        parsedArticle = parsedArticle.replace("\n", "").replace("  ", " ");

        parsedArticle = parsedArticle.replaceAll("(<!-- start:article image -->)(.)*(<!-- end:article image -->)", "");

        return parsedArticle;
    }

    public static String removeInterpunction(final String string) {
        final String newString = string.replace("\t", "")
                .replace("\n", "")
                .replace(".", "")
                .replace("?", "")
                .replace("!", "")
                .replace(",", "")
                .replace(":", "")
                .replace(";", "")
                .replace("'", "")
                .replace("\"", "")
                .replace("  ", "")
                .replace(")", "")
                .replace("(", "")
                .replace("|", "");

        return newString;
    }

    public static String formatInterpunction(final String string) {
        String newString = string.replace("\t", " ")
                .replace("\n", " ")
                .replace(".", " .")
                .replace("?", " ?")
                .replace("!", " !")
                .replace(",", " ,")
                .replace(":", " :")
                .replace(";", " ;")
                .replace("'", " ' ")
                .replace("\"", " \" ")
                .replace("  ", " ");

        return newString;
    }

    public static boolean isTermAlphaWord(final String term) {
        if (term == null || "".equals(term.trim())) {
            return false;
        }
        boolean hasNonAlpha = term.matches("^.*[^a-zA-Z].*$");
        return !hasNonAlpha;
    }
}
