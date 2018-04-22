package com.bettypower.listeners;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

/**
 * Created by giuliopettenuzzo on 26/07/17.
 * in order to get te scroll smoothly in the old version of android
 */

public class PreLoadingLinearLayoutManager extends LinearLayoutManager {
    private int mPages = 1;
    private OrientationHelper mOrientationHelper;

    private boolean isScrollEnabled = true;

    public PreLoadingLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }

    @Override
    public void setOrientation(final int orientation) {
        super.setOrientation(orientation);
        mOrientationHelper = null;
    }

    /**
     * Set the number of pages of layout that will be preloaded off-screen,
     * a page being a pixel measure equivalent to the on-screen size of the
     * recycler view.
     * @param pages the number of pages; can be {@code 0} to disable preloading
     */
    public void setPages(final int pages) {
        this.mPages = pages;
    }

    @Override
    protected int getExtraLayoutSpace(final RecyclerView.State state) {
        if (mOrientationHelper == null) {
            mOrientationHelper = OrientationHelper.createOrientationHelper(this, getOrientation());
        }
        return mOrientationHelper.getTotalSpace() * mPages;
    }
}
