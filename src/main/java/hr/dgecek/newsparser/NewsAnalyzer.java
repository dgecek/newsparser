package hr.dgecek.newsparser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by dgecek on 27.05.17..
 */
public final class NewsAnalyzer {

    private List<List<String>> topics = new ArrayList<>();

    public NewsAnalyzer() {
        fillSubjects();
    }

    private void fillSubjects() {
        final List<String> president = new ArrayList<>();
        president.add("grabar kitarović");
        topics.add(president);

        final List<String> hdz = new ArrayList<>();
        hdz.add("HDZ");
        hdz.add("plenković");
        hdz.add("hasanbegović");
        hdz.add("brkić");
        topics.add(hdz);

        final List<String> most = new ArrayList<>();
        most.add("most");
        most.add("grmoja");
        most.add("petrov");
        most.add("bulj");
        topics.add(most);

        final List<String> sdp = new ArrayList<>();
        sdp.add("SDP");
        sdp.add("bernardić");
        sdp.add("ostojić");
        sdp.add("opačić");
        topics.add(sdp);

        final List<String> ziviZid = new ArrayList<>();
        ziviZid.add("živi zid");
        ziviZid.add("sinčić");
        ziviZid.add("pernar");
        ziviZid.add("bunjac");
        topics.add(ziviZid);

        final List<String> agrokor = new ArrayList<>();
        agrokor.add("todorić");
        agrokor.add("agrokor");
        agrokor.add("ledo");
        topics.add(agrokor);

        final List<String> foreignPolitics = new ArrayList<>();
        foreignPolitics.add("trump");
        foreignPolitics.add("merkel");
        foreignPolitics.add("vučić");
        foreignPolitics.add("putin");
        topics.add(foreignPolitics);
    }

    public void start() {
        
    }
}
