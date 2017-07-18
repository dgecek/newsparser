package hr.dgecek.newsparser;

import hr.dgecek.newsparser.DB.ArticleRepository;
import hr.dgecek.newsparser.DB.SimilarityRepository;
import hr.dgecek.newsparser.categorizer.CategorizerImpl;
import hr.dgecek.newsparser.entity.NewsArticle;
import hr.dgecek.newsparser.entity.Similarity;
import hr.dgecek.newsparser.idf.IdfComputer;
import hr.dgecek.newsparser.stemmer.SCStemmer;
import hr.dgecek.newsparser.stopwordremover.StopWordsRemover;
import hr.dgecek.newsparser.utils.TextUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by davidgecek on 30/03/17.
 */
public final class NewsGrouper {

    private static final double K = 0.5;
    public static final double SIMILARITY_EDGE = 0.196;

    private final ArticleRepository articleRepository;
    private final SimilarityRepository similarityRepository;
    private final StopWordsRemover stopWordsRemover;
    private final IdfComputer idfComputer;
    private final SCStemmer stemmer;

    public NewsGrouper(final ArticleRepository articleRepository,
                       final SimilarityRepository similarityRepository,
                       final StopWordsRemover stopWordsRemover,
                       final IdfComputer idfComputer,
                       final SCStemmer stemmer) {
        this.articleRepository = articleRepository;
        this.similarityRepository = similarityRepository;
        this.stopWordsRemover = stopWordsRemover;
        this.idfComputer = idfComputer;
        this.stemmer = stemmer;
    }

    public void start() {
        writeTfIdfs(articleRepository.getRecentArticles());
        writeSimilarities(articleRepository.getRecentArticles());
        writeArticleSubjects(similarityRepository.getRecent());
    }

