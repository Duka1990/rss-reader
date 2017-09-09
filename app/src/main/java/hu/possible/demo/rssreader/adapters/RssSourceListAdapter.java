package hu.possible.demo.rssreader.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.possible.demo.rssreader.R;
import hu.possible.demo.rssreader.adapters.viewholders.RssSourceListViewHolder;
import hu.possible.demo.rssreader.models.ContentOrderMode;
import hu.possible.demo.rssreader.models.RssSource;
import hu.possible.demo.rssreader.utils.ModelHelpers;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RssSourceListAdapter extends RecyclerView.Adapter<ViewHolder> {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<RssSource> mUnorderedItems = new ArrayList<>();
    private List<RssSource> mItems = new ArrayList<>();

    private ContentOrderMode mContentOrderMode = ContentOrderMode.DEFAULT;

    private final PublishSubject<RssSource> mOnClickSubject = PublishSubject.create();
    private final PublishSubject<RssSource> mOnShareSubject = PublishSubject.create();
    private final PublishSubject<RssSource> mOnRemoveSubject = PublishSubject.create();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RssSourceListAdapter() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// RECYCLE VIEW OVERRIDES
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        View view = mInflater.inflate(R.layout.rss_source_list_item, viewGroup, false);
        RssSourceListViewHolder rssSourceListViewHolder = new RssSourceListViewHolder(view);

        view.setTag(rssSourceListViewHolder);

        return rssSourceListViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RssSource item = getItemForPosition(position);
        RssSourceListViewHolder rssSourceListViewHolder = (RssSourceListViewHolder) holder;

        rssSourceListViewHolder.getTitle().setText(ModelHelpers.resolveTitleFromRssSource(item));
        rssSourceListViewHolder.getDescription().setText(ModelHelpers.resolveDescriptionFromRssSource(item));
        rssSourceListViewHolder.getMenu().setTag(item);
        rssSourceListViewHolder.getMenu().setOnClickListener(view -> openOptionMenu(view, (RssSource) view.getTag()));

        rssSourceListViewHolder.itemView.setOnClickListener(v -> mOnClickSubject.onNext(item));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// RECYCLE VIEW OVERRIDES - - EMD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void openOptionMenu(View view, final RssSource rssSource) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);

        popup.getMenuInflater().inflate(R.menu.rss_source_list_item_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.rssSourceList_item_share:
                    mOnShareSubject.onNext(rssSource);
                    break;
                case R.id.rssSourceList_item_remove:
                    mOnRemoveSubject.onNext(rssSource);
                    break;
                default:
                    break;
            }

            return true;
        });

        popup.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// HELPER METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<RssSource> getOnClickObservable(){
        return mOnClickSubject;
    }

    public Observable<RssSource> getOnShareObservable(){
        return mOnShareSubject;
    }

    public Observable<RssSource> getOnRemoveObservable(){
        return mOnRemoveSubject;
    }

    private RssSource getItemForPosition(int position) {
        return mItems.get(position);
    }

    public void addItems(List<RssSource> items) {
        clearItemsIfAny();
        mUnorderedItems.addAll(items);
        mItems.addAll(items);

        if (mContentOrderMode != ContentOrderMode.DEFAULT) {
            orderItems(mContentOrderMode);
        }
    }

    public void clearItemsIfAny() {
        if (mItems.isEmpty()) {
            return;
        }

        mUnorderedItems.clear();
        mItems.clear();
    }

    public void orderItems(ContentOrderMode contentOrderMode) {
        mContentOrderMode = contentOrderMode;

        switch (contentOrderMode) {
            case ORDER_BY_TITLE_ASC:
                orderItemsByTitle(true);
                break;
            case ORDER_BY_TITLE_DESC:
                orderItemsByTitle(false);
                break;
            case ORDER_BY_PUB_DATE_ASC:
                throw new UnsupportedOperationException();
            case ORDER_BY_PUB_DATE_DESC:
                throw new UnsupportedOperationException();
            case DEFAULT:
                mItems.clear();
                mItems.addAll(mUnorderedItems);
                break;
            default:
                break;
        }
    }

    private void orderItemsByTitle(boolean ascending) {
        Collections.sort(mItems, (first, second) -> {
            String firstTitle = ModelHelpers.resolveTitleFromRssSource(first);
            String secondTitle = ModelHelpers.resolveTitleFromRssSource(second);

            if (TextUtils.isEmpty(firstTitle) || TextUtils.isEmpty(secondTitle)) {
                return 0;
            }

            if (ascending) {
                return firstTitle.toLowerCase().compareTo(secondTitle.toLowerCase());
            } else {
                return secondTitle.toLowerCase().compareTo(firstTitle.toLowerCase());
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// HELPER METHODS  - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
