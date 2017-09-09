package hu.possible.demo.rssreader.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hu.possible.demo.rssreader.R;
import hu.possible.demo.rssreader.adapters.viewholders.RssSourceListViewHolder;
import hu.possible.demo.rssreader.models.RssSource;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RssSourceListAdapter extends RecyclerView.Adapter<ViewHolder> {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Context mContext;

    private List<RssSource> mItems = new ArrayList<>();

    private final PublishSubject<RssSource> mOnClickSubject = PublishSubject.create();
    private final PublishSubject<RssSource> mOnShareSubject = PublishSubject.create();
    private final PublishSubject<RssSource> mOnRemoveSubject = PublishSubject.create();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RssSourceListAdapter(Context context) {
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

        View view = mInflater.inflate(R.layout.rss_source_list_item, viewGroup, false);
        RssSourceListViewHolder rssSourceListViewHolder = new RssSourceListViewHolder(view);

        view.setTag(rssSourceListViewHolder);

        return rssSourceListViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RssSource item = getItemForPosition(position);
        RssSourceListViewHolder rssSourceListViewHolder = (RssSourceListViewHolder) holder;

        rssSourceListViewHolder.getTitle().setText(item.resolveTitle());
        rssSourceListViewHolder.getDescription().setText(item.resolveDescription());
        rssSourceListViewHolder.getMenu().setTag(item);
        rssSourceListViewHolder.getMenu().setOnClickListener(view -> openOptionMenu(view, (RssSource) view.getTag()));

        rssSourceListViewHolder.itemView.setOnClickListener(v -> mOnClickSubject.onNext(item));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// RECYCLE VIEW OVERRIDES - - EMD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void openOptionMenu(View view, final RssSource rssSource) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);

        popup.getMenuInflater().inflate(R.menu.rss_source_list_item_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.rss_source_list_item_share:
                    mOnShareSubject.onNext(rssSource);
                    break;
                case R.id.rss_source_list_item_remove:
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

    private RssSource getItemForPosition(int position) {
        return mItems.get(position);
    }

    public void addItems(List<RssSource> items) {
        mItems.addAll(items);
    }

    public void clearItemsIfAny() {
        if (isEmpty()) {
            return;
        }

        mItems.clear();
    }

    public void replaceItems(List<RssSource> items) {
        clearItemsIfAny();
        addItems(items);
    }

    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    public Observable<RssSource> getOnClickObservable(){
        return mOnClickSubject;
    }

    public Observable<RssSource> getOnShareObservable(){
        return mOnShareSubject;
    }

    public Observable<RssSource> getOnRemoveObservable(){
        return mOnRemoveSubject;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// HELPER METHODS  - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
