package hu.possible.demo.rssreader.adapters.viewholders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.possible.demo.rssreader.R;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class RssSourceListViewHolder extends ViewHolder {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Getter
    @BindView(R.id.rssSourceList_item_title)
    TextView mTitle;

    @Getter
    @BindView(R.id.rssSourceList_item_description)
    TextView mDescription;

    @Getter
    @BindView(R.id.rssSourceList_item_menu)
    TextView mMenu;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RssSourceListViewHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTION - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
