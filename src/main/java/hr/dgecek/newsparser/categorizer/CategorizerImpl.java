package hr.dgecek.newsparser.categorizer;

import java.util.HashMap;
import java.util.Map;

public final class CategorizerImpl implements Categorizer {
    final private Map<String, String> categories = new HashMap<>();

   /* vijesti:vijesti:politika-kriminal:globus:biznis
    kolumnisti
    reality:spektakli:rouge:black
    mame
    sport:sport
    kultura
    tech
    ljubimci
    zdravzivot:dobrahrana:fit
    vjera
    moda:lifestyle:domidizajn
    auto:auto:autoklub
    promo
    zivot:life:zivot
    internet:viral:video:magazin
*/
    public CategorizerImpl() {
        categories.put("politika-kriminal", "vijesti");
        categories.put("globus", "vijesti");
        categories.put("biznis", "vijesti");
        categories.put("kolumnisti", "vijesti");

        categories.put("spektakli", "reality");
        categories.put("rouge", "reality");
        categories.put("black", "reality");

        categories.put("dobrahrana", "zdravzivot");
        categories.put("fit", "zdravzivot");

        categories.put("lifestyle", "moda");
        categories.put("domidizajn", "moda");

        categories.put("autoklub", "auto");

        categories.put("life", "zivot");

        categories.put("viral", "internet");
        categories.put("video", "internet");
        categories.put("magazin", "internet");

        categories.put("biznis-tech", "tech");
    }

    @Override
    public String getCategory(final String category) {
        if(categories.containsKey(category)){
            return categories.get(category);
        } else {
            return category;
        }
    }
}
