package hr.dgecek.newsparser.stopwordremover;

import hr.dgecek.newsparser.NegationsManager;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dgecek on 23.12.16..
 */
public final class StopWordsRemoverImpl implements StopWordsRemover {

    private static final String PATH = "/home/dgecek/projects/intellij/annotatedNews/stopwords.txt";

    private final List<String> stopWords;
    private final NegationsManager negationsManager;

    public StopWordsRemoverImpl(final NegationsManager negationsManager) {
        this.negationsManager = negationsManager;
        this.stopWords = new ArrayList<>(200);
        fillStopWords();
    }

    private void fillStopWords() {
        String line;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(PATH), Charset.forName("UTF-8")))) {
            while ((line = br.readLine()) != null) {
                stopWords.add(line.trim());
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String removeStopWordsLeaveNegations(final String string) {
        final String[] words = string.split(" ");
        for (int i = 0; i< words.length; i++) {
            if(stopWords.contains(words[i])){
                words[i] = "";
            }
        }
        return String.join(" ", words);
    }

    @Override
    public String removeStopWordsAndNegations(final String string) {
        return negationsManager.removeNegations(removeStopWordsLeaveNegations(string));
    }
}
