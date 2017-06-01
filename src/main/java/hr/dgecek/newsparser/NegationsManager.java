package hr.dgecek.newsparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dgecek on 25.12.16..
 */
public final class NegationsManager {

    private final List<String> negations = new ArrayList<>();

    public NegationsManager() {
        negations.add(("ne"));
        negations.add(("nem"));
        negations.add(("neć"));
        negations.add(("nećem"));
        negations.add(("nećet"));
        negations.add(("nećeš"));
        negations.add(("ni"));
        negations.add(("nij"));
        negations.add(("nis"));
        negations.add(("nist"));
        negations.add(("nism"));
        negations.add(("nik"));
        negations.add(("nikoj"));
        negations.add(("nisa"));
/*
       ne nem neć nećem nećet nećeš neć ni nij nik nikoj nikoj nisa nis nism nist nis*/
    }

    public String getNegationsInArticle(final String string) {
        final StringBuilder negations = new StringBuilder();
        for (final String word : string.split(" ")) {
            if (this.negations.contains(word)) {
                negations.append(word).append(",");
            }
        }
        if (negations.length() == 0) {
            return "";
        } else {
            return negations.deleteCharAt(negations.length() - 1).toString();
        }
    }

    public String removeNegations(final String string){
        final String[] words = string.split(" ");
        for (int i = 0; i< words.length; i++) {
            if(negations.contains(words[i])){
                words[i] = "";
            }
        }
        return String.join(" ", words);
    }
}
