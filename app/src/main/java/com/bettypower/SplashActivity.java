package com.bettypower;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.renard.betty.documents.viewing.grid.DocumentGridActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent;

        intent = getIntent();
        Uri uri = intent.getData();
        if(Intent.ACTION_MAIN.equals(uri) || uri == null){
            intent = new Intent(this, DocumentGridActivity.class);
        }else{
            intent = intent.setClass(this,SingleBetActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