    private void writeArticleSubjects(final List<Similarity> recentSimilarities) {
        final List<Set<NewsArticle>> listOfNewsArticleSets = new ArrayList<>();

        // group articles
        for (final Similarity similarity : recentSimilarities) {
            if (similarity.getSimilarity() > SIMILARITY_EDGE) {
                final NewsArticle firstArticle = articleRepository.get(similarity.getFirstArticleId());
                final NewsArticle secondArticle = articleRepository.get(similarity.getSecondArticleId());

                final Optional<Set<NewsArticle>> firstArticleSetOptional = getArticleSetInArray(firstArticle, listOfNewsArticleSets);
                final Optional<Set<NewsArticle>> secondArticleSetOptional = getArticleSetInArray(secondArticle, listOfNewsArticleSets);

                if (firstArticleSetOptional.isPresent()) {
                    firstArticleSetOptional.ifPresent(newsArticles -> newsArticles.add(secondArticle));
                } else if (secondArticleSetOptional.isPresent()) {
                    secondArticleSetOptional.ifPresent(newsArticles -> newsArticles.add(firstArticle));
                } else {
                    final HashSet<NewsArticle> similaritySet = new HashSet<>();
                    similaritySet.add(firstArticle);
                    similaritySet.add(secondArticle);

                    listOfNewsArticleSets.add(similaritySet);
                }
            }
        }

        for (final Set<NewsArticle> newsArticleSet : listOfNewsArticleSets) {
            final Set<String> subjects = new HashSet<>();

            //add first n tfIdfs to subjects
            for (final NewsArticle newsArticle : newsArticleSet) {
                subjects.addAll(
                        newsArticle.getTfIdfs().entrySet().stream()
                                .sorted((entry1, entry2) -> (int) ((entry2.getValue() - entry1.getValue()) * 10))
                                .limit(10)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList())
                );
            }

            //remove uncommon
            for (final NewsArticle newsArticle : newsArticleSet) {
                final Iterator<String> subjectsIterator = subjects.iterator();
                while (subjectsIterator.hasNext()) {
                    final String subject = subjectsIterator.next();
                    if (!newsArticle.getTfIdfs().containsKey(subject)) {
                        subjectsIterator.remove();
                    }
                }
            }

            System.out.println();
            subjects.forEach(subject -> System.out.print(subject + ", "));

            for (final NewsArticle newsArticle : newsArticleSet) {
                newsArticle.setSubjects(subjects);
                articleRepository.update(newsArticle);
            }
        }


    }

    private Optional<Set<NewsArticle>> getArticleSetInArray(final NewsArticle element, final List<Set<NewsArticle>> listOfNewsArticleSets) {
        /*for (final Set<NewsArticle> newsArticleSet : listOfNewsArticleSets) {
            if (newsArticleSet.contains(element)) {
                return Optional.of(newsArticleSet);
            }
        }
        return Optional.empty();*/

        return listOfNewsArticleSets.stream()
                .filter(newsArticleSet -> newsArticleSet.contains(element))
                .findFirst();
    }

    private void writeSimilarities(final List<NewsArticle> recentArticles) {
        int numberOfSimilarities = 0;
        for (final NewsArticle firstNewsArticle : recentArticles) {
            if (firstNewsArticle == null) {
                continue;
            }
            final boolean similarityExistsForArticleId = similarityRepository.similarityExistsForArticleId(firstNewsArticle.getId());
            if (similarityExistsForArticleId || !CategorizerImpl.NEWS_CATEGORY.equals(firstNewsArticle.getCategory())) {
                continue;
            }
            for (final NewsArticle secondNewsArticle : recentArticles) {
                if (!firstNewsArticle.equals(secondNewsArticle) &&
                        CategorizerImpl.NEWS_CATEGORY.equals(secondNewsArticle.getCategory())) {
                    final Similarity similarity = new Similarity();
                    similarity.setFirstArticleId(firstNewsArticle.getId());
                    similarity.setSecondArticleId(secondNewsArticle.getId());
                    similarity.setDate(firstNewsArticle.getDate());

                    final double cosineSimilarity = computeCosineSimilarity(firstNewsArticle, secondNewsArticle);
                    similarity.setSimilarity(cosineSimilarity);
                    similarityRepository.addSimilarity(similarity);

                    System.out.println("similarity: " + firstNewsArticle.getUrl() + " " + secondNewsArticle.getUrl());
                    numberOfSimilarities++;
                }
            }
        }

        System.out.println("Wrote " + numberOfSimilarities + "similarities");
    }

    private double computeCosineSimilarity(final NewsArticle firstArticle, final NewsArticle secondArticle) {
        double fractionNumerator = 0;
        double firstArticleNormalizer = 0;
        double secondArticleNormalizer = 0;

        if (firstArticle.getTfIdfs() == null || secondArticle.getTfIdfs() == null) {
            return 0;
        }

        for (final Map.Entry<String, Double> tfIdf : firstArticle.getTfIdfs().entrySet()) {
            fractionNumerator += tfIdf.getValue() * secondArticle.getTfIdfs().getOrDefault(tfIdf.getKey(), 0d);
            firstArticleNormalizer += Math.pow(tfIdf.getValue(), 2);
        }
        firstArticleNormalizer = Math.sqrt(firstArticleNormalizer);

        for (final Map.Entry<String, Double> tfIdf : secondArticle.getTfIdfs().entrySet()) {
            secondArticleNormalizer += Math.pow(tfIdf.getValue(), 2);
        }
        secondArticleNormalizer = Math.sqrt(secondArticleNormalizer);

        final double fractionDenominator = firstArticleNormalizer * secondArticleNormalizer;
        if (fractionDenominator == 0) {
            return 0;
        }
        return fractionNumerator / fractionDenominator;
    }

    private void writeTfIdfs(final List<NewsArticle> recentArticles) {
        for (final NewsArticle newsArticle : recentArticles) {
            if (newsArticle.getTfIdfs() == null && CategorizerImpl.NEWS_CATEGORY.equals(newsArticle.getCategory())) {
                int maximumFrequency = 0;
                final Map<String, Integer> termNumber = new HashMap<>();
                for (String term : stopWordsRemover.removeStopWordsAndNegations(TextUtils.removeInterpunction(newsArticle.getTitleAndText())).split(" ")) {
                    term = stemmer.stem(term.trim().toLowerCase());
                    if (term.length() > 0 && TextUtils.isTermAlphaWord(term)) {
                        final int frequency = termNumber.getOrDefault(term, 0);
                        final int newFrequency = frequency + 1;
                        termNumber.put(term, newFrequency);
                        if (newFrequency > maximumFrequency) {
                            maximumFrequency = newFrequency;
                        }
                    }
                }
                final Map<String, Double> tfidfs = new HashMap<>();
                int finalMaximumFrequency = maximumFrequency;
                termNumber.forEach(
                        (term, number) -> {
                            final double tf = K + ((1 - K) * (termNumber.getOrDefault(term, 0) / finalMaximumFrequency));
                            final double idf = idfComputer.getIdfFor(term);
                            tfidfs.put(term, tf * idf);
                        }
                );
                newsArticle.setTfIdfs((HashMap<String, Double>) tfidfs);
                articleRepository.update(newsArticle);
            }
        }

        System.out.println("Wrote TF-IDF metrics");
    }
}
