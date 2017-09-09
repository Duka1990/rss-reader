package hu.possible.demo.rssreader.managers;

import java.util.List;

import hu.possible.demo.rssreader.models.ContentState;
import hu.possible.demo.rssreader.models.Feed;
import hu.possible.demo.rssreader.models.Item;
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

    public Observable<ContentState> getContentStatusObservable() {
        return mContentStatusSubject;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// MANAGER EVENTS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// OPERATIONS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<RssSource> getRssSources() {
        return mRssSources.get();
    }

    public RssSource getRssSource(String rssSourceId) {
        for (RssSource rssSource : mRssSources.get()) {
            if (rssSource.getId().equals(rssSourceId)) {
                return rssSource;
            }
        }

        return null;
    }

    public Item getItemFromRssSource(String rssSourceId, String itemId) {
        for (RssSource rssSource : mRssSources.get()) {
            if (rssSource.getId().equals(rssSourceId)) {
                if (rssSource.getFeed() == null && rssSource.getFeed().getChannel() == null) {
                    return null;
                }

                for (Item item : rssSource.getFeed().getChannel().getItems()) {
                    if (item.getId().equals(itemId)) {
                        return item;
                    }
                }
            }
        }

        return null;
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

                        mContentStatusSubject.onNext(ContentState.ERROR);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("addNewRssSource -> onComplete");

                        loadRssSources();
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

                        mContentStatusSubject.onNext(ContentState.ERROR);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("removeRssSource -> onComplete");

                        loadRssSources();
                    }
                });
    }

    public void loadRssSources() {
        mContentStatusSubject.onNext(ContentState.LOADING);

        mDatabaseManager.getRssSources()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StateObject<List<RssSource>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Timber.d("loadRssSources -> onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull StateObject<List<RssSource>> rssSources) {
                        Timber.d("loadRssSources -> onNext");

                        mRssSources = rssSources;

                        mContentStatusSubject.onNext(mRssSources.getState().equals(State.OK) ? ContentState.READY : ContentState.EMPTY);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.d("loadRssSources -> onError");

                        mContentStatusSubject.onNext(ContentState.ERROR);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("loadRssSources -> onComplete");
                    }
                });
    }

    public void loadFeedInRssSource(String rssSourceId, boolean forceReload) {
        mContentStatusSubject.onNext(ContentState.LOADING);

        mDatabaseManager.getRssSource(rssSourceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RssSource>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Timber.d("getRssSource -> onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull RssSource rssSource) {
                        Timber.d("getRssSource -> onNext");

                        if (forceReload || rssSource.getFeed() == null) {
                            mRssReaderClient.getFeed(rssSource.getUrl())
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Feed>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                        }

                                        @Override
                                        public void onNext(@NonNull Feed feed) {
                                            addFeedToRssSource(rssSourceId, feed);
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            mContentStatusSubject.onNext(ContentState.ERROR);
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                    });
                        } else {
                            mContentStatusSubject.onNext(ContentState.READY);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.d("getRssSource -> onError");

                        mContentStatusSubject.onNext(ContentState.ERROR);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("getRssSource -> onComplete");
                    }
                });
    }

    private void addFeedToRssSource(String rssSourceId, Feed feed) {
        mDatabaseManager.insertNewFeedIntoRssSource(rssSourceId, feed)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Timber.d("addFeedToRssSource -> onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        Timber.d("addFeedToRssSource -> onNext");

                        loadRssSources();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.d("addFeedToRssSource -> onError");

                        mContentStatusSubject.onNext(ContentState.ERROR);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("addFeedToRssSource -> onComplete");
                    }
                });

    }

    public void removeFeedItem(Item item) {
        mContentStatusSubject.onNext(ContentState.LOADING);

        mDatabaseManager.removeFeedItem(item)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Timber.d("removeFeedItem -> onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Object object) {
                        Timber.d("removeFeedItem -> onNext");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.d("removeFeedItem -> onError");

                        mContentStatusSubject.onNext(ContentState.ERROR);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("removeFeedItem -> onComplete");

                        loadRssSources();
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// OPERATIONS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
