package hu.possible.demo.rssreader.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.possible.demo.rssreader.R;
import hu.possible.demo.rssreader.adapters.RssSourceListAdapter;
import hu.possible.demo.rssreader.models.ContentState;
import hu.possible.demo.rssreader.models.RssSource;
import hu.possible.demo.rssreader.utils.StringUtils;
import hu.possible.demo.rssreader.utils.ui.RegularSpacerItemDecoration;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AbstractActivity {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTANTS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTANTS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.rss_sources_list_recyclerView)
    RecyclerView mRecyclerView;

    RssSourceListAdapter mRssSourceListAdapter;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// Activity OVERRIDES
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        List<String> list = new ArrayList<>();
        list.add(0, "");

        ButterKnife.bind(this);

        setupToolbar(false, true, true, false, true);

        mRssSourceListAdapter = new RssSourceListAdapter(this);

        int spacing = (int) getResources().getDimension(R.dimen.rss_source_list_recyclerView_spacing);
        int firstRowSpacing = (int) getResources().getDimension(R.dimen.rss_source_list_recyclerView_firstRowSpacing);
        int lastRowSpacing = (int) getResources().getDimension(R.dimen.rss_source_list_recyclerView_lastRowSpacing);

        mRecyclerView.addItemDecoration(new RegularSpacerItemDecoration(1, firstRowSpacing, lastRowSpacing, spacing, true));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRssSourceListAdapter);

        mRssSourceListAdapter.getOnClickObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RssSource>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull RssSource rssSource) {
                        Snackbar
                                .make(getRootContentView(), rssSource.getUrl(), Snackbar.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        mRssSourceListAdapter.getOnRemoveObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RssSource>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull RssSource rssSource) {
                        mContentManager.removeRssSource(rssSource);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
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
                break;
            case READY:
                displayContent();
                break;
            case EMPTY:
                clearContent();
                break;
            case ERROR:
            case ERROR_DB:
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
    protected void onAddSelected() {
        showAddRssSourceDialog();
    }

    @Override
    protected void onSortSelected() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// TOOLBAR SUPPORT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DIALOG SUPPORT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void showAddRssSourceDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_rss_source, null);

        final EditText editText = dialogView.findViewById(R.id.dialog_add_rss_source_input);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.dialog_add_rss_source_title);
        dialogBuilder.setPositiveButton(R.string.dialog_add_rss_source_add, (dialog, whichButton) -> {
            String input = editText.getText().toString();

            if (TextUtils.isEmpty(input)) {
                Snackbar
                        .make(getRootContentView(), R.string.dialog_add_rss_source_empty, Snackbar.LENGTH_SHORT)
                        .show();
            } else if (!StringUtils.isValidUrl(input)) {
                Snackbar
                        .make(getRootContentView(), R.string.dialog_add_rss_source_invalid_url, Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                mContentManager.addNewRssSource(input);
            }
        });

        dialogBuilder.setNegativeButton(R.string.dialog_add_rss_source_cancel, (dialog, whichButton) -> {
        });

        AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DIALOG SUPPORT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SNACK BAR SUPPORT
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public View getRootContentView() {
        return findViewById(android.R.id.content);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SNACK BAR SUPPORT - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATA HANDLING
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void clearContent() {
        mRssSourceListAdapter.clearItemsIfAny();
        mRssSourceListAdapter.notifyDataSetChanged();
    }

    private void displayContent() {
        mRssSourceListAdapter.replaceItems(mContentManager.getRssSources());
        mRssSourceListAdapter.notifyDataSetChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DATA HANDLING - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
