package com.bettypower;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.renard.ocr.R;
import com.renard.ocr.documents.viewing.grid.DocumentGridActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, DocumentGridActivity.class);
        startActivity(intent);
        finish();
    }
}
