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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hu.possible.demo.rssreader.R;
import hu.possible.demo.rssreader.adapters.viewholders.RssFeedListViewHolder;
import hu.possible.demo.rssreader.models.Item;
import hu.possible.demo.rssreader.utils.ModelHelpers;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

public class RssFeedListAdapter extends RecyclerView.Adapter<ViewHolder> {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Context mContext;

    private List<Item> mItems = new ArrayList<>();

    private final PublishSubject<Item> mOnClickSubject = PublishSubject.create();
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

        rssFeedListViewHolder.getTitle().setText(item.getTitle());
        rssFeedListViewHolder.getDescription().setText(item.getDescription());

        if (!TextUtils.isEmpty(item.getPubDate())) {
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

            try {
                Date date = dateFormat.parse(item.getPubDate());
                SimpleDateFormat displayedDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH);

                rssFeedListViewHolder.getPublicationDate().setText(displayedDateFormat.format(date));
            } catch (ParseException e) {
                Timber.d(e);

                rssFeedListViewHolder.getPublicationDate().setText(item.getPubDate());
            }
        } else {
            rssFeedListViewHolder.getPublicationDate().setText(null);
        }

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
                case R.id.rss_feed_list_item_remove:
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

    private Item getItemForPosition(int position) {
        return mItems.get(position);
    }

    public void addItems(List<Item> items) {
        mItems.addAll(items);
    }

    public void clearItemsIfAny() {
        if (isEmpty()) {
            return;
        }

        mItems.clear();
    }

    public void replaceItems(List<Item> items) {
        clearItemsIfAny();
        addItems(items);
    }

    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    public Observable<Item> getOnClickObservable(){
        return mOnClickSubject;
    }

    public Observable<Item> getOnRemoveObservable(){
        return mOnRemoveSubject;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// HELPER METHODS  - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
