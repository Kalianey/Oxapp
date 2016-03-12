package com.kalianey.oxapp.utils;

/**
 * Created by kalianey on 12/03/2016.
 */
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public final static int SCROLL_DIRECTION_UP = 0;
    public final static int SCROLL_DIRECTION_DOWN = 1;

    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private int scrollDirection = SCROLL_DIRECTION_UP;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
//        if (!loading && (totalItemCount - visibleItemCount)
//                <= (firstVisibleItem + visibleThreshold)) {
//            // End has been reached
//
//            // Do something
//            current_page++;
//
//            onLoadMore(current_page);
//
//            loading = true;
//        }

        if (!loading) {

            if (scrollDirection == SCROLL_DIRECTION_DOWN && ((totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold))) {
                // End has been reached

                // Do something
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }

            else if (scrollDirection == SCROLL_DIRECTION_UP && ( firstVisibleItem <= visibleThreshold)) {
                // End has been reached

                // Do something
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }
        }
    }

    public abstract void onLoadMore(int current_page);

    public boolean isLoading() {
        return loading;
    }

    public void finishedLoading() {
        this.loading = false;
    }

    public int getScrollDirection() {
        return scrollDirection;
    }

    public void setScrollDirection(int scrollDirection) {
        if (scrollDirection == SCROLL_DIRECTION_DOWN || scrollDirection == SCROLL_DIRECTION_UP)
        { this.scrollDirection = scrollDirection; }
    }

}
