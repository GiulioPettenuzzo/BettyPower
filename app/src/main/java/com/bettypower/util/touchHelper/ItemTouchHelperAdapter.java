package com.bettypower.util.touchHelper;

/**
 * interface used to swipe an element in recycler view
 * Created by giuliopettenuzzo on 21/01/18.
 */

public interface ItemTouchHelperAdapter {
        /**
         * Called when user swiped an item ofthe list
         * @param position the position of swiped element in the list
         */
        void onItemSwiped(int position);

}
