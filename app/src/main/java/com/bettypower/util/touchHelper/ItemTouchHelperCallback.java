package com.bettypower.util.touchHelper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.bettypower.adapters.SingleBetAdapter;

/**
 * class used to swipe an element in recycler view
 * Created by giuliopettenuzzo on 21/01/18.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private boolean swipeEnabled;

    public ItemTouchHelperCallback(
            ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * set only right and left swipe
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(viewHolder.getItemViewType()!=1) {
            int dragFlags = 0;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder.getItemViewType()!=1) {
            mAdapter.onItemSwiped(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
