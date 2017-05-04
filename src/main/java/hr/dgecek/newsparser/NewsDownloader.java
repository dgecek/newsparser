package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.date.DateProvider;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.portalinfo.*;
import hr.dgecek.newsparser.utils.TextUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by dgecek on 27.10.16..
 */
public final class NewsDownloader {

    private static final String LINKS_SELECTOR = "a[href]";
    private static final String HREF_SELECTOR = "href";
    private static final String IMG_TAG = "img";
    public static final String SRC_ATTR = "src";

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int WAIT_BEFORE_NEXT_CONNECTION_MILLIS = 200;
    private static final long WAIT_IF_CONNECTION_REFUSED_MILLIS = 500;
    private static final List<PortalInfo> portalInfoList;
    public static final String WIDTH_KEY = "width";
    private static final String META_PROPERTY_TITLE = "meta[property=og:title]";
    private static final String CONTENT_ATTRIBUTE = "content";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String REFERRER = "http://www.google.com";

    private final ArticleRepository dataStore;
    private final List<NewsArticle> recentArticles;
    private final DateProvider dateProvider;
    private final List<String> triedUrls = new ArrayList<>();

    static {
        portalInfoList = new ArrayList<>();

        //add all portals
        portalInfoList.add(new DnevnoHrInfo());
        portalInfoList.add(new JutarnjiInfo());
        portalInfoList.add(new TelegramPortalInfo());
        portalInfoList.add(new TportalPortalInfo());
        portalInfoList.add(new IndexPortalInfo());
    }


    public NewsDownloader(final ArticleRepository datastore, final DateProvider dateProvider) {
        this.dataStore = datastore;
        this.recentArticles = datastore.getRecentArticles();
        this.dateProvider = dateProvider;
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
        dataStore.clearStatistics();
    }

    private void saveAllArticlesFromLinks(final Elements links, final PortalInfo portalInfo) throws InterruptedException {
        for (final Element link : links) {
            final String articleUrl = link.attr(HREF_SELECTOR);
            if (portalInfo.checkNewsURLRegex(articleUrl) && !isArticleAlreadySaved(articleUrl)) {
                final NewsArticle article = new NewsArticle();

                if (triedUrls.contains(articleUrl)) {
                    continue;
                } else {
                    triedUrls.add(articleUrl);
                }
                article.setUrl(portalInfo.getAbsoluteUrl(articleUrl));

                waitBeforeNextRequest();
                final Document articleDOM = getArticleDocument(portalInfo, article);

                if (articleDOM == null) {
                    waitIfConnectionRefused();
                    continue;
                }

                final String title = getTitle(articleDOM);
                article.setTitle(title);

                final String unparsedArticle = articleDOM.select(portalInfo.getArticleSelector()).toString();
                final String parsedArticle = TextUtils.removeHTMLAndJS(unparsedArticle);

                article.setBody(parsedArticle);
                article.setCategory(portalInfo.getCategoryFromUrl(article.getUrl()));
                article.setPortal(portalInfo.getName());
                article.setDate(dateProvider.getCurrentDate());
                article.setUrlToImage(portalInfo.getAbsoluteUrl(getMainImageFromPage(articleDOM, portalInfo)));

                dataStore.addArticle(article);
            }
        }
    }

    private String getMainImageFromPage(final Document articleDOM, final PortalInfo portalInfo) {
        final Elements imageElements = articleDOM.getElementsByTag(IMG_TAG);
        final Optional<Element> mainImageOptional = portalInfo.getMainImage(imageElements);

        if (mainImageOptional.isPresent()) {
            final Element largestImage = mainImageOptional.get();
            return largestImage.attr(SRC_ATTR);
        } else {
            return "";
        }
    }

    private boolean isArticleAlreadySaved(final String articleUrl) {
        return articleUrl == null || recentArticles.stream().anyMatch(newsArticle -> newsArticle.getUrl().equals(articleUrl));
    }

    private void waitIfConnectionRefused() {
        try {
            Thread.sleep(WAIT_IF_CONNECTION_REFUSED_MILLIS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitBeforeNextRequest() {
        try {
            Thread.sleep(WAIT_BEFORE_NEXT_CONNECTION_MILLIS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Document getArticleDocument(final PortalInfo portal, final NewsArticle article) {
        try {
            final Connection connection = getConnection(portal.getAbsoluteUrl(article.getUrl()));
            return connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTitle(final Document articleDOM) {
        String title;
        final Elements metaOgTitle = articleDOM.select(META_PROPERTY_TITLE);
        if (metaOgTitle != null) {
            title = metaOgTitle.attr(CONTENT_ATTRIBUTE);
        } else {
            title = articleDOM.title();
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

    private Connection getConnection(final String url) {
        String parsedUrl = url;

        while (parsedUrl.startsWith("/")) {
            parsedUrl = parsedUrl.replaceFirst("/", "");
        }
        if (parsedUrl.startsWith("www")) {
            parsedUrl = "http://" + parsedUrl;
        }

        final Connection connection = Jsoup.connect(parsedUrl)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .timeout(CONNECTION_TIMEOUT);
        return connection;
    }
}
