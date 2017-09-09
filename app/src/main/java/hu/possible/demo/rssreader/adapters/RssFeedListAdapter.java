package hu.possible.demo.rssreader.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import hu.possible.demo.rssreader.R;
import hu.possible.demo.rssreader.adapters.viewholders.RssFeedListViewHolder;
import hu.possible.demo.rssreader.models.ContentOrderMode;
import hu.possible.demo.rssreader.models.Item;
import hu.possible.demo.rssreader.utils.ModelHelpers;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RssFeedListAdapter extends RecyclerView.Adapter<ViewHolder> {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Context mContext;

    private List<Item> mUnorderedItems = new ArrayList<>();
    private List<Item> mItems = new ArrayList<>();

    private ContentOrderMode mContentOrderMode = ContentOrderMode.DEFAULT;

    private final PublishSubject<Item> mOnClickSubject = PublishSubject.create();
    private final PublishSubject<Item> mOnShareSubject = PublishSubject.create();
    private final PublishSubject<Item> mOnRemoveSubject = PublishSubject.create();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RssFeedListAdapter(Context context) {
        mContext = context;
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

        View view = mInflater.inflate(R.layout.rss_feed_list_item, viewGroup, false);
        RssFeedListViewHolder rssFeedListViewHolder = new RssFeedListViewHolder(view);

        view.setTag(rssFeedListViewHolder);

        return rssFeedListViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = getItemForPosition(position);
        RssFeedListViewHolder rssFeedListViewHolder = (RssFeedListViewHolder) holder;

        rssFeedListViewHolder.getTitle().setText(ModelHelpers.resolveTitleFromItem(item));
        rssFeedListViewHolder.getDescription().setText(ModelHelpers.resolveDescriptionFromItem(item));
        rssFeedListViewHolder.getPublicationDate().setText(ModelHelpers.resolveFormattedPublicationDateFromItem(item));

        String imageUrl = ModelHelpers.resolveImageUrlFromItem(item);

        if (!TextUtils.isEmpty(imageUrl)) {
            Glide
                    .with(mContext)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher)
                            .centerCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(rssFeedListViewHolder.getImage());
        } else {
            rssFeedListViewHolder.getImage().setImageResource(R.mipmap.ic_launcher);
        }

        rssFeedListViewHolder.getMenu().setTag(item);
        rssFeedListViewHolder.getMenu().setOnClickListener(view -> openOptionMenu(view, (Item) view.getTag()));

        rssFeedListViewHolder.itemView.setOnClickListener(v -> mOnClickSubject.onNext(item));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// RECYCLE VIEW OVERRIDES - - EMD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void openOptionMenu(View view, final Item item) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);

        popup.getMenuInflater().inflate(R.menu.rss_feed_list_item_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.rssFeedList_item_share:
                    mOnShareSubject.onNext(item);
                    break;
                case R.id.rssFeedList_item_remove:
                    mOnRemoveSubject.onNext(item);
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

    public Observable<Item> getOnClickObservable(){
        return mOnClickSubject;
    }

    public Observable<Item> getOnShareObservable(){
        return mOnShareSubject;
    }

    public Observable<Item> getOnRemoveObservable(){
        return mOnRemoveSubject;
    }

    private Item getItemForPosition(int position) {
        return mItems.get(position);
    }

    public void addItems(List<Item> items) {
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
                throw new UnsupportedOperationException();
            case ORDER_BY_TITLE_DESC:
                throw new UnsupportedOperationException();
            case ORDER_BY_PUB_DATE_ASC:
                orderItemsByPubDate(false);
                break;
            case ORDER_BY_PUB_DATE_DESC:
                orderItemsByPubDate(true);
                break;
            case DEFAULT:
                mItems.clear();
                mItems.addAll(mUnorderedItems);
                break;
            default:
                break;
        }
    }

    private void orderItemsByPubDate(boolean ascending) {
        Collections.sort(mItems, (first, second) -> {
            Date firstPubDate = ModelHelpers.resolvePublicationDateFromItem(first);
            Date secondPubDate = ModelHelpers.resolvePublicationDateFromItem(second);

            if (firstPubDate == null || secondPubDate == null) {
                return 0;
            }

            if (ascending) {
                return firstPubDate.compareTo(secondPubDate);
            } else {
                return secondPubDate.compareTo(firstPubDate);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// HELPER METHODS  - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
