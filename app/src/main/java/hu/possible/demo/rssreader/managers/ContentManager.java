package hu.possible.demo.rssreader.managers;

import java.util.List;

import hu.possible.demo.rssreader.models.ContentState;
import hu.possible.demo.rssreader.models.RssFeed;
import hu.possible.demo.rssreader.models.RssSource;
import hu.possible.demo.rssreader.models.StateObject;
import hu.possible.demo.rssreader.models.StateObject.State;
import hu.possible.demo.rssreader.networking.RssReaderClient;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class ContentManager {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DatabaseManager mDatabaseManager;

    private RssReaderClient mRssReaderClient;

    private StateObject<List<RssSource>> mRssSources = new StateObject<>(State.LOADING);

    private BehaviorSubject<ContentState> mContentStatusSubject = BehaviorSubject.createDefault(ContentState.LOADING);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ContentManager(DatabaseManager databaseManager, RssReaderClient rssReaderClient) {
        mDatabaseManager = databaseManager;
        mRssReaderClient = rssReaderClient;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// NETWORK OPERATIONS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// NETWORK OPERATIONS - - EMD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void getRssFeed(String url) {
        mRssReaderClient.getRssFeed(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RssFeed>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull RssFeed rssFeed) {
                        //mRssSources = new StateObject<>(StateObject.State.OK, rssFeed);

                        mContentStatusSubject.onNext(mRssSources.getState().equals(State.OK) ? ContentState.READY : ContentState.EMPTY);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mContentStatusSubject.onNext(ContentState.ERROR);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// MANAGER EVENTS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<ContentState> getContentStatusObservable() {
        return mContentStatusSubject;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// MANAGER EVENTS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATA OPERATIONS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<RssSource> getRssSources() {
        return mRssSources.get();
    }

    public void addNewRssSource(String url) {
        mContentStatusSubject.onNext(ContentState.LOADING);

        RssSource rssSource = new RssSource();

        rssSource.setUrl(url);

        mDatabaseManager.insertNewRssSource(rssSource)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Timber.d("addNewRssSource -> onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Object object) {
                        Timber.d("addNewRssSource -> onNext");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.d("addNewRssSource -> onError");

                        mContentStatusSubject.onNext(ContentState.ERROR_DB);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("addNewRssSource -> onComplete");

                        initializeRssSources();
                    }
                });
    }

    public void removeRssSource(RssSource rssSource) {
        mContentStatusSubject.onNext(ContentState.LOADING);

        mDatabaseManager.removeRssSource(rssSource)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Timber.d("removeRssSource -> onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Object object) {
                        Timber.d("removeRssSource -> onNext");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.d("removeRssSource -> onError");

                        mContentStatusSubject.onNext(ContentState.ERROR_DB);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("removeRssSource -> onComplete");

                        initializeRssSources();
                    }
                });
    }

    public void initializeRssSources() {
        mContentStatusSubject.onNext(ContentState.LOADING);

        mDatabaseManager.getRssSources()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StateObject<List<RssSource>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Timber.d("initializeRssSources -> onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull StateObject<List<RssSource>> rssSources) {
                        Timber.d("initializeRssSources -> onNext");

                        mRssSources = rssSources;

                        mContentStatusSubject.onNext(mRssSources.getState().equals(State.OK) ? ContentState.READY : ContentState.EMPTY);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.d("initializeRssSources -> onError");

                        mContentStatusSubject.onNext(ContentState.ERROR_DB);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("initializeRssSources -> onComplete");
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATA OPERATIONS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
