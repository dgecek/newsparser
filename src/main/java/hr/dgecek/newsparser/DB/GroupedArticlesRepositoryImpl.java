package hr.dgecek.newsparser.DB;

import hr.dgecek.newsparser.entity.ArticlesGroup;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.List;

/**
 * Created by dgecek on 21.07.17..
 */
public final class GroupedArticlesRepositoryImpl implements GroupedArticlesRepository {

    private final Datastore datastore;

    public GroupedArticlesRepositoryImpl(final Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public void saveAll(final List<ArticlesGroup> listOfNewsArticleSets) {
        datastore.save(listOfNewsArticleSets);
    }

    @Override
    public void removeAll() {
        datastore.delete(datastore.createQuery(ArticlesGroup.class));
    }
}
