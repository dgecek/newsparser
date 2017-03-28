package hr.dgecek.newsparser.DB;

import com.mongodb.MongoClient;
import hr.dgecek.newsparser.date.DateProvider;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by dgecek on 10.11.16..
 */
public final class MorphiaManager {

    private static final String DB_NAME = "newsParser";
    private static final String PACKAGE_NAME = "hr.dgecek.newsparser.entity";

    private static ArticleRepositoryImpl datastoreInstance;

    public static ArticleRepositoryImpl getDataStore(final DateProvider dateProvider) {
        if(datastoreInstance == null) {
            final Morphia morphia = new Morphia();

            // tell Morphia where to find your classes
            // can be called multiple times with different packages or classes
            morphia.mapPackage(PACKAGE_NAME);

            // create the Datastore connecting to the default port on the local host
            final Datastore datastore = morphia.createDatastore(new MongoClient(), DB_NAME);
            datastore.ensureIndexes();

            datastoreInstance = new ArticleRepositoryImpl(datastore, dateProvider);
        }

        return datastoreInstance;
    }

    private MorphiaManager(){

    }
}
