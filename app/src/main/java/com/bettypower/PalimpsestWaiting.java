package com.bettypower;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.renard.ocr.R;


public class PalimpsestWaiting extends AppCompatActivity {

    private TextView matchChargingTextView;
    private TextView internetConnectionTextView;


    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palimpsest_waiting);
        matchChargingTextView = (TextView) findViewById(R.id.match_loading);
        internetConnectionTextView = (TextView) findViewById(R.id.textView5);
        if(isNetworkConnected()){
            internetConnectionTextView.setVisibility(View.GONE);
            animateTextView(1500,100000,matchChargingTextView);

        }else{
            internetConnectionTextView.setVisibility(View.VISIBLE);
            matchChargingTextView.setText(String.format("%s %s %s", getResources().getString(R.string.match_charged_first_part), " 0 ", getResources().getString(R.string.match_charged_second_part)));
        }

    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    public void animateTextView(int initialValue, int finalValue, final TextView  textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(150000);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(String.format("%s %s %s", getResources().getString(R.string.match_charged_first_part), valueAnimator.getAnimatedValue().toString(), getResources().getString(R.string.match_charged_second_part)));

            }
        });
        valueAnimator.start();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
