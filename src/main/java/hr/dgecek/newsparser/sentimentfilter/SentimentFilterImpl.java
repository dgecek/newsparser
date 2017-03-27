package hr.dgecek.newsparser.sentimentfilter;

import hr.dgecek.newsparser.stemmer.SCStemmer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dgecek on 14.03.17..
 */
public final class SentimentFilterImpl implements SentimentFilter {

    private static final float MINIMUM_WEIGHT = 0.20f;
    private static final String POSITIVES_PATH = "/home/dgecek/projects/intellij/annotatedNews/crosentilex-positives.txt";
    private static final String NEGATIVES_PATH = "/home/dgecek/projects/intellij/annotatedNews/crosentilex-negatives.txt";


    private final List<String> sentimentWords = new LinkedList<>();
    private final SCStemmer stemmer;

    public SentimentFilterImpl(final SCStemmer stemmer) {
        this.stemmer = stemmer;

        fillSentimentList(POSITIVES_PATH);
        fillSentimentList(NEGATIVES_PATH);
    }

    private void fillSentimentList(final String filePath) {
        String line;
        try (
                final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), Charset.forName("UTF-8")))
        ) {
            while ((line = br.readLine()) != null) {
                final String[] lineParts = line.split(" ");
                if (lineParts.length > 1) {
                    final String word = lineParts[0];
                    final Float weight = Float.valueOf(lineParts[1]);
                    if (weight < MINIMUM_WEIGHT) {
                        break;
                    } else {
                        sentimentWords.add(stemmer.stem(word));
                    }
                }

            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String filter(final String string) {
        final String[] sentences = string.split("\\.|!|\\?");

        int numOfErasedSentences = 0;
        sentenceLoop:
        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i];
            for (final String word : sentence.split(" ")) {
                if (sentimentWords.contains(word)) {
                    //keep this sentence
                    continue sentenceLoop;
                }
            }
            sentences[i] = "";
            numOfErasedSentences++;
        }
        //System.out.println("numOfErasedSentences: " + numOfErasedSentences);
        //TODO dot, reallyyy?3

        return String.join(". ", Arrays.asList(sentences));
    }
}
