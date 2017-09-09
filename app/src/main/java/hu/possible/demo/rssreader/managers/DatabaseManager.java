package hu.possible.demo.rssreader.managers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import hu.possible.demo.rssreader.exceptions.NotFoundException;
import hu.possible.demo.rssreader.models.Feed;
import hu.possible.demo.rssreader.models.Irrelevant;
import hu.possible.demo.rssreader.models.Item;
import hu.possible.demo.rssreader.models.RssSource;
import hu.possible.demo.rssreader.models.StateObject;
import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class DatabaseManager {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Realm mRealm;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DatabaseManager(Context context) {
        mRealm = Realm.getDefaultInstance();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS / INSERT
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Observable<Object> insertNewRssSource(RssSource rssSource) {
        return Observable
                .create(emitter -> {
                    try {
                        mRealm.executeTransaction(realm -> {
                            realm.copyToRealm(rssSource);

                            Timber.d("insertNewRssSource success");

                            emitter.onNext(Irrelevant.INSTANCE);
                            emitter.onComplete();
                        });
                    } catch (Exception e) {
                        Timber.e(e, "insertNewRssSource error");

                        emitter.onError(e);
                    }
                });
    }

    Observable<Object> insertNewFeedIntoRssSource(String rssSourceId, Feed feed) {
        return Observable
                .create(emitter -> {
                    try {
                        mRealm.executeTransaction(realm -> {
                            final RealmResults<RssSource> rssSources =
                                    realm
                                            .where(RssSource.class)
                                            .equalTo("mId", rssSourceId)
                                            .findAll();

                            if (rssSources.size() == 0) {
                                Timber.d("insertNewFeedIntoRssSource success, NOT FOUND");

                                emitter.onError(new NotFoundException());
                            } else {
                                RssSource rssSource = rssSources.get(0);
                                Feed currentFeed = rssSource.getFeed();

                                if (currentFeed != null) {
                                    rssSource.setFeed(null);
                                    currentFeed.deleteFromRealm();
                                }

                                Feed managedFeed = realm.copyToRealm(feed);

                                rssSource.setFeed(managedFeed);

                                Timber.d("insertNewFeedIntoRssSource success, ADDED");

                                emitter.onNext(Irrelevant.INSTANCE);
                                emitter.onComplete();
                            }
                        });
                    } catch (Exception e) {
                        Timber.e(e, "insertNewFeedIntoRssSource error");

                        emitter.onError(e);
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS / INSERT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS / SELECT
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Observable<StateObject<List<RssSource>>> getRssSources() {
        return Observable
                .create(emitter -> {
                    try {
                        final RealmResults<RssSource> rssSources =
                                mRealm
                                        .where(RssSource.class)
                                        .findAll();

                        if (rssSources.size() == 0) {
                            Timber.d("getRssSources success, EMPTY");

                            emitter.onNext(new StateObject<>(StateObject.State.EMPTY));
                            emitter.onComplete();
                        } else {
                            Timber.d("getRssSources success, OK");

                            emitter.onNext(new StateObject<>(StateObject.State.OK, new ArrayList<>(rssSources)));
                            emitter.onComplete();
                        }
                    } catch (Exception e) {
                        Timber.e(e, "getRssSources error");

                        emitter.onError(e);
                    }
                });
    }

    Observable<RssSource> getRssSource(String rssResouceId) {
        return Observable
                .create(emitter -> {
                    try {
                        final RealmResults<RssSource> rssSources =
                                mRealm
                                        .where(RssSource.class)
                                        .equalTo("mId", rssResouceId)
                                        .findAll();

                        if (rssSources.size() == 0) {
                            Timber.d("getRssSource error, NOT FOUND");

                            emitter.onError(new NotFoundException());
                        } else {
                            Timber.d("getRssSource success, FOUND");

                            emitter.onNext(rssSources.get(0));
                            emitter.onComplete();
                        }
                    } catch (Exception e) {
                        Timber.e(e, "getRssSource error");

                        emitter.onError(e);
                    }
                });
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS / SELECT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS / REMOVE
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Observable<Object> removeRssSource(RssSource rssSource) {
        return Observable
                .create(emitter -> {
                    try {
                        mRealm.executeTransaction(realm -> {
                            final RealmResults<RssSource> rssSources =
                                    realm
                                            .where(RssSource.class)
                                            .equalTo("mId", rssSource.getId())
                                            .findAll();

                            rssSources.deleteAllFromRealm();

                            Timber.d("removeRssSource success");

                            emitter.onNext(Irrelevant.INSTANCE);
                            emitter.onComplete();
                        });
                    } catch (Exception e) {
                        Timber.e(e, "removeRssSource error");

                        emitter.onError(e);
                    }
                });
    }

    Observable<Object> removeFeedItem(Item item) {
        return Observable
                .create(emitter -> {
                    try {
                        mRealm.executeTransaction(realm -> {
                            final RealmResults<Item> rssSources =
                                    realm
                                            .where(Item.class)
                                            .equalTo("mId", item.getId())
                                            .findAll();

                            rssSources.deleteAllFromRealm();

                            Timber.d("removeFeedItem success");

                            emitter.onNext(Irrelevant.INSTANCE);
                            emitter.onComplete();
                        });
                    } catch (Exception e) {
                        Timber.e(e, "removeFeedItem error");

                        emitter.onError(e);
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS / REMOVE - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATABASE OPERATIONS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
