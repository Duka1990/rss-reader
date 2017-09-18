package hu.possible.demo.rssreader.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.possible.demo.rssreader.R;
import hu.possible.demo.rssreader.adapters.RssFeedListAdapter;
import hu.possible.demo.rssreader.models.ContentOrderMode;
import hu.possible.demo.rssreader.models.ContentState;
import hu.possible.demo.rssreader.models.Item;
import hu.possible.demo.rssreader.models.RssSource;
import hu.possible.demo.rssreader.utils.AndroidUtils;
import hu.possible.demo.rssreader.utils.ModelHelpers;
import hu.possible.demo.rssreader.utils.ui.RegularSpacerItemDecoration;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class FeedActivity extends AbstractActivity {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTANTS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String PARAM_RSS_SOURCE_ID = "rssSourceId";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTANTS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Intent newIntent(Context context, String rssSourceId) {
        Intent intent = new Intent(context, FeedActivity.class);

        intent.putExtra(PARAM_RSS_SOURCE_ID, rssSourceId);

        return intent;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.feedActivity_rssFeedList_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.feedActivity_rssFeedList_progressBar)
    ProgressBar mProgressBar;

    private String mRssSourceId;

    private RssSource mRssSource;

    private RssFeedListAdapter mRssFeedListAdapter;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// Activity OVERRIDES
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRssSourceId = getIntent().getStringExtra(PARAM_RSS_SOURCE_ID);

        setContentView(R.layout.activity_feed);

        ButterKnife.bind(this);

        setupToolbar(true, false, false, true, false, true);

        mRssFeedListAdapter = new RssFeedListAdapter(this);

        int spacing = (int) getResources().getDimension(R.dimen.feedActivity_rssFeedList_recyclerView_spacing);
        int firstRowSpacing = (int) getResources().getDimension(R.dimen.feedActivity_rssFeedList_recyclerView_firstRowSpacing);
        int lastRowSpacing = (int) getResources().getDimension(R.dimen.feedActivity_rssFeedList_recyclerView_lastRowSpacing);

        mRecyclerView.addItemDecoration(new RegularSpacerItemDecoration(1, firstRowSpacing, lastRowSpacing, spacing, true));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRssFeedListAdapter);

        mRssFeedListAdapter.getOnClickObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Item>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Item item) {
                        Intent intent = ItemActivity.newIntent(FeedActivity.this, mRssSourceId, item.getId());

                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        mRssFeedListAdapter.getOnShareObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Item>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Item item) {
                        AndroidUtils.share(FeedActivity.this, ModelHelpers.resolveTitleFromItem(item), item.getLink());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        mRssFeedListAdapter.getOnRemoveObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Item>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Item item) {
                        mContentManager.removeFeedItem(item);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        // With the current functionality only existing rss sources can be opened in this Activity.
        // Therefore mContentManager.getRssSource(...) cannot return null here.
        mRssSource = mContentManager.getRssSource(mRssSourceId);
        mContentManager.loadFeedInRssSource(mRssSource, false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// Activity OVERRIDES - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SUBSCRIPTION HANDLING
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SUBSCRIPTION HANDLING / CONTENT STATE
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onContentStateChanged(ContentState contentState) {
        switch (contentState) {
            case LOADING:
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case READY:
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                displayContent();
                break;
            case EMPTY:
                mProgressBar.setVisibility(View.GONE);

                clearContent();
                break;
            case ERROR:
                mProgressBar.setVisibility(View.GONE);

                showFeedErrorDialog();
                break;
            default:
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SUBSCRIPTION HANDLING / CONTENT STATE - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SUBSCRIPTION HANDLING - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// TOOLBAR SUPPORT
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected ContentOrderPermission getContentOrderPermission () {
        return ContentOrderPermission.ORDER_BY_PUB_DATE;
    }

    @Override
    protected void onRefreshSelected() {
        mContentManager.loadFeedInRssSource(mRssSource, true);
    }

    @Override
    protected void onOrderSelected(ContentOrderMode contentOrderMode) {
        mRssFeedListAdapter.orderItems(contentOrderMode);
        mRssFeedListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onShareSelected() {
        AndroidUtils.share(FeedActivity.this, ModelHelpers.resolveTitleFromRssSource(mRssSource), mRssSource.getUrl());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// TOOLBAR SUPPORT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DIALOG SUPPORT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void showFeedErrorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setMessage(R.string.dialog_feedError_description);
        dialogBuilder.setPositiveButton(R.string.dialog_feedError_ok, (dialog, whichButton) -> {
        });

        AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DIALOG SUPPORT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATA HANDLING
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void clearContent() {
        mRssFeedListAdapter.clearItemsIfAny();
        mRssFeedListAdapter.notifyDataSetChanged();
    }

    private void displayContent() {
        mRssSource = mContentManager.getRssSource(mRssSourceId);

        if (mRssSource.getFeed() == null) {
            return;
        }

        mRssFeedListAdapter.addItems(mRssSource.getFeed().getChannel().getItems());
        mRssFeedListAdapter.notifyDataSetChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATA HANDLING - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
