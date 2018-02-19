package com.bettypower.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

/**
 * Created by giuliopettenuzzo on 18/02/18.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    /**
     * Constructs a new auto-complete text view with the given context's theme
     * and the supplied attribute set.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            InputMethodManager mgr = (InputMethodManager)

                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            // TODO: Hide your view as you do it in your activity
        }
        return false;
    }
}
