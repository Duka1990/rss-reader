package hu.possible.demo.rssreader.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hu.possible.demo.rssreader.managers.ContentManager;
import hu.possible.demo.rssreader.managers.DatabaseManager;
import hu.possible.demo.rssreader.networking.RssReaderClient;
import okhttp3.OkHttpClient;

@Module(includes = {HttpClientModule.class})
class ManagerModule {

    @Provides
    @Singleton
    DatabaseManager provideDatabaseManager(Context context) {
        return new DatabaseManager(context);
    }

    @Provides
    @Singleton
    RssReaderClient provideRssReaderClient(OkHttpClient httpClient) {
        return new RssReaderClient(httpClient);
    }

    @Provides
    @Singleton
    ContentManager provideContentManager(DatabaseManager databaseManager, RssReaderClient rssReaderClient) {
        return new ContentManager(databaseManager, rssReaderClient);
    }

}
