package hu.possible.demo.rssreader.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hu.possible.demo.rssreader.R;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = ApplicationModule.class)
public class HttpClientModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
            Timber.tag(context.getString(R.string.log_http_tag));
            Timber.d(message);
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Builder builder =
                new OkHttpClient.Builder()
                        .addInterceptor(logging);

        return builder.build();
    }
}

