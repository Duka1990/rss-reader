package hu.possible.demo.rssreader.utils.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(prefix = "m")
public class RegularSpacerItemDecoration extends RecyclerView.ItemDecoration {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private int mSpanCount;

    private int mFirstRowSpacing;

    private int mLastRowSpacing;

    private int mSpacing;

    private boolean mIncludeEdge;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// RecyclerView.ItemDecoration IMPLEMENTATION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // item position
        int position = parent.getChildAdapterPosition(view);

        // item count
        int childCount = parent.getAdapter().getItemCount();

        // item column
        int column = position % mSpanCount;
        boolean lastRow = position >= (childCount - (mSpanCount - (childCount % mSpanCount)));

        if (mIncludeEdge) {
            // mSpacing - column * ((1f / mSpanCount) * mSpacing)
            outRect.left = mSpacing - column * mSpacing / mSpanCount;
            // (column + 1) * ((1f / mSpanCount) * mSpacing)
            outRect.right = (column + 1) * mSpacing / mSpanCount;

            // top edge
            if (position < mSpanCount) {
                outRect.top = mFirstRowSpacing;
            }

            // item bottom
            if (lastRow) {
                outRect.bottom = mLastRowSpacing;
            } else {
                outRect.bottom = mSpacing;
            }
        } else {
            // column * ((1f / mSpanCount) * mSpacing)
            outRect.left = column * mSpacing / mSpanCount;
            // mSpacing - (column + 1) * ((1f /    mSpanCount) * mSpacing)
            outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount;

            if (position >= mSpanCount) {
                // item top
                outRect.top = mSpacing;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// RecyclerView.ItemDecoration IMPLEMENTATION - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
