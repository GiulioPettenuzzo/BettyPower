package com.bettypower;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.renard.ocr.documents.viewing.grid.DocumentGridActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, DocumentGridActivity.class);
        startActivity(intent);
        finish();
    }
}
