package hr.dgecek.newsparser;

import hr.dgecek.newsparser.categorizer.Categorizer;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.portalinfo.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dgecek on 27.10.16..
 */
public final class NewsDownloader {
    public static final String LINKS_SELECTOR = "a[href]";
    public static final String HREF_SELECTOR = "href";
    public static final int CONNECTION_TIMEOUT = 5000;
    public static final int WAIT_BEFORE_NEXT_CONNECTION_MILLIS = 200;
    private static final long WAIT_IF_CONNECTION_REFUSED_MILLIS = 500;
    private static final List<PortalInfo> portalInfoList;
    private final ArticleDAO dataStore;

    static {
        portalInfoList = new ArrayList<>();

        //add all portals
        portalInfoList.add(new DnevnoHrInfo());
        portalInfoList.add(new JutarnjiInfo());
        portalInfoList.add(new TelegramPortalInfo());
        portalInfoList.add(new TportalPortalInfo());
        portalInfoList.add(new IndexPortalInfo());
    }

    public NewsDownloader(final ArticleDAO datastore) {
        this.dataStore = datastore;
    }

    public void downloadNews() throws IOException, InterruptedException {
        for (final PortalInfo portal : portalInfoList) {
            final Document doc = getPortalDocument(portal);

            if (doc != null) {
                final Elements links = doc.select(LINKS_SELECTOR);
                saveAllArticlesFromLinks(links, portal);
            }
        }

        System.out.println(dataStore.getStatistics());
    }

    private void saveAllArticlesFromLinks(final Elements links, final PortalInfo portal) throws InterruptedException {
        for (final Element link : links) {
            final String articleUrl = link.attr(HREF_SELECTOR);
            if (portal.checkNewsURLRegex(articleUrl)) {
                final NewsArticle article = new NewsArticle();
                article.setUrl(articleUrl);

                waitBeforeNextRequest();
                final Document artDOM = getArticleDocument(portal, article);

                if (artDOM == null) {
                    waitIfConnectionRefused();
                    continue;
                }

                final String title = getTitle(artDOM);
                article.setTitle(title);

                final String unparsedArticle = artDOM.select(portal.getArticleSelector()).toString();
                final String parsedArticle = ArticleParser.parse(unparsedArticle);

                article.setBody(parsedArticle);
                article.setCategory(portal.getCategoryFromUrl(article.getUrl()));
                article.setPortal(portal.getName());

                dataStore.addArticle(article);
            }
        }
    }

    private void waitIfConnectionRefused() {
        try {
            Thread.sleep(WAIT_IF_CONNECTION_REFUSED_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitBeforeNextRequest() {
        try {
            Thread.sleep(WAIT_BEFORE_NEXT_CONNECTION_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Document getArticleDocument(PortalInfo portal, NewsArticle article) {
        try {
            final Connection connection = getConnection(portal.getURL() + article.getUrl());
            return connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTitle(final Document artDOM) {
        String title;
        final Elements metaOgTitle = artDOM.select("meta[property=og:title]");
        if (metaOgTitle != null) {
            title = metaOgTitle.attr("content");
        } else {
            title = artDOM.title();
        }
        return title;
        //return title.split("|")[0].trim();
    }

    private Document getPortalDocument(final PortalInfo portal) {
        try {
            final Connection connection = getConnection(portal.getArchiveURL());
            return connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection getConnection(String url) {
        while (url.startsWith("/")) {
            url = url.replaceFirst("/", "");
        }
        if (url.startsWith("www")) {
            url = "http://" + url;
        }
        Connection connection = Jsoup.connect(url);
        connection = connection.userAgent("Mozilla/5.0");
        connection = connection.referrer("http://www.google.com");
        return connection.timeout(CONNECTION_TIMEOUT);
    }
}
