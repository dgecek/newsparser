package hr.dgecek.newsparser.DB;

import hr.dgecek.newsparser.entity.ArticlesGroup;

import java.util.List;

/**
 * Created by dgecek on 21.07.17..
 */
public interface GroupedArticlesRepository {

    void saveAll(List<ArticlesGroup> listOfNewsArticleSets);

    void removeAll();
}
